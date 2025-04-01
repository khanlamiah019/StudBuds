import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

const SUBJECTS = [
  "Calculus", "Differential Equations", "Linear Algebra", "Probability",
  "Physics", "General Chemistry", "Organic Chemistry", "Biochemistry",
  "Electronics I", "Electronics II", "Digital Logic", "Computer Architecture", "Data Structures and Algorithms",
  "Operating Systems", "Computer Networks", "Software Engineering",
  "Concrete Structures", "Steel Structures", "Transportation Engineering", "Environmental Engineering",
  "Thermodynamics", "Fluid Mechanics", "Heat Transfer", "Mechanical Design", "Manufacturing Processes"
];

function UpdatePreference({ userId }) {
  const navigate = useNavigate();
  const [selectedDays, setSelectedDays] = useState([]);
  const [subjectStates, setSubjectStates] = useState(() =>
    SUBJECTS.reduce((acc, subject) => ({ ...acc, [subject]: "none" }), {})
  );
  const [message, setMessage] = useState('');

  const handleDayClick = (day) => {
    setSelectedDays(prev =>
      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
    );
  };

  const setSubjectState = (subject, newState) => {
    setSubjectStates(prev => ({ ...prev, [subject]: newState }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const subjectsToLearn = [];
    const subjectsToTeach = [];

    Object.entries(subjectStates).forEach(([subject, state]) => {
      if (state === "learn") subjectsToLearn.push(subject);
      if (state === "teach") subjectsToTeach.push(subject);
    });

    const preferenceData = {
      availableDays: selectedDays.join(', '),
      subjectsToLearn: subjectsToLearn.join(', '),
      subjectsToTeach: subjectsToTeach.join(', ')
    };

    axios.post(`/api/user/${userId}/preference`, preferenceData)
      .then(() => {
        setMessage('Preference updated successfully.');
        navigate('/matchlist');
      })
      .catch(error => {
        // If error.response.data is an object, assume it's due to too many preferences picked.
        if (error.response && typeof error.response.data === 'object') {
          setMessage("Too many preferences picked. Please select fewer subjects.");
        } else {
          setMessage(error.response?.data || 'Update failed.');
        }
      });
  };


  const styles = {
    container: {
      maxWidth: '30vw',
      margin: '3rem auto',
      padding: '2rem',
      borderRadius: '12px',
      backgroundColor: '#ffffff',
      boxShadow: '0 6px 12px rgba(0,0,0,0.1)',
      textAlign: 'left'
    },
    heading: {
      textAlign: 'center',
      marginBottom: '2rem'
    },
    fieldset: {
      marginBottom: '2rem',
      padding: '1.5rem',
      border: '1px solid #ccc',
      borderRadius: '6px'
    },
    legend: {
      fontWeight: 'bold',
      marginBottom: '1rem'
    },
    dayPillContainer: {
      display: 'flex',
      flexWrap: 'wrap',
      justifyContent: 'center',
      gap: '12px',
      marginTop: '1rem'
    },
    dayPill: (selected) => ({
      padding: '10px 16px',
      borderRadius: '20px',
      backgroundColor: selected ? '#5ccdc1' : '#f5f5f5',
      border: '1px solid #ccc',
      color: selected ? 'white' : '#333',
      fontWeight: selected ? 'normal' : 'normal',
      cursor: 'pointer',
      transition: 'all 0.2s ease',
      userSelect: 'none'
    }),
    subjectRow: {
      display: 'flex',
      justifyContent: 'space-between',
      alignItems: 'center',
      padding: '8px 0',
      borderBottom: '1px solid #eee'
    },
    subjectName: {
      flex: 1,
      fontSize: '0.95rem'
    },
    switchWrapper: {
      width: '180px',
      height: '36px',
      borderRadius: '18px',
      backgroundColor: '#e5e7eb',
      position: 'relative',
      display: 'flex',
      overflow: 'hidden',
      boxShadow: 'inset 0 0 5px rgba(0,0,0,0.1)'
    },
    switchZone: {
      flex: 1,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      fontSize: '0.85rem',
      fontWeight: 500,
      color: '#333',
      cursor: 'pointer',
      zIndex: 2
    },
    switchCircle: (state) => ({
      position: 'absolute',
      height: '28px',
      width: '60px',
      borderRadius: '14px',
      backgroundColor:
        state === "learn" ? '#7dd3fc' :
        state === "teach" ? '#38bdf8' :
        '#cbd5e1',
      top: '4px',
      left: state === "learn" ? '4px' : state === "teach" ? '116px' : '60px',
      transition: 'left 0.25s ease, background-color 0.25s ease',
      zIndex: 1
    }),
    buttonContainer: {
      textAlign: 'center',
      marginTop: '2rem'
    },
    error: {
      color: 'blue',
      textAlign: 'center',
      marginTop: '1rem'
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Update Preferences</h2>
      <form onSubmit={handleSubmit}>
        <fieldset style={styles.fieldset}>
          <legend style={styles.legend}>Available Days</legend>
          <div style={styles.dayPillContainer}>
            {DAYS.map(day => (
              <span
                key={day}
                style={styles.dayPill(selectedDays.includes(day))}
                onClick={() => handleDayClick(day)}
              >
                {day}
              </span>
            ))}
          </div>
        </fieldset>

        <fieldset style={styles.fieldset}>
          <legend style={styles.legend}>Subject Preferences</legend>
          {SUBJECTS.map(subject => (
            <div key={subject} style={styles.subjectRow}>
              <span style={styles.subjectName}>{subject}</span>
              <div style={styles.switchWrapper}>
                <div style={styles.switchCircle(subjectStates[subject])} />
                <div style={styles.switchZone} onClick={() => setSubjectState(subject, 'learn')}>
                  Learn
                </div>
                <div style={styles.switchZone} onClick={() => setSubjectState(subject, 'none')}>
                  None
                </div>
                <div style={styles.switchZone} onClick={() => setSubjectState(subject, 'teach')}>
                  Teach
                </div>
              </div>
            </div>
          ))}
        </fieldset>

        <div style={styles.buttonContainer}>
          <button type="submit">Update Preference</button>
        </div>
      </form>
      {message && <p style={styles.error}>{message}</p>}
    </div>
  );
}

export default UpdatePreference;