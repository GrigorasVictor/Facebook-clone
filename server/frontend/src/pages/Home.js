import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import UsersPanel from '../components/UsersPanel';
import CommentModal from '../components/CommentModal';
import './Home.css';

function Home() {
  const navigate = useNavigate();
  const [userData, setUserData] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [newPost, setNewPost] = useState({
    text: '',
    photo: null
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [posts, setPosts] = useState([]);
  const [avatarUrls, setAvatarUrls] = useState({});
  const [tagInput, setTagInput] = useState('');
  const [tags, setTags] = useState([]);
  const [allTags, setAllTags] = useState([]);
  const [selectedTag, setSelectedTag] = useState(null);
  const [cursor, setCursor] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [friendIds, setFriendIds] = useState([]);
  const [commentModalOpen, setCommentModalOpen] = useState(false);
  const [activePostId, setActivePostId] = useState(null);

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setUserData(user);
      fetchPosts(0, true);
    }
  }, []);

  useEffect(() => {
    const fetchTags = async () => {
      try {
        const user = AuthService.getCurrentUser();
        const response = await fetch('http://localhost:8081/tag', {
          headers: {
            'Authorization': `Bearer ${user?.token}`
          }
        });
        if (response.ok) {
          const data = await response.json();
          setAllTags(data);
        }
      } catch (error) {
        console.error('Error fetching tags:', error);
      }
    };
    fetchTags();
  }, []);

  useEffect(() => {
    // Fetch friend ids
    const fetchFriends = async () => {
      const user = AuthService.getCurrentUser();
      if (!user?.token) return;
      const response = await fetch('http://localhost:8081/friend/mutual', {
        headers: { 'Authorization': `Bearer ${user.token}` }
      });
      if (response.ok) {
        const friends = await response.json();
        setFriendIds(friends.map(f => f.id));
      }
    };
    fetchFriends();
  }, []);

  const fetchPosts = async (page = 0, initial = false) => {
    try {
      const user = AuthService.getCurrentUser();
      const response = await fetch(`http://localhost:8081/content/from/${page}`, {
        headers: {
          'Authorization': `Bearer ${user?.token}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        if (initial) {
          setPosts(data);
          setCursor(1);
          setHasMore(data.length === 5);
          fetchAvatars(data);
        } else {
          setPosts(prev => [...prev, ...data]);
          setCursor(prev => prev + 1);
          setHasMore(data.length === 5);
          fetchAvatars([...posts, ...data]);
        }
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
    } finally {
      setLoadingMore(false);
    }
  };

  const fetchAvatars = async (postsData) => {
    const user = AuthService.getCurrentUser();
    const newAvatarUrls = {};
    await Promise.all(postsData.map(async (post) => {
      if (post.user && post.user.url_photo) {
        if (post.user.url_photo.startsWith('http://') || post.user.url_photo.startsWith('https://')) {
          newAvatarUrls[post.id] = post.user.url_photo;
        } else {
        try {
          const response = await fetch(`http://localhost:8081/user/photo/${post.user.url_photo}`, {
            headers: {
              'Authorization': `Bearer ${user?.token}`
            }
          });
          if (response.ok) {
            const blob = await response.blob();
            newAvatarUrls[post.id] = URL.createObjectURL(blob);
          }
        } catch (err) {
          newAvatarUrls[post.id] = null;
          }
        }
      }
    }));
    setAvatarUrls(newAvatarUrls);
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewPost(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setNewPost(prev => ({
        ...prev,
        photo: file
      }));
      // Create preview URL for the image
      const reader = new FileReader();
      reader.onloadend = () => {
        setPhotoPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleTagInputChange = (e) => {
    setTagInput(e.target.value);
  };

  const handleTagKeyDown = (e) => {
    if ((e.key === 'Enter' || e.key === ',') && tagInput.trim() !== '') {
      e.preventDefault();
      addTag(tagInput.trim());
    }
  };

  const addTag = (tag) => {
    if (tag && !tags.includes(tag)) {
      setTags([...tags, tag]);
    }
    setTagInput('');
  };

  const removeTag = (tagToRemove) => {
    setTags(tags.filter(tag => tag !== tagToRemove));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    
    const user = AuthService.getCurrentUser();
    if (!user?.token) {
      alert('You must be logged in to create a post');
      setIsSubmitting(false);
      return;
    }

    const formData = new FormData();
    const contentData = {
      content: {
        text: newPost.text,
        typeContent: true,
        status: "active",
        nrComments: 0,
        nrVotes: 0,
        votes: [],
        tags: []
      },
      tag: tags.map(tag => ({ name: tag }))
    };

    formData.append('content', new Blob([JSON.stringify(contentData)], {
      type: 'application/json'
    }));

    if (newPost.photo) {
      formData.append('photo', newPost.photo);
    } else {
      formData.append('photo', new Blob([''], { type: 'image/png' }));
    }

    try {
      const response = await fetch('http://localhost:8081/content', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${user.token}`
        },
        body: formData
      });

      if (response.ok) {
        setNewPost({ text: '', photo: null });
        setPhotoPreview(null);
        setShowModal(false);
        setTags([]);
        setTagInput('');
        fetchPosts(0, true);
      } else {
        const errorText = await response.text();
        console.error('Failed to create post:', errorText);
        alert('Failed to create post: ' + errorText);
      }
    } catch (error) {
      console.error('Error creating post:', error);
      alert('Error creating post: ' + error.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setNewPost({ text: '', photo: null });
    setPhotoPreview(null);
    setTags([]);
    setTagInput('');
  };

  const handleLoadMore = () => {
    setLoadingMore(true);
    fetchPosts(cursor, false);
  };

  // DOAR POSTĂRILE DE LA PRIETENI
  const filteredPosts = selectedTag
    ? posts.filter(post =>
        post.tags && post.tags.some(tag => tag.name === selectedTag) &&
        post.user && (friendIds.includes(post.user.id) || post.user.id === userData.id)
      )
    : posts.filter(post => post.user && (friendIds.includes(post.user.id) || post.user.id === userData.id));

  const sortedPosts = [...filteredPosts].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

  const user = AuthService.getCurrentUser();

  if (!userData) {
    return <div>Loading...</div>;
  }

  return (
    <div className="home-container">
      <div className="home-main-content">
      <div className="feed-container">
        <div className="create-post-button-container">
          <button className="create-post-button" onClick={() => setShowModal(true)}>
            <i className="fas fa-plus"></i>
            Create Post
          </button>
        </div>

          <div className="tag-cloud">
            <button
              className={`tag-cloud-btn${selectedTag === null ? ' selected' : ''}`}
              onClick={() => setSelectedTag(null)}
            >
              Toate
            </button>
            {allTags.map(tag => (
              <button
                key={tag.id}
                className={`tag-cloud-btn${selectedTag === tag.name ? ' selected' : ''}`}
                onClick={() => setSelectedTag(tag.name)}
              >
                {tag.name}
              </button>
            ))}
        </div>

        <div className="posts-feed">
            {sortedPosts.length > 0 ? (
              <>
                {sortedPosts.map(post => (
                <div key={post.id} className="post-card">
                  <div className="post-header">
                    <div className="post-user-info">
                      <div className="user-avatar">
                        {avatarUrls[post.id] ? (
                          <img src={avatarUrls[post.id]} alt={post.user?.username} className="user-avatar-image" />
                        ) : (
                          <i className="fas fa-user"></i>
                        )}
                      </div>
                      <div className="user-details">
                        <span className="username">{post.user?.username || 'Anonymous'}</span>
                        <span className="post-time">
                            {post.createdAt ?
                              new Date(post.createdAt).toLocaleString('ro-RO', {
                                day: '2-digit', month: '2-digit', year: 'numeric',
                                hour: '2-digit', minute: '2-digit'
                              }) : ''}
                        </span>
                        </div>
                      </div>
                    </div>
                    <div className="post-content">
                      <p className="post-description">{post.text}</p>
                      {post.tags && post.tags.length > 0 && (
                        <div className="post-tag-list">
                          {post.tags.map(tag => (
                            <span key={tag.id || tag.name} className="post-tag-item">#{tag.name}</span>
                          ))}
                    </div>
                      )}
                    {post.urlPhoto && (
                      <div className="post-image-container">
                        <img src={post.urlPhoto} alt="Post" className="post-image" />
                      </div>
                    )}
                    <button
                      className="post-comment-btn"
                      onClick={() => { setActivePostId(post.id); setCommentModalOpen(true); }}
                    >
                      <i className="fas fa-comment-alt"></i> Comentarii ({post.nrComments})
                    </button>
                  </div>
                </div>
              ))}
                {hasMore && (
                  <button 
                    className="post-button" 
                    style={{margin: '24px auto 0 auto', display: 'block'}} 
                    onClick={handleLoadMore} 
                    disabled={loadingMore}
                  >
                    {loadingMore ? 'Loading...' : 'Load More'}
                  </button>
                )}
              </>
            ) : (
              <div style={{ textAlign: 'center', padding: '20px', color: '#65676b' }}>
                No posts to display
              </div>
            )}
          </div>
        </div>
        <UsersPanel />
        <CommentModal
          open={commentModalOpen}
          onClose={() => setCommentModalOpen(false)}
          postId={activePostId}
          userToken={user?.token}
          onCommentAdded={() => {
            setPosts(posts =>
              posts.map(post =>
                post.id === activePostId
                  ? { ...post, nrComments: post.nrComments + 1 }
                  : post
              )
            );
          }}
        />
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h2>Create New Post</h2>
              <button className="close-button" onClick={handleCloseModal}>
                <i className="fas fa-times"></i>
              </button>
            </div>
            <form onSubmit={handleSubmit}>
              <div className="post-input">
                <textarea
                  name="text"
                  placeholder="What's on your mind?"
                  value={newPost.text}
                  onChange={handleInputChange}
                  required
                />
                {photoPreview && (
                  <div className="photo-preview">
                    <img src={photoPreview} alt="Preview" />
                    <button 
                      type="button" 
                      className="remove-photo"
                      onClick={() => {
                        setPhotoPreview(null);
                        setNewPost(prev => ({ ...prev, photo: null }));
                      }}
                    >
                      <i className="fas fa-times"></i>
                    </button>
                  </div>
                )}
                <div className="post-actions">
                  <label className="photo-upload">
                    <i className="fas fa-image"></i>
                    <span>{photoPreview ? 'Change Photo' : 'Add Photo'}</span>
                    <input
                      type="file"
                      accept="image/*"
                      onChange={handleFileChange}
                      style={{ display: 'none' }}
                    />
                  </label>
                  <div className="tag-input-container">
                    <input
                      type="text"
                      placeholder="Add tag and press Enter or ,"
                      value={tagInput}
                      onChange={handleTagInputChange}
                      onKeyDown={handleTagKeyDown}
                      disabled={isSubmitting}
                    />
                    <button type="button" onClick={() => addTag(tagInput.trim())} disabled={!tagInput.trim() || isSubmitting}>
                      Add Tag
                    </button>
                  </div>
                  <div className="tag-list">
                    {tags.map(tag => (
                      <span key={tag} className="tag-item">
                        {tag}
                        <button type="button" className="remove-tag" onClick={() => removeTag(tag)}>&times;</button>
                      </span>
                    ))}
                  </div>
                  <button 
                    type="submit" 
                    className={`post-button ${isSubmitting ? 'submitting' : ''}`}
                    disabled={isSubmitting}
                  >
                    {isSubmitting ? (
                      <>
                        <i className="fas fa-spinner fa-spin"></i>
                        Posting...
                      </>
                    ) : (
                      'Post'
                    )}
                  </button>
                </div>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Home; 