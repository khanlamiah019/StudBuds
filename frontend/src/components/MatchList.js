import React, { useState, useEffect } from 'react';
import TinderCard from 'react-tinder-card';
import axios from '../axiosSetup';
import { useNavigate } from 'react-router-dom';

function MatchList({ userId }) {
  const [matches, setMatches] = useState([]);
  const [error, setError] = useState('');
  const [bottomMessage, setBottomMessage] = useState('');
  const navigate = useNavigate();

  const cuteAnimalEmojiMap = {
    "electrical engineering": "🦈",
    "mechanical engineering": "🐟",
    "civil engineering": "🐠",
    "chemical engineering": "🐡",
    "general engineering": "🐬",
    "computer science": "🐳"
  };

  const getEngineerEmoji = (user) => {
    if (user.major) {
      const normalizedMajor = user.major.trim().toLowerCase();
      return cuteAnimalEmojiMap[normalizedMajor] || "🐱";
    }
    return "🐱";
  };

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

  const getUsername = (user) => {
    return user.name && user.name.trim() !== "" ? user.name : user.email.split('@')[0];
  };

  const onSwipe = (direction, matchUserId) => {
    if (direction === 'right') {
      axios.post(`/api/matches/swipe?user1Id=${userId}&user2Id=${matchUserId}`)
        .then(response => {
          setMatches(prev => prev.filter(match => match.user.id !== matchUserId));
          setBottomMessage(response.data?.message || '');
        })
        .catch(err => {
          setMatches(prev => prev.filter(match => match.user.id !== matchUserId));
          setBottomMessage(err.response?.data || 'Error processing swipe.');
        });
    } else if (direction === 'left') {
      setMatches(prev => prev.filter(match => match.user.id !== matchUserId));
      setBottomMessage('');
    }
  };

  const onCardLeftScreen = (matchUserId) => {
    console.log(`Card for user ${matchUserId} left the screen`);
  };

  const styles = {
    page: {
      minHeight: '100vh',
      paddingTop: '2rem',
      textAlign: 'center'
    },
    header: {
      textAlign: 'center',
      marginBottom: '1rem',
      color: '#ffffff'
    },
    cardContainer: {
      position: 'relative',
      width: '320px',
      height: '500px',
      margin: '0 auto'
    },
    card: {
      position: 'absolute',
      backgroundColor: '#ffffff',
      width: '320px',
      height: '500px',
      boxShadow: '0 4px 10px rgba(0,0,0,0.2)',
      borderRadius: '16px',
      display: 'flex',
      flexDirection: 'column',
      overflow: 'hidden'
    },
    imageContainer: {
      flex: '1 1 auto',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      padding: '1rem'
    },
    textContainer: {
      marginTop: 'auto',
      textAlign: 'left',
      backgroundColor: 'rgba(0,0,0,0.5)',
      padding: '1rem',
      color: '#ffffff'
    },
    username: {
      fontSize: '1.8rem',
      fontWeight: '600',
      marginBottom: '0.5rem',
      color: '#ffffff',
      letterSpacing: '1px',
      textShadow: '0px 1px 3px rgba(0,0,0,0.3)',
      textAlign: 'center'
    },
    emoji: {
      fontSize: '6rem'
    },
    button: {
      padding: '0.75rem 1.5rem',
      fontSize: '1rem',
      backgroundColor: '#5ccdc1',
      color: '#fff',
      border: 'none',
      borderRadius: '25px',
      cursor: 'pointer',
      marginTop: '0.5rem',
      marginRight: '0.5rem'
    }
  };

  return (
    <div style={styles.page}>
      <h2 style={styles.header}>Match List</h2>
      <div style={{ textAlign: 'center', marginBottom: '1rem' }}>
        <button style={styles.button} onClick={() => navigate('/update')}>Update Preference</button>
      </div>
      {error && (
        <p style={{ color: 'blue', textAlign: 'center' }}>
          {typeof error === 'object' ? JSON.stringify(error) : error}
        </p>
      )}
      <div style={styles.cardContainer}>
        {Array.isArray(matches) && matches.length > 0 ? (
          matches.map((match) => (
            <TinderCard
              key={match.user.id}
              onSwipe={(dir) => onSwipe(dir, match.user.id)}
              onCardLeftScreen={() => onCardLeftScreen(match.user.id)}
              preventSwipe={[]}
            >
              <div style={styles.card}>
                <div style={styles.imageContainer}>
                  <span style={styles.emoji}>{getEngineerEmoji(match.user)}</span>
                </div>
                <div style={styles.textContainer}>
                  <h3 style={styles.username}>{getUsername(match.user)}</h3>
                  <p><strong>Common Days:</strong> {match.commonDays.join(', ')}</p>
                  <p><strong>Common Subjects:</strong> {match.commonSubjects.join(', ')}</p>
                  <div style={{ textAlign: 'center', marginTop: '1rem' }}>
                    <button className="pressable" style={styles.button} onClick={() => onSwipe('left', match.user.id)}>✖️</button>
                    <button className="pressable" style={styles.button} onClick={() => onSwipe('right', match.user.id)}>✔️</button>
                  </div>
                </div>
              </div>
            </TinderCard>
          ))
        ) : (
          <p style={{
            textAlign: 'center',
            color: '#ffffff',
            fontSize: '1rem',
            fontWeight: 'bold',
            textShadow: '0 1px 3px rgba(0,0,0,0.6)',
            marginTop: '2rem'
          }}>
            You've swiped the whole school—more might swim by later! 🐟📚
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
