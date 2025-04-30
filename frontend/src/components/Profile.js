// src/Profile.js
import React, { useState, useEffect } from 'react';
import axios from '../axiosSetup';
import DeleteAccount from './DeleteAccount';
import { getAuth } from 'firebase/auth';

function Profile({ userId, setUserId }) {
  const [confirmedMatches, setConfirmedMatches] = useState([]);
  const [pendingMatches, setPendingMatches] = useState([]);
  const [userInfo, setUserInfo] = useState({});
  const [error, setError] = useState('');

  const auth = getAuth();
  const userEmail = auth.currentUser ? auth.currentUser.email : '';

  useEffect(() => {
    if (userId) {
      axios.get(`/api/matches/profile/${userId}`)
        .then(response => {
          setConfirmedMatches(response.data.confirmedMatches);
          setPendingMatches(response.data.pendingMatches);
          setError('');
        })
        .catch(err => {
          setError(err.response?.data || 'Error fetching profile matches.');
        });

      // Fetch User Info
      axios.get(`/api/user/${userId}`)
        .then(response => {
          setUserInfo(response.data);
          setError('');
        })
        .catch(err => {
          setError(err.response?.data || 'Error fetching user information.');
        });
    }
  }, [userId]);

  const getUsername = (user) => {
    if (user) {
      if (user.name && user.name.trim() !== "") {
        return user.name;
      } else if (user.email) {
        return user.email.split('@')[0];
      }
    }
    return "Unknown User";
  };

  const styles = {
    container: {
      maxWidth: '700px',
      margin: '2rem auto',
      padding: '2rem',
      backgroundColor: '#ffffff',
      boxShadow: '0 8px 16px rgba(0,0,0,0.1)',
      borderRadius: '16px',
      fontFamily: 'Arial, sans-serif',
      color: '#2c6e6a'
    },
    heading: {
      textAlign: 'center',
      marginBottom: '1.5rem',
    },
    sectionTitle: {
      marginTop: '2rem',
      marginBottom: '1rem',
      color: '#2c6e6a',
      borderBottom: '2px solid #5ccdc1',
      display: 'inline-block',
      paddingBottom: '0.3rem'
    },
    userInfoCard: {
      backgroundColor: '#e0f7fa',
      borderRadius: '12px',
      padding: '1rem',
      marginBottom: '0.8rem',
      boxShadow: '0 4px 8px rgba(0,0,0,0.05)'
    },
    matchCard: {
      backgroundColor: '#f5f5f5',
      borderRadius: '12px',
      padding: '1rem',
      marginBottom: '0.8rem',
      boxShadow: '0 4px 8px rgba(0,0,0,0.05)'
    },
    matchName: {
      margin: '0',
      fontWeight: 'bold',
      fontSize: '1rem',
      color: '#333'
    },
    matchEmail: {
      margin: '0.25rem 0 0',
      fontSize: '0.9rem',
      color: '#555'
    },
    noMatches: {
      fontStyle: 'italic',
      color: '#999'
    },
    error: {
      color: 'red',
      textAlign: 'center',
      marginTop: '1rem'
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Your Profile</h2>

      {error && <p style={styles.error}>{error}</p>}

      <div style={styles.userInfoCard}>
        <p><strong>Name:</strong> {userInfo.name || 'N/A'}</p>
        <p><strong>Email:</strong> {userInfo.email || 'N/A'}</p>
        <p><strong>Major:</strong> {userInfo.major || 'N/A'}</p>
        <p><strong>Year:</strong> {userInfo.year || 'N/A'}</p>
      </div>

      <h3 style={styles.sectionTitle}>People Matched With</h3>
      {confirmedMatches.length > 0 ? (
        confirmedMatches.map(user => (
          <div key={user.id} style={styles.matchCard}>
            <p style={styles.matchName}>{getUsername(user)}</p>
            <p style={styles.matchEmail}>{user.email}</p>
          </div>
        ))
      ) : (
        <p style={styles.noMatches}>No confirmed matches yet.</p>
      )}

      <h3 style={styles.sectionTitle}>Matches in Progress</h3>
      {pendingMatches.length > 0 ? (
        pendingMatches.map(user => (
          <div key={user.id} style={styles.matchCard}>
            <p style={styles.matchName}>{getUsername(user)}</p>
          </div>
        ))
      ) : (
        <p style={styles.noMatches}>No pending matches.</p>
      )}

      <DeleteAccount userEmail={userEmail} setUserId={setUserId} />
    </div>
  );
}

export default Profile;
