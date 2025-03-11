import React, { useState, useEffect } from 'react';
import axios from 'axios';

function Profile({ userId }) {
  const [confirmedMatches, setConfirmedMatches] = useState([]);
  const [pendingMatches, setPendingMatches] = useState([]);
  const [error, setError] = useState('');

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
    }
  }, [userId]);

  return (
    <div style={{ padding: '1rem' }}>
      <h2>Your Profile</h2>
      {error && <p style={{ color: 'red' }}>{error}</p>}
      <h3>People Matched With</h3>
      {confirmedMatches.length > 0 ? (
        confirmedMatches.map(match => (
          <div key={match.id} style={{ border: '1px solid #ccc', padding: '0.5rem', margin: '0.5rem 0' }}>
            <p>{match.email}</p>
          </div>
        ))
      ) : (
        <p>No confirmed matches yet.</p>
      )}
      <h3>Matches in Progress</h3>
      {pendingMatches.length > 0 ? (
        pendingMatches.map(match => (
          <div key={match.id} style={{ border: '1px solid #ccc', padding: '0.5rem', margin: '0.5rem 0' }}>
            <p>{match.email}</p>
          </div>
        ))
      ) : (
        <p>No pending matches.</p>
      )}
    </div>
  );
}

export default Profile;
