import { useState } from 'react';
import { createUserWithEmailAndPassword, updateProfile } from 'firebase/auth';
import { auth } from '../firebase';
import api from '../api';
import { useNavigate } from 'react-router-dom';

function Signup() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [major, setMajor] = useState('');
  const [year, setYear] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const signup = async () => {
    setLoading(true);
    setError('');
    try {
      const userCredential = await createUserWithEmailAndPassword(auth, email, password);
      await updateProfile(userCredential.user, { displayName: name });
      await api.post('/api/auth/signup', { name, email, password, major, year });
      localStorage.setItem('userId', userCredential.user.uid);
      navigate('/'); // Redirect to dashboard
    } catch (err) {
      setError(err.response?.data || err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <input placeholder="Name" value={name} onChange={e => setName(e.target.value)} />
      <input placeholder="Email" value={email} onChange={e => setEmail(e.target.value)} />
      <input type="password" placeholder="Password" value={password} onChange={e => setPassword(e.target.value)} />
      <input placeholder="Major" value={major} onChange={e => setMajor(e.target.value)} />
      <input placeholder="Year" value={year} onChange={e => setYear(e.target.value)} />
      <button disabled={loading} onClick={signup}>Signup</button>
      {error && <div style={{ color: 'red' }}>{error}</div>}
    </div>
  );
}

export default Signup;