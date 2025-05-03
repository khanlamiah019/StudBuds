// src/components/Signup.js
import React, { useState } from 'react';
import axios from '../axiosSetup';
import { useNavigate, Link } from 'react-router-dom';
import { getAuth, createUserWithEmailAndPassword } from 'firebase/auth';

const allowedMajors = [
  "Electrical Engineering", "Mechanical Engineering", "Chemical Engineering",
  "Civil Engineering", "General Engineering", "Computer Science"
];

export default function Signup() {
  const [formData, setFormData] = useState({
    name: '', email: '', password: '', major: '', year: ''
  });
  const [agreeToShare, setAgreeToShare] = useState(false);
  const [message, setMessage] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();
  const auth = getAuth();

  const handleChange = e => {
    let val = e.target.value;
    if (e.target.name === 'email') val = val.toLowerCase();
    setFormData(fd => ({ ...fd, [e.target.name]: val }));
  };

  const validateForm = () => {
    if (!formData.email.endsWith('@cooper.edu')) {
      setMessage("Please use a @cooper.edu email.");
      return false;
    }
    if (formData.password.length < 9) {
      setMessage("Password must be at least 9 characters.");
      return false;
    }
    const y = parseInt(formData.year, 10);
    if (isNaN(y) || y < 2020 || y > 2050) {
      setMessage("Year must be between 2020 and 2050.");
      return false;
    }
    if (!allowedMajors.includes(formData.major)) {
      setMessage("Please select a valid major.");
      return false;
    }
    if (!agreeToShare) {
      setMessage("You must agree to share your email with future matches.");
      return false;
    }
    return true;
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setMessage('');
    if (!validateForm()) return;

    setIsSubmitting(true);
    try {
      const userCredential = await createUserWithEmailAndPassword(
        auth,
        formData.email,
        formData.password
      );
      const token = await userCredential.user.getIdToken();

      await axios.post('/api/auth/signup', {
        name: formData.name.trim(),
        email: formData.email.trim(),
        major: formData.major,
        year: formData.year,
        firebaseToken: token
      });

      navigate('/login', { replace: true });
    } catch (err) {
      const msg = err.response?.data || err.message;
      setMessage(msg);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '2rem auto' }}>
      <h2>Sign Up</h2>
      <form onSubmit={handleSubmit}>
        <input name="name" value={formData.name} onChange={handleChange} placeholder="Name" required />
        <input name="email" value={formData.email} onChange={handleChange} placeholder="Email" type="email" required />
        <input name="password" value={formData.password} onChange={handleChange} placeholder="Password" type="password" required />
        <select name="major" value={formData.major} onChange={handleChange} required>
          <option value="">Select Major</option>
          {allowedMajors.map(m => <option key={m} value={m}>{m}</option>)}
        </select>
        <input name="year" value={formData.year} onChange={handleChange} placeholder="Graduation Year" type="number" required />
        <label>
          <input type="checkbox" checked={agreeToShare} onChange={() => setAgreeToShare(!agreeToShare)} /> I agree to share my email with matches
        </label>
        <button disabled={isSubmitting || !agreeToShare}>
          {isSubmitting ? 'Signing up...' : 'Sign Up'}
        </button>
      </form>
      {message && <p style={{ color: 'blue' }}>{message}</p>}
      <p>Already have an account? <Link to="/login">Login</Link></p>
    </div>
  );
}
