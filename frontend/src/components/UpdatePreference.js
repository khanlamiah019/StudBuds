import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

/* Days array unchanged */
const DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

/* Subject array with categories */
const SUBJECTS_WITH_CATEGORIES = [
  // Math
  { name: "Calculus", category: "Math" },
  { name: "Differential Equations", category: "Math" },
  { name: "Linear Algebra", category: "Math" },
  { name: "Probability", category: "Math" },
  { name: "Discrete Math", category: "Math" },

  // Chemical
  { name: "General Chemistry", category: "Chemical" },
  { name: "Organic Chemistry", category: "Chemical" },
  { name: "Biochemistry", category: "Chemical" },

  // Electrical
  { name: "Circuits", category: "Electrical" },
  { name: "Electronics", category: "Electrical" },
  { name: "Digital Logic Design", category: "Electrical" },
  { name: "Computer Architecture", category: "Electrical" },

  // Computer Science
  { name: "Computer Science 101", category: "Computer Science" },
  { name: "Data Structures and Algorithms", category: "Computer Science" },
  { name: "Operating Systems", category: "Computer Science" },
  { name: "Computer Networks", category: "Computer Science" },
  { name: "Software Engineering", category: "Computer Science" },

  // Civil
  { name: "Concrete Structures", category: "Civil" },
  { name: "Steel Structures", category: "Civil" },
  { name: "Transportation Engineering", category: "Civil" },
  { name: "Environmental Engineering", category: "Civil" },

  // Mechanical
  { name: "Thermodynamics", category: "Mechanical" },
  { name: "Fluid Mechanics", category: "Mechanical" },
  { name: "Heat Transfer", category: "Mechanical" },
  { name: "Mechanical Design", category: "Mechanical" },
  { name: "Manufacturing Processes", category: "Mechanical" },

  // Physics
  { name: "Classical Mechanics", category: "Physics" },
  { name: "Electromagnetism", category: "Physics" },
  { name: "Quantum Mechanics", category: "Physics" },
  { name: "Optics", category: "Physics" }
];

const CATEGORIES = [
  "All",
  "Civil",
  "Chemical",
  "Electrical",
  "Mechanical",
  "Computer Science",
  "Math",
  "Physics"
];

function UpdatePreference({ userId }) {
  const navigate = useNavigate();

  // Days state (unchanged)
  const [selectedDays, setSelectedDays] = useState([]);

  // Keep subject states in an object: subject name -> "learn"|"teach"|"none"
  const [subjectStates, setSubjectStates] = useState(() =>
    SUBJECTS_WITH_CATEGORIES.reduce((acc, sub) => ({ ...acc, [sub.name]: "none" }), {})
  );

  // New state for category filter
  const [activeCategory, setActiveCategory] = useState("All");

  // After submitting, show success/error message
  const [message, setMessage] = useState('');

  /* Handle day pills */
  const handleDayClick = (day) => {
    setSelectedDays(prev =>
      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
    );
  };

  /* Helper to change a subject's state */
  const setSubjectState = (subjectName, newState) => {
    setSubjectStates(prev => ({ ...prev, [subjectName]: newState }));
  };

  /* Filter subjects by activeCategory */
  const filteredSubjects = activeCategory === "All"
    ? SUBJECTS_WITH_CATEGORIES
    : SUBJECTS_WITH_CATEGORIES.filter(sub => sub.category === activeCategory);

  /* Submit preference to backend */
  const handleSubmit = (e) => {
    e.preventDefault();

    const subjectsToLearn = [];
    const subjectsToTeach = [];

    // Build arrays of chosen subjects
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
      borderRadius: '6px',
      display: 'flex',
      flexDirection: 'column',
      justifyContent: 'center', 
      minHeight: '200px',
      textAlign: 'left'
    },
    legend: {
      fontWeight: 'bold',
      marginBottom: '1rem',
      textAlign: 'center'
    },

    dayPill: (selected) => ({
      padding: '10px 16px',
      borderRadius: '20px',
      backgroundColor: selected ? '#5ccdc1' : '#f5f5f5',
      border: '1px solid #ccc',
      color: selected ? 'white' : '#333',
      fontWeight: 'normal',
      cursor: 'pointer',
      transition: 'all 0.2s ease',
      userSelect: 'none'
    }),
    dayPillContainer: {
      display: 'flex',
      flexWrap: 'wrap',
      justifyContent: 'center',
      gap: '12px',
      marginTop: '1rem'
    },
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
        state === "learn" ? '#7dd3fc'
        : state === "teach" ? '#38bdf8'
        : '#cbd5e1',
      top: '4px',
      left: state === "learn" ? '4px'
           : state === "teach" ? '116px'
           : '60px',
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
    },
    filterBar: {
      display: 'flex',
      flexWrap: 'wrap',
      justifyContent: 'center',
      gap: '10px',
      marginBottom: '1rem'
    },
    filterButton: (isActive) => ({
      border: '1px solid #ccc',
      backgroundColor: isActive ? '#5ccdc1' : '#f5f5f5',
      color: isActive ? 'white' : '#333',
      borderRadius: '8px',
      padding: '6px 12px',
      cursor: 'pointer',
      fontWeight: 'bold'
    })
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Update Preferences</h2>
      <form onSubmit={handleSubmit}>

        {/* 1) Available Days */}
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

        {/* 2) Category Filter Buttons */}
        <div style={styles.filterBar}>
          {CATEGORIES.map(cat => (
            <button
              key={cat}
              type="button"
              onClick={() => setActiveCategory(cat)}
              style={styles.filterButton(activeCategory === cat)}
            >
              {cat}
            </button>
          ))}
        </div>

        {/* 3) Subject Preferences (filtered) */}
        <fieldset style={styles.fieldset}>
          <legend style={styles.legend}>Subject Preferences</legend>
          {filteredSubjects.map(({ name }) => (
            <div key={name} style={styles.subjectRow}>
              <span style={styles.subjectName}>{name}</span>
              <div style={styles.switchWrapper}>
                <div style={styles.switchCircle(subjectStates[name])} />
                <div
                  style={styles.switchZone}
                  onClick={() => setSubjectState(name, 'learn')}
                >
                  Learn
                </div>
                <div
                  style={styles.switchZone}
                  onClick={() => setSubjectState(name, 'none')}
                >
                  None
                </div>
                <div
                  style={styles.switchZone}
                  onClick={() => setSubjectState(name, 'teach')}
                >
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