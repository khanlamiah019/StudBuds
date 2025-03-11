import React from 'react';
import { Link } from 'react-router-dom';

function Landing() {
  return (
    <div style={{ textAlign: 'center', marginTop: '2rem' }}>
      <h1>Welcome to StudBuds</h1>
      <p>Your study buddy matching app.</p>
      <Link to="/login"><button>Log In</button></Link>
      <Link to="/signup"><button>Sign Up</button></Link>
    </div>
  );
}

export default Landing;
