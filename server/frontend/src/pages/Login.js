import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import './Login.css';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      await AuthService.login(email, password);
      navigate('/');
    } catch (err) {
      setError('Invalid email or password');
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <h1 className="facebook-logo">facebook</h1>
        <form onSubmit={handleLogin}>
          <input
            type="text"
            placeholder="Mobile number or email address"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          {error && <div className="error-message">{error}</div>}
          <button type="submit" className="login-button">
            Log In
          </button>
          <div className="forgot-password">
            <a href="/forgot-password">Forgotten password?</a>
          </div>
          <div className="divider">
            <span>or</span>
          </div>
          <button
            type="button"
            className="create-account-button"
            onClick={() => navigate('/register')}
          >
            Create New Account
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login; 