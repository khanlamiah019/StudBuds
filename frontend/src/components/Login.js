import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function Login({ setUserId }) {
  const [formData, setFormData] = useState({ email: '', password: '' });
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    axios.post('/api/auth/login', formData)
      .then(response => {
        if (response.data && response.data.userId) {
          setUserId(response.data.userId);
          navigate('/dashboard');
        } else {
          setMessage("Login failed: userId not returned.");
        }
      })
      .catch(error => {
        setMessage(error.response?.data || 'Login failed');
      });
  };

  return (
    <div style={{ textAlign: 'center', marginTop: '2rem' }}>
      <h2>Log In</h2>
      <form onSubmit={handleSubmit}>
        <input 
          type="email" 
          name="email" 
          placeholder="Email (@cooper.edu)" 
          onChange={handleChange} 
          value={formData.email} 
          required 
        /><br/><br/>
        <input 
          type="password" 
          name="password" 
          placeholder="Password" 
          onChange={handleChange} 
          value={formData.password} 
          required 
        /><br/><br/>
        <button type="submit">Log In</button>
      </form>
      {message && <p style={{ color: 'red' }}>{message}</p>}
    </div>
  );
}

export default Login;
