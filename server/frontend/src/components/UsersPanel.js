import React, { useState, useEffect } from 'react';
import AuthService from '../services/AuthService';
import './UsersPanel.css';

// DEBUG: vezi structura userului curent
console.log('DEBUG AuthService.getCurrentUser():', AuthService.getCurrentUser());

function UsersPanel() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const currentUser = AuthService.getCurrentUser();
    if (!currentUser || !currentUser.token) {
      setError("Nu sunteți autentificat. Vă rugăm să vă conectați.");
      setLoading(false);
      return;
    }
    fetchUsers(currentUser);
  }, []);

  const fetchUsers = async (currentUser) => {
    setLoading(true);
    setError(null);
    try {
      // 1. Ia userul curent cu id din backend
      const meResponse = await fetch('http://localhost:8081/user/me', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${currentUser.token}`,
          'Content-Type': 'application/json'
        }
      });
      if (!meResponse.ok) {
        throw new Error('Nu s-au putut obține datele userului curent');
      }
      const me = await meResponse.json();

      // 2. Ia toți userii
      const usersResponse = await fetch('http://localhost:8081/user', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${currentUser.token}`,
          'Content-Type': 'application/json'
        }
      });
      if (!usersResponse.ok) {
        const errorData = await usersResponse.text();
        throw new Error(errorData || 'Eroare la încărcarea utilizatorilor');
      }
      const allUsers = await usersResponse.json();

      // 3. Ia prietenii userului curent
      const friendsResponse = await fetch('http://localhost:8081/friend/mutual', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${currentUser.token}`,
          'Content-Type': 'application/json'
        }
      });
      if (!friendsResponse.ok) {
        const errorData = await friendsResponse.text();
        throw new Error(errorData || 'Eroare la încărcarea prietenilor');
      }
      const friends = await friendsResponse.json();
      const friendIds = friends.map(friend => friend.id);

      // 4. Ia pending requests
      const pendingResponse = await fetch('http://localhost:8081/friend/pending', {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${currentUser.token}`,
          'Content-Type': 'application/json'
        }
      });
      if (!pendingResponse.ok) {
        const errorData = await pendingResponse.text();
        throw new Error(errorData || 'Eroare la încărcarea pending requests');
      }
      const pending = await pendingResponse.json();
      const pendingIds = pending.map(u => u.id);

      // 5. Filtrează și marchează requestSent
      const filteredUsers = allUsers
        .filter(user => Number(user.id) !== Number(me.id) && !friendIds.includes(user.id))
        .map(user => ({
          ...user,
          requestSent: pendingIds.includes(user.id)
        }));
      setUsers(filteredUsers);
    } catch (err) {
      console.error('Error fetching users:', err);
      setError(err.message || "A apărut o eroare la încărcarea utilizatorilor. Vă rugăm să încercați din nou.");
    } finally {
      setLoading(false);
    }
  };

  const handleAddFriend = async (userId) => {
    try {
      const currentUser = AuthService.getCurrentUser();
      const response = await fetch(`http://localhost:8081/friend/add/${userId}`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${currentUser.token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        const errorData = await response.text();
        throw new Error(errorData || 'Eroare la trimiterea cererii de prietenie');
      }

      // Update the UI to show the request was sent
      setUsers(users.map(user => 
        user.id === userId 
          ? { ...user, requestSent: true }
          : user
      ));
    } catch (err) {
      console.error('Error sending friend request:', err);
      setError(err.message || "A apărut o eroare la trimiterea cererii de prietenie");
    }
  };

  if (loading) {
    return <div className="users-panel"><p>Se încarcă utilizatorii...</p></div>;
  }

  if (error) {
    return <div className="users-panel"><p className="error-message">Eroare: {error}</p></div>;
  }

  return (
    <div className="users-panel">
      <h2>Persoane pe care le poți cunoaște</h2>
      <div className="users-list">
        {users.length === 0 ? (
          <p>Nu s-au găsit utilizatori</p>
        ) : (
          users.map(user => (
            <div key={user.id} className={`user-card${user.requestSent ? ' pending' : ''}`}>
              <div className="user-info">
                <div className="user-avatar">
                  {user.url_photo ? (
                    <img src={user.url_photo} alt={user.username} />
                  ) : (
                    <div className="avatar-placeholder">
                      <i className="fas fa-user"></i>
                    </div>
                  )}
                </div>
                <div className="user-details">
                  <h3>{user.username}</h3>
                  <p>{user.email}</p>
                </div>
              </div>
              <button
                className={`add-friend-btn ${user.requestSent ? 'sent' : ''}`}
                onClick={() => handleAddFriend(user.id)}
                disabled={user.requestSent}
              >
                {user.requestSent ? 'Cerere trimisă' : 'Adaugă prieten'}
              </button>
            </div>
          ))
        )}
      </div>
    </div>
  );
}

export default UsersPanel; 
