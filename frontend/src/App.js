import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Login from './components/Login';
import Signup from './components/Signup';
import Dashboard from './components/Dashboard';
import Profile from './components/Profile';

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Dashboard</Link> | 
        <Link to="/login">Login</Link> | 
        <Link to="/signup">Signup</Link> | 
        <Link to="/profile">Profile</Link>
      </nav>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/profile" element={<Profile />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;