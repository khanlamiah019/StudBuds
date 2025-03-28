import React, { useState, useEffect } from 'react';
import TinderCard from 'react-tinder-card';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function MatchList({ userId }) {
  const [matches, setMatches] = useState([]);
  const [error, setError] = useState('');
  const [bottomMessage, setBottomMessage] = useState('');
  const navigate = useNavigate();

// Mapping of majors to cute animal emojis.
const cuteAnimalEmojiMap = {
  "electrical engineering": "🐭",
  "mechanical engineering": "🐱",
  "civil engineering": "🐶",
  "chemical engineering": "🐹",
  "general engineering": "🐻",
  "computer science": "🦊"
};

// Returns a cute animal emoji based on the user's major.
const getEngineerEmoji = (user) => {
  if (user.major) {
    const normalizedMajor = user.major.trim().toLowerCase();
    return cuteAnimalEmojiMap[normalizedMajor] || "🐱"; // default emoji
  }
  return "🐱";
};


  // Fetch matches from backend and ensure we have an array.
  const fetchMatches = () => {
    axios.get(`/api/matches/find/${userId}`)
      .then(response => {
        if (Array.isArray(response.data)) {
          setMatches(response.data);
          setError('');
        } else {
          setMatches([]);
          setError(response.data);
        }
      })
      .catch(err => {
        setError(err.response?.data || 'Error fetching matches');
        setMatches([]);
      });
  };

  useEffect(() => {
    if (userId) fetchMatches();
  }, [userId]);

  // Get username from user.
  const getUsername = (user) => {
    return user.name && user.name.trim() !== "" ? user.name : user.email.split('@')[0];
  };

  // onSwipe callback.
  const onSwipe = (direction, matchUserId) => {
    if (direction === 'right') {
      axios.post(`/api/matches/swipe?user1Id=${userId}&user2Id=${matchUserId}`)
        .then(response => {
          // Always remove the card.
          setMatches(prev => Array.isArray(prev) ? prev.filter(match => match.user.id !== matchUserId) : prev);
          // If a message is returned, update bottomMessage; otherwise clear it.
          if (response.data && response.data.message) {
            setBottomMessage(response.data.message);
          } else {
            setBottomMessage('');
          }
        })
        .catch(err => {
          // Always remove the card even in error case.
          setMatches(prev => Array.isArray(prev) ? prev.filter(match => match.user.id !== matchUserId) : prev);
          setBottomMessage(err.response?.data || 'Error processing swipe.');
        });
    } else if (direction === 'left') {
      // For a left swipe, remove the card and clear any message.
      setMatches(prev => Array.isArray(prev) ? prev.filter(match => match.user.id !== matchUserId) : prev);
      setBottomMessage('');
    }
  };
    const onCardLeftScreen = (matchUserId) => {
    console.log(`Card for user ${matchUserId} left the screen`);
  };

  // Styling for overall page and container.
  const pageStyle = {
    background: 'linear-gradient(135deg, #e0f7fa, #ffffff)',
    minHeight: '100vh',
    paddingTop: '2rem',
    textAlign: 'center'
  };

  const headerStyle = {
    textAlign: 'center',
    marginBottom: '1rem',
    color: '#2c6e6a'
  };

  const cardContainerStyle = {
    position: 'relative',
    width: '320px',
    height: '500px',
    margin: '0 auto'
  };

  const cardStyle = {
    position: 'absolute',
    backgroundColor: '#ffffff',
    width: '320px',
    height: '500px',
    boxShadow: '0 4px 10px rgba(0,0,0,0.2)',
    borderRadius: '16px',
    display: 'flex',
    flexDirection: 'column',
    overflow: 'hidden'
  };

  const imageContainerStyle = {
    flex: '1 1 auto',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    padding: '1rem'
  };

  const textContainerStyle = {
    marginTop: 'auto',
    textAlign: 'left',
    backgroundColor: 'rgba(0,0,0,0.5)',
    padding: '1rem',
    color: '#ffffff'
  };

  const usernameStyle = {
    fontSize: '1.8rem',
    fontWeight: '600',
    marginBottom: '0.5rem',
    color: '#ffffff',
    letterSpacing: '1px',
    textShadow: '0px 1px 3px rgba(0,0,0,0.3)',
    textAlign: 'center'
  };

  const emojiStyle = {
    fontSize: '6rem'
  };

  const buttonStyle = {
    padding: '0.75rem 1.5rem',
    fontSize: '1rem',
    backgroundColor: '#5ccdc1',
    color: '#fff',
    border: 'none',
    borderRadius: '25px',
    cursor: 'pointer',
    marginTop: '0.5rem',
    marginRight: '0.5rem'
  };

  return (
    <div style={pageStyle}>
      <h2 style={headerStyle}>Match List</h2>
      <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
        <button style={buttonStyle} onClick={() => navigate('/update')}>Update Preference</button>
      </div>
      {error && (
        <p style={{ color: 'blue', textAlign: 'center' }}>
          {typeof error === 'object' ? JSON.stringify(error) : error}
        </p>
      )}
      <div style={cardContainerStyle}>
        {Array.isArray(matches) && matches.length > 0 ? (
          matches.map((match) => (
            <TinderCard
              key={match.user.id}
              onSwipe={(dir) => onSwipe(dir, match.user.id)}
              onCardLeftScreen={() => onCardLeftScreen(match.user.id)}
              preventSwipe={[]} // Allow swiping in all directions
            >
              <div style={cardStyle}>
                <div style={imageContainerStyle}>
                  <span style={emojiStyle}>{getEngineerEmoji(match.user)}</span>
                </div>
                <div style={textContainerStyle}>
                  <h3 style={usernameStyle}>{getUsername(match.user)}</h3>
                  <p><strong>Common Days:</strong> {match.commonDays.join(', ')}</p>
                  <p><strong>Common Subjects:</strong> {match.commonSubjects.join(', ')}</p>
                  <div style={{ textAlign: 'center', marginTop: '1rem' }}>
                    <button className="pressable" style={buttonStyle} onClick={() => onSwipe('left', match.user.id)}>✖️</button>
                    <button className="pressable" style={buttonStyle} onClick={() => onSwipe('right', match.user.id)}>✔️</button>
                  </div>
                </div>
              </div>
            </TinderCard>
          ))
        ) : (
          <p style={{ textAlign: 'center' }}>
            {Array.isArray(matches) ? "No matches available." : matches}
          </p>
        )}
      </div>
      {bottomMessage && (
        <p style={{ color: 'blue', textAlign: 'center', marginTop: '1rem' }}>
          {bottomMessage}
        </p>
      )}
    </div>
  );
}

export default MatchList;