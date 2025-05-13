import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import './Profile.css';

function Profile() {
  const navigate = useNavigate();
  const [userData, setUserData] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [imageBlobUrl, setImageBlobUrl] = useState(null);

  useEffect(() => {
    const user = AuthService.getCurrentUser();
    if (user) {
      setUserData(user);
      if (user.url_photo) {
        // Fetch imaginea cu JWT
        const fetchImage = async () => {
          try {
            const response = await fetch(`http://localhost:8081/user/photo/${user.url_photo}`, {
              headers: {
                'Authorization': `Bearer ${user.token}`
              }
            });
            if (response.ok) {
              const blob = await response.blob();
              setImageBlobUrl(URL.createObjectURL(blob));
            } else {
              setImageBlobUrl(null);
            }
          } catch (err) {
            setImageBlobUrl(null);
          }
        };
        fetchImage();
      }
    }
  }, []);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setSelectedFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewUrl(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleUpload = async () => {
    if (!selectedFile) return;

    setIsUploading(true);
    const formData = new FormData();
    formData.append('photo', selectedFile);

    try {
      const user = AuthService.getCurrentUser();
      const response = await fetch('http://localhost:8081/user/photo', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${user?.token}`
        },
        body: formData
      });

      if (response.ok) {
        const data = await response.json();
        if (data.user) {
          const updatedUser = { ...user, url_photo: data.user.url_photo };
          localStorage.setItem('user', JSON.stringify(updatedUser));
          setUserData(updatedUser);
          // Refetch imaginea nouă
          const imgResp = await fetch(`http://localhost:8081/user/photo/${data.user.url_photo}`, {
            headers: {
              'Authorization': `Bearer ${user.token}`
            }
          });
          if (imgResp.ok) {
            const blob = await imgResp.blob();
            setImageBlobUrl(URL.createObjectURL(blob));
          } else {
            setImageBlobUrl(null);
          }
          setPreviewUrl(null);
        }
      } else {
        console.error('Upload failed:', await response.text());
      }
    } catch (error) {
      console.error('Error uploading photo:', error);
    } finally {
      setIsUploading(false);
      setSelectedFile(null);
    }
  };

  if (!userData) {
    return <div className="profile-container">Loading...</div>;
  }

  return (
    <div className="profile-container">
      <div className="profile-header">
        <div className="profile-picture-row">
          <div className="profile-picture-container">
            {previewUrl ? (
              <img src={previewUrl} alt="Profile" className="profile-picture" />
            ) : imageBlobUrl ? (
              <img src={imageBlobUrl} alt="Profile" className="profile-picture" />
            ) : (
              <div className="profile-picture-placeholder">
                <i className="fas fa-user"></i>
              </div>
            )}
          </div>
          <div className="profile-picture-upload profile-picture-upload-side">
            <label htmlFor="photo-upload" className="upload-button small-upload-button">
              <i className="fas fa-camera"></i>
            </label>
            <input
              id="photo-upload"
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              style={{ display: 'none' }}
            />
            {selectedFile && (
              <button 
                className="save-photo-button small-save-photo-button"
                onClick={handleUpload}
                disabled={isUploading}
              >
                {isUploading ? 'Uploading...' : 'Save'}
              </button>
            )}
          </div>
        </div>
      </div>

      <div className="profile-content">
        <div className="profile-section">
          <h2>About</h2>
          <div className="info-item">
            <i className="fas fa-envelope"></i>
            <span>Email: {userData.user?.email || userData.email || 'Not provided'}</span>
          </div>
          <div className="info-item">
            <i className="fas fa-user"></i>
            <span>Username: {userData.user?.username || userData.username || 'Not provided'}</span>
          </div>
          <div className="info-item">
            <i className="fas fa-calendar"></i>
            <span>Member since: {new Date(userData.user?.created_at || userData.created_at).toLocaleDateString('en-US', { day: 'numeric', month: 'long', year: 'numeric' })}</span>
          </div>
        </div>

        <div className="profile-section">
          <h2>Activity</h2>
          <div className="activity-stats">
            <div className="stat-item">
              <span className="stat-number">0</span>
              <span className="stat-label">Posts</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">0</span>
              <span className="stat-label">Friends</span>
            </div>
            <div className="stat-item">
              <span className="stat-number">0</span>
              <span className="stat-label">Photos</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile; 