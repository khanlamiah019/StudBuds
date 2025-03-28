import React from 'react';
import { useNavigate } from 'react-router-dom';

function Dashboard({ userId }) {
  const navigate = useNavigate();
  return (
    <div style={{ textAlign: 'center', marginTop: '2rem' }}>
      <h2>Dashboard</h2>
      <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem' }}>
        <button onClick={() => navigate('/update')}>Update Preferences</button>
        <button onClick={() => navigate('/matchlist')}>View Matches</button>
        <button onClick={() => navigate('/profile')}>Profile</button>
      </div>
    </div>
  );
}

export default Dashboard;