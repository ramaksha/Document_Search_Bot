import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import '../css/style.css';

const Header = ({ username, role }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <header className="app-header">
      <h1>Document Search Bot</h1>

      {username && role && (
        <>
          <h2 className="username-text">Welcome to {role} page, {username}</h2>
          <nav>
            {role === 'ADMIN' && (
              <>
                <Link to="/users" className="nav-btn small-btn">Manage Users</Link>
                <Link to="/document" className="nav-btn small-btn">Manage Documents</Link>
              </>
            )}
            <Link to="/home" className="nav-btn small-btn">Chat</Link>
            <button onClick={handleLogout} className="nav-btn small-btn">Logout</button>
          </nav>
        </>
      )}
    </header>
  );
};

export default Header;