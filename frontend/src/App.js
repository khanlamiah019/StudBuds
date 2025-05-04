// src/App.js
import React, { useState } from 'react';
import { Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom';
import './App.css';

import Landing from './components/Landing';
import Signup from './components/Signup';
import Login from './components/Login';
import UpdatePreference from './components/UpdatePreference';
import MatchList from './components/MatchList';
import Profile from './components/Profile';

function App() {
  const [userId, setUserId] = useState(null);
  const navigate = useNavigate();

  // State for toggling mobile menu
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    setUserId(null);
    navigate('/');
    setMenuOpen(false); // Close menu on logout
  };

  return (
    <div>
      {/* Header */}
      <div className="header">
        <img src="/logo.png" alt="StudBud Logo" />
        <h1>StudBuds</h1>
      </div>

      {userId && (
        <nav>
          {/* Hamburger icon (shows on mobile) */}
          <div 
            className={hamburger ${menuOpen ? 'active' : ''}} 
            onClick={() => setMenuOpen((prev) => !prev)}
          >
            <span></span>
            <span></span>
            <span></span>
          </div>

          {/* Nav links */}
          <ul className={menuOpen ? 'menu-open' : ''}>
            <li><Link to="/matchlist" onClick={() => setMenuOpen(false)}>Match List</Link></li>
            <li><Link to="/update" onClick={() => setMenuOpen(false)}>Update Preferences</Link></li>
            <li><Link to="/profile" onClick={() => setMenuOpen(false)}>Profile</Link></li>
            <li>
              <button onClick={handleLogout}>
                Logout
              </button>
            </li>
          </ul>
        </nav>
      )}

      <div className="container">
        <Routes>
          {/* Redirect to matchlist if user is signed in */}
          <Route path="/" element={userId ? <Navigate to="/matchlist" /> : <Landing />} />
          <Route path="/signup" element={<Signup setUserId={setUserId} />} />
          <Route path="/login" element={<Login setUserId={setUserId} />} />
          <Route path="/matchlist" element={userId ? <MatchList userId={userId} /> : <Navigate to="/login" />} />
          <Route path="/update" element={userId ? <UpdatePreference userId={userId} /> : <Navigate to="/login" />} />
          <Route path="/profile" element={userId ? <Profile userId={userId} setUserId={setUserId} /> : <Navigate to="/login" />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;
