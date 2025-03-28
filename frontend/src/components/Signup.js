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
  
    // Check that the email address is a cooper.edu email
    if (!formData.email.toLowerCase().endsWith('@cooper.edu')) {
      setMessage("Please use a cooper.edu email address.");
      return;
    }
  
    setMessage('');
    setIsSubmitting(true);
    let token;
  
    try {
      await signOut(auth);
    } catch (err) {
      console.log("Sign-out error (ignorable):", err);
    }
  
    try {
      const userCredential = await createUserWithEmailAndPassword(
        auth,
        formData.email,
        formData.password
      );
      token = await userCredential.user.getIdToken();
  
      await axios.post('/api/auth/signup', formData, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
  
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

  const styles = {
    container: {
      maxWidth: '450px',
      margin: '3rem auto',
      padding: '2rem',
      borderRadius: '12px',
      boxShadow: '0 6px 12px rgba(0,0,0,0.1)',
      backgroundColor: '#ffffff',
    },
    heading: {
      textAlign: 'center',
      marginBottom: '1.5rem',
      color: '#2c6e6a'
    },
    input: {
      width: '100%',
      padding: '12px',
      margin: '0.5rem 0',
      borderRadius: '20px',
      border: '1px solid #ccc',
      boxSizing: 'border-box',
      outline: 'none',
      fontSize: '1rem'
    },
    select: {
      width: '100%',
      padding: '12px',
      margin: '0.5rem 0',
      borderRadius: '20px',
      border: '1px solid #ccc',
      boxSizing: 'border-box',
      outline: 'none',
      fontSize: '1rem'
    },
    button: {
      width: '100%',
      padding: '12px',
      marginTop: '1rem',
      borderRadius: '20px',
      border: 'none',
      backgroundColor: '#5ccdc1',
      color: '#fff',
      fontSize: '1rem',
      cursor: 'pointer',
      transition: 'background-color 0.3s ease'
    },
    buttonDisabled: {
      backgroundColor: '#9de0da',
      cursor: 'not-allowed'
    },
    message: {
      color: 'blue',
      textAlign: 'center',
      marginTop: '1rem'
    },
    link: {
      color: '#2c6e6a',
      textDecoration: 'none',
      fontWeight: 'bold'
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Sign Up for StudBuds</h2>
      <form onSubmit={handleSubmit}>
        <input 
          style={styles.input}
          type="text" 
          name="name" 
          placeholder="Full Name" 
          onChange={handleChange} 
          value={formData.name} 
          required
        />

        <input 
          style={styles.input}
          type="email" 
          name="email" 
          placeholder="Email (@cooper.edu)" 
          onChange={handleChange} 
          value={formData.email} 
          required
        />

        <input 
          style={styles.input}
          type="password" 
          name="password" 
          placeholder="Password (min 9 characters)" 
          onChange={handleChange} 
          value={formData.password} 
          required
        />

        <select 
          name="major" 
          value={formData.major} 
          onChange={handleChange} 
          style={styles.select}
          required
        >
          <option value="">Select Your Major</option>
          {allowedMajors.map((major) => (
            <option key={major} value={major}>{major}</option>
          ))}
        </select>

        <input 
          style={styles.input}
          type="number" 
          name="year" 
          placeholder="Year (2020-2050)" 
          onChange={handleChange} 
          value={formData.year} 
          min="2020" 
          max="2050" 
          required
        />

        <button 
          type="submit" 
          style={{ 
            ...styles.button, 
            ...(isSubmitting ? styles.buttonDisabled : {}) 
          }}
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Signing Up...' : 'Sign Up'}
        </button>
      </form>

      {message && (
        <div style={styles.message}>
          {message}
        </div>
      )}

      <p style={{ marginTop: '1.5rem', textAlign: 'center' }}>
        Already have an account?{' '}
        <Link to="/login" style={styles.link}>Sign in here</Link>
      </p>
    </div>
  );
}

export default Signup;