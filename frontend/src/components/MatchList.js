import React, { useState, useEffect } from 'react';
import TinderCard from 'react-tinder-card';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function MatchList({ userId }) {
  const [matches, setMatches] = useState([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  // Array of ASCII art images (animals) - you can expand or change these
  const asciiAnimals = [
    ` /\\_/\\  
( o.o )
 > ^ <`, // Cat
    `  /\\_/\\
 ( o.o )
  > ^ <`, // Dog (similar styling)
    `  /  \\~~~/  \\
 (    ..    )
  \\__-__-/`, // Elephant
    ` (\\_/)
 ( •_•)
 / >🥕`  // Rabbit
  ];

  // Increase ASCII art size for visual impact.
  const asciiStyle = {
    fontFamily: 'monospace',
    fontSize: '2.5rem',
    whiteSpace: 'pre',
    textAlign: 'center',
    margin: 0
  };

  // Returns an ASCII art image based on the user id.
  const getAsciiArt = (userId) => {
    return asciiAnimals[userId % asciiAnimals.length];
  };

  // Fetch matches from backend.
  const fetchMatches = () => {
    axios.get(`/api/matches/find/${userId}`)
      .then(response => {
        setMatches(response.data);
        setError('');
      })
      .catch(err => {
        setError(err.response?.data || 'Error fetching matches');
        setMatches([]);
      });
  };

  useEffect(() => {
    if (userId) fetchMatches();
  }, [userId]);

  // Get username from the user object.
  const getUsername = (user) => {
    return user.name && user.name.trim() !== "" ? user.name : user.email.split('@')[0];
  };

  // When a swipe occurs.
  const onSwipe = (direction, matchUserId) => {
    if (direction === 'right') {
      axios.post(`/api/matches/swipe?user1Id=${userId}&user2Id=${matchUserId}`)
        .then(() => {
          setMatches(prev => prev.filter(match => match.user.id !== matchUserId));
        })
        .catch(err => {
          alert(err.response?.data || 'Error processing swipe.');
        });
    } else if (direction === 'left') {
      setMatches(prev => prev.filter(match => match.user.id !== matchUserId));
    }
  };

  const onCardLeftScreen = (matchUserId) => {
    console.log(`Card for user ${matchUserId} left the screen`);
  };

  // Styling for the overall background and container.
  const pageStyle = {
    background: 'linear-gradient(135deg, #e0f7fa, #ffffff)',
    minHeight: '100vh',
    paddingTop: '2rem'
  };

  const headerStyle = {
    textAlign: 'center',
    marginBottom: '1rem',
    color: '#2c6e6a'
  };

  // Container for the Tinder cards.
  const cardContainerStyle = {
    position: 'relative',
    width: '320px',
    height: '500px',
    margin: '0 auto'
  };

  // Card styling with a translucent overlay for details.
  const cardStyle = {
    position: 'absolute',
    backgroundColor: '#ffffff',
    width: '320px',
    height: '500px',
    boxShadow: '0 8px 16px rgba(0,0,0,0.2)',
    borderRadius: '16px',
    display: 'flex',
    flexDirection: 'column',
    overflow: 'hidden'
  };

  // Image container centers the ASCII art.
  const imageContainerStyle = {
    flex: '1 1 auto',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '1rem'
  };

  // Text container for match details with a semi-transparent overlay.
  const textContainerStyle = {
    backgroundColor: 'rgba(0,0,0,0.5)',
    color: '#fff',
    padding: '1rem',
    textAlign: 'left'
  };

  const buttonStyle = {
    padding: '0.75rem 1.5rem',
    fontSize: '1rem',
    backgroundColor: '#5ccdc1',
    color: '#fff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginTop: '1rem'
  };

  return (
    <div style={pageStyle}>
      <h2 style={headerStyle}>Match List</h2>
      <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
        <button style={buttonStyle} onClick={() => navigate('/update')}>Update Preference</button>
      </div>
      {error && <p style={{ color: 'red', textAlign: 'center' }}>{error}</p>}
      <div style={cardContainerStyle}>
        {matches && matches.length > 0 ? (
          matches.map((match) => (
            <TinderCard
              key={match.user.id}
              onSwipe={(dir) => onSwipe(dir, match.user.id)}
              onCardLeftScreen={() => onCardLeftScreen(match.user.id)}
              preventSwipe={[]} // Allow swiping in all directions
            >
              <div style={cardStyle}>
                <div style={imageContainerStyle}>
                  <pre style={asciiStyle}>
                    {getAsciiArt(match.user.id)}
                  </pre>
                </div>
                <div style={textContainerStyle}>
                  <h3>{getUsername(match.user)}</h3>
                  <p><strong>Common Days:</strong> {match.commonDays.join(', ')}</p>
                  <p><strong>Common Subjects:</strong> {match.commonSubjects.join(', ')}</p>
                </div>
              </div>
            </TinderCard>
          ))
        ) : (
          <p style={{ textAlign: 'center' }}>No matches available.</p>
        )}
      </div>
    </div>
  );
}

export default MatchList;
