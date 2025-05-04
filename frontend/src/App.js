// src/App.js
import React, { useEffect, useState } from 'react';
import { Routes, Route, Link, Navigate, useNavigate } from 'react-router-dom';
import { getAuth, onAuthStateChanged, signOut } from 'firebase/auth';
import axios from './axiosSetup'; // ✅ use axios here
import './App.css';

import Landing from './components/Landing';
import Signup from './components/Signup';
import Login from './components/Login';
import UpdatePreference from './components/UpdatePreference';
import MatchList from './components/MatchList';
import Profile from './components/Profile';

function App() {
  const [userId, setUserId] = useState(null);
  const [loading, setLoading] = useState(true); // ✅ show loading state during auth check
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const auth = getAuth();
    const unsubscribe = onAuthStateChanged(auth, async (user) => {
      if (user) {
        try {
          const token = await user.getIdToken();

          const res = await axios.post('/api/auth/login', 
            { firebaseToken: token },
            { headers: { Authorization: `Bearer ${token}` } }
          );

          if (res.data && res.data.userId) {
            setUserId(res.data.userId);
          } else {
            setUserId(null);
          }
        } catch (error) {
          console.error("Login sync failed:", error);
          setUserId(null);
        }
      } else {
        setUserId(null);
      }

      setLoading(false); // ✅ auth check complete
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

  if (loading) {
  return (
    <div className="loading-screen">
      <div className="fish-tank">
        <div className="fish">🐡</div>
      </div>
      <p>Splashing in... just a sec!</p>
    </div>
  );
}

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
