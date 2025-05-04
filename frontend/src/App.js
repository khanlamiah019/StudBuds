// src/App.js
import React, { useEffect, useState } from 'react';
import { Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom';
import { getAuth, onAuthStateChanged, signOut } from 'firebase/auth';
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
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const auth = getAuth();
    const unsubscribe = onAuthStateChanged(auth, async (user) => {
      if (user) {
        const token = await user.getIdToken();
        const res = await fetch("/api/auth/login", {
          method: "POST",
          headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
          },
          body: JSON.stringify({ firebaseToken: token })
        });

        if (res.ok) {
          const data = await res.json();
          setUserId(data.userId);
        } else {
          setUserId(null);
        }
      } else {
        setUserId(null);
      }
    });

    return () => unsubscribe();
  }, []);

  const handleLogout = () => {
    signOut(getAuth()).then(() => {
      setUserId(null);
      navigate('/');
      setMenuOpen(false);
    });
  };

  return (
    <div>
      <div className="header">
        <img src="/logo.png" alt="StudBud Logo" />
        <h1>StudBuds</h1>
      </div>

      {userId && (
        <nav>
          <div 
            className={`hamburger ${menuOpen ? 'active' : ''}`} 
            onClick={() => setMenuOpen((prev) => !prev)}
          >
            <span></span><span></span><span></span>
          </div>
          <ul className={menuOpen ? 'menu-open' : ''}>
            <li><Link to="/matchlist" onClick={() => setMenuOpen(false)}>Match List</Link></li>
            <li><Link to="/update" onClick={() => setMenuOpen(false)}>Update Preferences</Link></li>
            <li><Link to="/profile" onClick={() => setMenuOpen(false)}>Profile</Link></li>
            <li><button onClick={handleLogout}>Logout</button></li>
          </ul>
        </nav>
      )}

      <div className="container">
        <Routes>
          <Route path="/" element={userId ? <Navigate to="/matchlist" /> : <Landing />} />
          <Route path="/signup" element={<Signup />} />
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
