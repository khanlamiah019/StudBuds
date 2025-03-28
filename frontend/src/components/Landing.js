import React from 'react';
import { Link } from 'react-router-dom';

function Landing() {
  const styles = {
    container: {
      maxWidth: '400px',
      margin: '3rem auto',
      padding: '2rem',
      borderRadius: '12px',
      backgroundColor: '#ffffff',
      boxShadow: '0 6px 12px rgba(0,0,0,0.1)',
      textAlign: 'center'
    },
    heading: {
      color: '#2c6e6a',
      marginBottom: '1.5rem'
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
    }
  };

  return (
    <div style={styles.container}>
      <h1 style={styles.heading}>Welcome to StudBuds</h1>
      <p>Your study buddy matching app.</p>
      <Link to="/login">
        <button style={styles.button}>Log In</button>
      </Link>
      <Link to="/signup">
        <button style={styles.button}>Sign Up</button>
      </Link>
    </div>
  );
}

export default Landing;