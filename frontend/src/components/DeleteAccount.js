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
      // Get the Firebase token.
      const token = await auth.currentUser.getIdToken();
      const payload = { email: userEmail, firebaseToken: token };

      // Call the backend delete endpoint.
      await axios.delete('http://localhost:8080/api/auth/delete', {
        data: payload,
        headers: { 'Authorization': `Bearer ${token}` }
      });

      setMessage("Account deleted successfully.");
      setUserId(null);
      // Redirect to sign up page after deletion.
      navigate('/signup');
    } catch (error) {
      // If the error message is an object, stringify it.
      const errMsg = error.response?.data || error.message || 'Account deletion failed.';
      setMessage(typeof errMsg === 'object' ? JSON.stringify(errMsg) : errMsg);
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