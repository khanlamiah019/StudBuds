// src/DeleteAccount.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { getAuth, signOut } from 'firebase/auth';

function DeleteAccount({ setUserId }) {
  const [message, setMessage] = useState('');
  const navigate = useNavigate();
  const auth = getAuth();

  const handleDelete = async () => {
    try {
      // 1) Get the Firebase token
      const token = await auth.currentUser.getIdToken();

     // 2) Hit our new POST /delete endpoint
      const response = await axios.post(
        '/api/auth/delete',
        { firebaseToken: token },
        { headers: { Authorization: `Bearer ${token}` } }
      );

+     // 3) locally sign out of Firebase
      await signOut(auth);

      setMessage(response.data.message);
      setUserId(null);
      navigate('/signup');
    } catch (error) {
      const errMsg = error.response?.data?.message
                   || error.message
                   || 'Account deletion failed.';
      setMessage(errMsg);
    }
  };

  return (
    <div style={{ textAlign: 'center', marginTop: '2rem' }}>
      <h2>Delete Account</h2>
      <button onClick={handleDelete}>
        Delete Account
      </button>
      {message && <p style={{ color: 'blue' }}>{message}</p>}
    </div>
  );
}

export default DeleteAccount;