// src/Signup.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import { getAuth, signOut, createUserWithEmailAndPassword } from 'firebase/auth';

const allowedMajors = [
  "Electrical Engineering",
  "Mechanical Engineering",
  "Chemical Engineering",
  "Civil Engineering",
  "General Engineering",
  "Computer Science"
];

function Signup() {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    password: '',
    major: '',
    year: ''
  });
  const [message, setMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const auth = getAuth();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

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

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    setMessage('');
    setIsSubmitting(true);
    let token;
    
    // Ensure no user is currently signed in.
    try {
      await signOut(auth);
    } catch (err) {
      console.log("Sign-out error (ignorable):", err);
    }
    
    try {
      // Create the user in Firebase.
      const userCredential = await createUserWithEmailAndPassword(
        auth,
        formData.email,
        formData.password
      );
      token = await userCredential.user.getIdToken();

      // Call the backend signup endpoint.
      await axios.post('/api/auth/signup', formData, {
        headers: { 'Authorization': `Bearer ${token}` }
      });

      // After successful signup, redirect to the login page.
      navigate('/login');
    } catch (error) {
      if (error.code === 'auth/email-already-in-use') {
        setMessage("User already exists. Please sign in.");
        navigate('/login');
      } else {
        setMessage(error.response?.data || error.message || 'Signup failed');
      }
    } finally {
      setIsSubmitting(false);
    }
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
          {allowedMajors.map((major) => (
            <option key={major} value={major}>{major}</option>
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
        <button type="submit" disabled={isSubmitting}>Sign Up</button>
      </form>
      {message && (
        <div style={{ marginTop: '1rem', textAlign: 'center' }}>
          <p style={{ color: 'red' }}>{message}</p>
        </div>
      )}
      <p style={{ marginTop: '1rem' }}>
        Already have an account? <Link to="/login">Sign in here</Link>
      </p>
    </div>
  );
}

export default Signup;