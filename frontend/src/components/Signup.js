import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

// Allowed majors.
const allowedMajors = [
  "Electrical Engineering",
  "Mechanical Engineering",
  "Chemical Engineering",
  "Civil Engineering",
  "General Engineering",
  "Computer Science"
];

function Signup({ setUserId }) {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    major: '',
    year: ''
  });
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  // Handle field changes.
  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  // Validate year and major.
  const validateForm = () => {
    const year = parseInt(formData.year, 10);
    if (isNaN(year) || year < 2020 || year > 2050) {
      setMessage("Year must be a number between 2020 and 2050.");
      return false;
    }
    if (!allowedMajors.includes(formData.major)) {
      setMessage("Please select a valid major.");
      return false;
    }
    return true;
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setMessage("");
    if (!validateForm()) return;
    axios.post('/api/auth/signup', formData)
      .then(response => {
        // Expect backend to return a success message.
        setMessage(response.data);
        // Redirect user to login.
        navigate('/login');
      })
      .catch(error => {
        // Display error from backend.
        setMessage(error.response?.data || 'Signup failed');
      });
  };

  return (
    <div style={{ textAlign: 'center', marginTop: '2rem' }}>
      <h2>Sign Up</h2>
      <form onSubmit={handleSubmit}>
        <input 
          type="text" 
          name="name" 
          placeholder="Name" 
          onChange={handleChange} 
          value={formData.name} 
          required 
        /><br/><br/>
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
          placeholder="Password (min 9 characters)" 
          onChange={handleChange} 
          value={formData.password} 
          required 
        /><br/><br/>
        <select name="major" value={formData.major} onChange={handleChange} required>
          <option value="">Select Major</option>
          {allowedMajors.map((major, index) => (
            <option key={index} value={major}>{major}</option>
          ))}
        </select><br/><br/>
        <input 
          type="number" 
          name="year" 
          placeholder="Year (2020-2050)" 
          onChange={handleChange} 
          value={formData.year} 
          min="2020" 
          max="2050" 
          required 
        /><br/><br/>
        <button type="submit">Sign Up</button>
      </form>
      {message && <p style={{ color: 'red' }}>{message}</p>}
    </div>
  );
}

export default Signup;
