import React, { useState } from 'react';
import axios from '../axiosSetup';
import { useNavigate, Link } from 'react-router-dom';
import { getAuth, createUserWithEmailAndPassword, signOut } from 'firebase/auth';

const allowedMajors = [
  "Electrical Engineering",
  "Mechanical Engineering",
  "Chemical Engineering",
  "Civil Engineering",
  "General Engineering",
  "Computer Science"
];

const styles = {
  container: {
    maxWidth: '400px',
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
  checkboxContainer: {
    display: 'flex',
    alignItems: 'center',
    margin: '1rem 0'
  },
  checkbox: {
    marginRight: '0.5rem'
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
    await signOut(auth); // 🧼 Important to clear any deleted or cached Firebase user

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
    // Better error handling
    if (err.code === 'auth/email-already-in-use') {
      setMessage('This email is already registered. Try logging in instead.');
    } else {
      const text = err.response?.data || err.message || 'Something went wrong.';
      setMessage(text);
    }
  } finally {
    setIsSubmitting(false);
  }
};
  
  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Sign Up for StudBuds</h2>
      <form onSubmit={handleSubmit}>
        <input
          style={styles.input}
          name="name"
          placeholder="Full Name"
          value={formData.name}
          onChange={handleChange}
          required
        />
        <input
          style={styles.input}
          name="email"
          type="email"
          placeholder="Email (@cooper.edu)"
          value={formData.email}
          onChange={handleChange}
          required
        />
        <input
          style={styles.input}
          name="password"
          type="password"
          placeholder="Password (min 9 chars)"
          value={formData.password}
          onChange={handleChange}
          required
        />
        <select
          style={styles.select}
          name="major"
          value={formData.major}
          onChange={handleChange}
          required
        >
          <option value="">Select Major</option>
          {allowedMajors.map(m => (
            <option key={m} value={m}>{m}</option>
          ))}
        </select>
        <input
          style={styles.input}
          name="year"
          type="number"
          placeholder="Year (2020–2050)"
          value={formData.year}
          onChange={handleChange}
          required
        />

        <div style={styles.checkboxContainer}>
          <input
            type="checkbox"
            id="agreeToShare"
            checked={agreeToShare}
            onChange={e => setAgreeToShare(e.target.checked)}
            style={styles.checkbox}
          />
          <label htmlFor="agreeToShare">
            I agree that StudBuds may share my email with all future matches
          </label>
        </div>

        <button
          type="submit"
          style={{
            ...styles.button,
            ...(isSubmitting || !agreeToShare ? styles.buttonDisabled : {})
          }}
          disabled={isSubmitting || !agreeToShare}
        >
          {isSubmitting ? 'Signing Up…' : 'Sign Up'}
        </button>
      </form>

      {message && <div style={styles.message}>{message}</div>}

      <p style={{ marginTop: '1.5rem', textAlign: 'center' }}>
        Already have an account?{' '}
        <Link to="/login" style={styles.link}>Sign in here</Link>
      </p>
    </div>
  );
}
