// src/App.js
import React, { useState } from 'react';
import { Routes, Route, Link, Navigate } from 'react-router-dom';
import './App.css';

import Landing from './components/Landing';
import Signup from './components/Signup';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import UpdatePreference from './components/UpdatePreference';
import MatchList from './components/MatchList';
import Profile from './components/Profile';

function App() {
  const [userId, setUserId] = useState(null);

  return (
    <div>
      <div className="header">
        <img src="/logo.png" alt="StudBud Logo" />
        <h1>StudBud</h1>
      </div>

      {userId && (
        <nav>
          <ul>
            <li><Link to="/dashboard">Dashboard</Link></li>
            <li><Link to="/profile">Profile</Link></li>
            <li><Link to="/matchlist">Match List</Link></li>
            <li><Link to="/update">Update Preference</Link></li>
            <li>
              <button onClick={() => setUserId(null)}>Logout</button>
            </li>
          </ul>
        </nav>
      )}

      <div className="container">
        <Routes>
          <Route path="/" element={userId ? <Navigate to="/dashboard" /> : <Landing />} />
          <Route path="/signup" element={<Signup setUserId={setUserId} />} />
          <Route path="/login" element={<Login setUserId={setUserId} />} />
          <Route path="/dashboard" element={userId ? <Dashboard userId={userId} /> : <Navigate to="/login" />} />
          <Route path="/update" element={userId ? <UpdatePreference userId={userId} /> : <Navigate to="/login" />} />
          <Route path="/matchlist" element={userId ? <MatchList userId={userId} /> : <Navigate to="/login" />} />
          <Route path="/profile" element={userId ? <Profile userId={userId} /> : <Navigate to="/login" />} />
        </Routes>
      </div>
    </div>
  );
}

export default App;