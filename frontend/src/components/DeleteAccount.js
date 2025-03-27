// src/DeleteAccount.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { getAuth } from 'firebase/auth';

function DeleteAccount({ userEmail, setUserId }) {
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const auth = getAuth();

  const handleDelete = async () => {
    try {
      // Get the Firebase token from the currently authenticated user.
      const token = await auth.currentUser.getIdToken();
      const payload = { email: userEmail, firebaseToken: token };

      // Call the backend delete endpoint.
      await axios.delete('/api/auth/delete', {
        data: payload, // For DELETE requests, data is passed here.
        headers: { 'Authorization': `Bearer ${token}` }
      });

      setMessage("Account deleted successfully.");
      setUserId(null);
      // Optionally sign out the user from Firebase.
      // Navigate back to the landing page.
      navigate('/');
    } catch (error) {
      setMessage(error.response?.data || error.message || 'Account deletion failed.');
    }
  };

  return (
    <div style={{ textAlign: 'center', marginTop: '2rem' }}>
      <h2>Delete Account</h2>
      <button onClick={handleDelete}>Delete Account</button>
      {message && <p style={{ color: 'red' }}>{message}</p>}
    </div>
  );
}

export default DeleteAccount;