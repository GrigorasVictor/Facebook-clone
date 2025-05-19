import React, { useEffect, useState } from 'react';
import './CommentModal.css';

function CommentModal({ open, onClose, postId, userToken, onCommentAdded }) {
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState(null);
  const [photo, setPhoto] = useState(null);
  const [photoPreview, setPhotoPreview] = useState(null);
  const [imageModal, setImageModal] = useState({ open: false, src: null });

  useEffect(() => {
    if (open && postId) {
      fetchComments();
    }
    // eslint-disable-next-line
  }, [open, postId]);

  const fetchComments = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/content/${postId}/comments`, {
        headers: { 'Authorization': `Bearer ${userToken}` }
      });
      if (!response.ok) throw new Error('Eroare la încărcarea comentariilor');
      const data = await response.json();
      setComments(data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleAddComment = async (e) => {
    e.preventDefault();
    if (!newComment.trim()) return;
    setSubmitting(true);
    setError(null);
    try {
      const formData = new FormData();
      const contentData = {
        content: {
          text: newComment,
          typeContent: false,
          status: 'active',
          parentContentId: postId,
          nrComments: 0,
          nrVotes: 0,
          votes: [],
          tags: []
        },
        tag: []
      };
      formData.append('content', new Blob([JSON.stringify(contentData)], { type: 'application/json' }));
      if (photo) {
        formData.append('photo', photo);
      } else {
        formData.append('photo', new Blob([''], { type: 'image/png' }));
      }
      const response = await fetch('http://localhost:8081/content', {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${userToken}` },
        body: formData
      });
      if (!response.ok) throw new Error('Eroare la adăugarea comentariului');
      setNewComment('');
      setPhoto(null);
      setPhotoPreview(null);
      fetchComments();
      if (onCommentAdded) onCommentAdded();
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setPhoto(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setPhotoPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  if (!open) return null;

  return (
    <div className="comment-modal-overlay" onClick={onClose}>
      <div className="comment-modal-content" onClick={e => e.stopPropagation()}>
        <div className="comment-modal-header">
          <h3>Comentarii</h3>
          <button className="close-button" onClick={onClose}>&times;</button>
        </div>
        <div className="comment-modal-body">
          {loading ? (
            <div className="comment-loading">Se încarcă...</div>
          ) : error ? (
            <div className="comment-error">{error}</div>
          ) : comments.length === 0 ? (
            <div className="comment-empty">Nu există comentarii.</div>
          ) : (
            <ul className="comment-list">
              {comments.map(comment => (
                <li key={comment.id} className="comment-item">
                  <div className="comment-user">{comment.user?.username || 'Anonim'}</div>
                  <div className="comment-text">{comment.text}</div>
                  {comment.urlPhoto && (
                    <div className="comment-image-container" style={{ justifyContent: 'center' }}>
                      <img
                        src={comment.urlPhoto}
                        alt="Comentariu"
                        className="comment-image"
                        style={{ cursor: 'pointer' }}
                        onClick={() => setImageModal({ open: true, src: comment.urlPhoto })}
                      />
                    </div>
                  )}
                  <div className="comment-date">{comment.createdAt ? new Date(comment.createdAt).toLocaleString('ro-RO', { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit' }) : ''}</div>
                </li>
              ))}
            </ul>
          )}
        </div>
        <form className="comment-form" onSubmit={handleAddComment}>
          <input
            type="text"
            placeholder="Scrie un comentariu..."
            value={newComment}
            onChange={e => setNewComment(e.target.value)}
            disabled={submitting}
            maxLength={300}
          />
          <label className="comment-photo-upload">
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              disabled={submitting}
              style={{ display: 'none' }}
            />
            <span role="img" aria-label="Adaugă poză" style={{ cursor: 'pointer', fontSize: 18, color: photoPreview ? '#1877f2' : '#65676b' }}>📷</span>
          </label>
          {photoPreview && (
            <div className="comment-photo-preview">
              <img src={photoPreview} alt="Preview" />
              <button
                type="button"
                className="remove-photo"
                onClick={() => { setPhoto(null); setPhotoPreview(null); }}
                tabIndex={-1}
              >
                <i className="fas fa-times"></i>
              </button>
            </div>
          )}
          <button type="submit" disabled={submitting || !newComment.trim()}>
            {submitting ? 'Se trimite...' : 'Trimite'}
          </button>
        </form>
      </div>
      {imageModal.open && (
        <div className="comment-image-modal-overlay" onClick={() => setImageModal({ open: false, src: null })}>
          <div className="comment-image-modal-content" onClick={e => e.stopPropagation()}>
            <img src={imageModal.src} alt="Preview mare" />
            <button className="close-button" onClick={() => setImageModal({ open: false, src: null })}>&times;</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default CommentModal; 