// src/components/DeleteAccount.js
import React from 'react';
import axios from '../axiosSetup';
import { getAuth } from 'firebase/auth';
import { useNavigate } from 'react-router-dom';

function DeleteAccount({ userEmail, setUserId }) {
  const navigate = useNavigate();

  const handleDeleteAccount = async () => {
    const confirm = window.confirm(`Are you sure you want to delete your account (${userEmail})? This cannot be undone.`);
    if (!confirm) return;

    try {
      const auth = getAuth();
      const user = auth.currentUser;
      const token = await user.getIdToken();

      const res = await axios.post('/api/auth/delete', {}, {
        headers: { Authorization: `Bearer ${token}` }
      });

      alert(res.data.message || 'Account deleted.');
      await auth.signOut();
      setUserId(null);  // clear user state
      navigate('/signup', { replace: true });
    } catch (err) {
      const message = err.response?.data?.message || err.message;
      alert('Failed to delete account: ' + message);
    }
  };

  return (
    <div style={{ marginTop: '2rem', textAlign: 'center' }}>
      <button onClick={handleDeleteAccount} style={{
        backgroundColor: '#ff4d4f',
        color: 'white',
        padding: '12px 20px',
        borderRadius: '20px',
        border: 'none',
        fontSize: '1rem',
        cursor: 'pointer'
      }}>
        Delete My Account
      </button>
    </div>
  );
}

export default DeleteAccount;