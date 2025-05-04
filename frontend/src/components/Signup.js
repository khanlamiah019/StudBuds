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
    const val = e.target.name === 'email' ? e.target.value.toLowerCase() : e.target.value;
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
      const userCredential = await createUserWithEmailAndPassword(auth, formData.email, formData.password);
      const token = await userCredential.user.getIdToken();

      await axios.post('/api/auth/signup', {
        name: formData.name.trim(),
        email: formData.email.trim(),
        password: formData.password,
        major: formData.major,
        year: formData.year,
        firebaseToken: token
      });

      navigate('/login', { replace: true });
    } catch (err) {
      const text = err.response?.data || err.message;
      setMessage(text);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: '420px', margin: '2rem auto', padding: '2rem', border: '1px solid #ccc', borderRadius: '8px' }}>
      <h2 style={{ marginBottom: '1rem' }}>Sign Up</h2>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
        <input name="name" value={formData.name} onChange={handleChange} placeholder="Name" required />
        <input name="email" type="email" value={formData.email} onChange={handleChange} placeholder="Email" required />
        <input name="password" type="password" value={formData.password} onChange={handleChange} placeholder="Password" required />
        <select name="major" value={formData.major} onChange={handleChange} required>
          <option value="">Select Major</option>
          {allowedMajors.map(m => <option key={m} value={m}>{m}</option>)}
        </select>
        <input name="year" type="number" value={formData.year} onChange={handleChange} placeholder="Graduation Year" required />

        <label style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.9rem' }}>
          <input type="checkbox" checked={agreeToShare} onChange={() => setAgreeToShare(!agreeToShare)} />
          I agree to share my email with matches
        </label>

        <button type="submit" disabled={isSubmitting || !agreeToShare} style={{
          padding: '0.5rem',
          backgroundColor: '#0056b3',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: isSubmitting || !agreeToShare ? 'not-allowed' : 'pointer'
        }}>
          {isSubmitting ? 'Signing up...' : 'Sign Up'}
        </button>
      </form>

      {message && <p style={{ marginTop: '1rem', color: 'blue' }}>{message}</p>}
      <p style={{ marginTop: '1rem', fontSize: '0.9rem' }}>
        Already have an account? <Link to="/login">Login</Link>
      </p>
    </div>
  );
}
