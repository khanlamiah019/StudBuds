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

  const [confirmedPage, setConfirmedPage] = useState(0);
  const confirmedPageSize = 5;
  const totalConfirmedPages = Math.ceil(confirmedMatches.length / confirmedPageSize);
  const visibleConfirmed = confirmedMatches.slice(
    confirmedPage * confirmedPageSize,
    (confirmedPage + 1) * confirmedPageSize
  );

  const [pendingPage, setPendingPage] = useState(0);
  const pendingPageSize = 5;
  const totalPendingPages = Math.ceil(pendingMatches.length / pendingPageSize);
  const visiblePending = pendingMatches.slice(
    pendingPage * pendingPageSize,
    (pendingPage + 1) * pendingPageSize
  );

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
      if (user.name && user.name.trim() !== "") return user.name;
      if (user.email) return user.email.split('@')[0];
    }
    return "Unknown User";
  };

  const styles = {
    container: {
      maxWidth: '90%',
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
      fontSize: 'clamp(1.5rem, 2.5vw, 2rem)'
    },
    sectionTitle: {
      marginTop: '2rem',
      marginBottom: '1rem',
      color: '#2c6e6a',
      borderBottom: '2px solid #5ccdc1',
      display: 'inline-block',
      paddingBottom: '0.3rem',
      fontSize: 'clamp(1.2rem, 2vw, 1.5rem)'
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
    },
    pagination: {
      display: 'flex',
      justifyContent: 'center',
      gap: '1rem',
      marginTop: '1rem',
      flexWrap: 'wrap'
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
        <>
          {visibleConfirmed.map(user => (
            <div key={user.id} style={styles.matchCard}>
              <p style={styles.matchName}>{getUsername(user)}</p>
              <p style={styles.matchEmail}>{user.email}</p>
            </div>
          ))}
          {totalConfirmedPages > 1 && (
            <div style={styles.pagination}>
              <button onClick={() => setConfirmedPage(p => Math.max(p - 1, 0))} disabled={confirmedPage === 0}>
                ⬅ Prev
              </button>
              <span>Page {confirmedPage + 1} of {totalConfirmedPages}</span>
              <button onClick={() => setConfirmedPage(p => Math.min(p + 1, totalConfirmedPages - 1))} disabled={confirmedPage === totalConfirmedPages - 1}>
                Next ➡
              </button>
            </div>
          )}
        </>
      ) : (
        <p style={styles.noMatches}>No confirmed matches yet.</p>
      )}

      <h3 style={styles.sectionTitle}>Matches in Progress</h3>
      {pendingMatches.length > 0 ? (
        <>
          {visiblePending.map(user => (
            <div key={user.id} style={styles.matchCard}>
              <p style={styles.matchName}>{getUsername(user)}</p>
            </div>
          ))}
          {totalPendingPages > 1 && (
            <div style={styles.pagination}>
              <button onClick={() => setPendingPage(p => Math.max(p - 1, 0))} disabled={pendingPage === 0}>
                ⬅ Prev
              </button>
              <span>Page {pendingPage + 1} of {totalPendingPages}</span>
              <button onClick={() => setPendingPage(p => Math.min(p + 1, totalPendingPages - 1))} disabled={pendingPage === totalPendingPages - 1}>
                Next ➡
              </button>
            </div>
          )}
        </>
      ) : (
        <p style={styles.noMatches}>No pending matches.</p>
      )}

      <DeleteAccount userEmail={userEmail} setUserId={setUserId} />
    </div>
  );
}

export default Profile;
