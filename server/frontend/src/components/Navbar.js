import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthService from '../services/AuthService';
import './Navbar.css';

function Navbar() {
  const navigate = useNavigate();
  const user = AuthService.getCurrentUser();

  const handleLogout = () => {
    AuthService.logout();
    navigate('/login');
  };

  if (!user) {
    return null;
  }

  return (
    <nav className="navbar">
        <div className="navbar-left">
        <Link to="/" className="logo">
          <h1>facebook</h1>
        </Link>
        </div>

        <div className="navbar-right">
        <div className="nav-icons">
          <Link to="/" className="nav-item">
            <i className="fas fa-home"></i>
            <span>Home</span>
          </Link>
          <Link to="/profile" className="nav-item">
            <i className="fas fa-user"></i>
            <span>Profile</span>
          </Link>
          <div className="nav-item" onClick={handleLogout}>
            <i className="fas fa-sign-out-alt"></i>
            <span>Logout</span>
          </div>
        </div>
      </div>
    </nav>
  );
}

export default Navbar; 