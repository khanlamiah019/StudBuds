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

  return (
    <div>
      {/* Header */}
      <div className="header">
        <img src="/logo.png" alt="StudBud Logo" />
        <h1>StudBud</h1>
      </div>

      {userId && (
        <nav>
          <ul>
            <li><Link to="/matchlist">Match List</Link></li>
            <li><Link to="/update">Update Preferences</Link></li>
            <li><Link to="/profile">Profile</Link></li>
            <li>
              <button onClick={() => {
                setUserId(null);
                navigate('/');  // Redirect to Landing page on logout
              }}>
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