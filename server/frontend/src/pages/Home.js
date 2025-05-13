import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import './Home.css';

function Home() {
  const navigate = useNavigate();
  const [userData, setUserData] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [newPost, setNewPost] = useState({
    title: '',
    text: '',
    photo: null
  });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [posts, setPosts] = useState([]);
  const [avatarUrls, setAvatarUrls] = useState({});

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setUserData(user);
      fetchPosts();
    }
  }, []);

  const fetchPosts = async () => {
    try {
      const user = AuthService.getCurrentUser();
      const response = await fetch('http://localhost:8081/content', {
        headers: {
          'Authorization': `Bearer ${user?.token}`
        }
      });
      if (response.ok) {
        const data = await response.json();
        setPosts(data);
        // Fetch avatars for all posts
        fetchAvatars(data);
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
    }
  };

  const fetchAvatars = async (postsData) => {
    const user = AuthService.getCurrentUser();
    const newAvatarUrls = {};
    await Promise.all(postsData.map(async (post) => {
      if (post.user && post.user.url_photo) {
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
        title: newPost.title,
        text: newPost.text,
        typeContent: true,
        status: "active",
        nrComments: 0,
        nrVotes: 0,
        votes: [],
        tags: []
      },
      tag: []
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
        setNewPost({ title: '', text: '', photo: null });
        setPhotoPreview(null);
        setShowModal(false);
        fetchPosts();
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
    setNewPost({ title: '', text: '', photo: null });
    setPhotoPreview(null);
  };

  if (!userData) {
    return <div>Loading...</div>;
  }

  return (
    <div className="home-container">
      <div className="feed-container">
        <div className="create-post-button-container">
          <button className="create-post-button" onClick={() => setShowModal(true)}>
            <i className="fas fa-plus"></i>
            Create Post
          </button>
        </div>

        <div className="posts-feed">
          {posts.map(post => (
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
                      {new Date(post.createdAt).toLocaleDateString()}
                    </span>
                  </div>
                </div>
              </div>
              <div className="post-content">
                <h3>{post.title}</h3>
                <p>{post.text}</p>
                {post.urlPhoto && (
                  <div className="post-image-container">
                    <img src={post.urlPhoto} alt="Post" className="post-image" />
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
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
                <input
                  type="text"
                  name="title"
                  placeholder="Post title..."
                  value={newPost.title}
                  onChange={handleInputChange}
                  required
                />
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