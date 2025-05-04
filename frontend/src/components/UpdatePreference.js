import React, { useState, useEffect } from 'react';
import axios from '../axiosSetup';
import { useNavigate } from 'react-router-dom';

/** CONSTANTS */
const DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
const MOBILE_BREAKPOINT = 600; // px

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
  "All", "Civil", "Chemical", "Electrical", "Mechanical",
  "Computer Science", "Math", "Physics"
];

// In UpdatePreference.js, replace your existing `styles` object with this:

const styles = {
  container: {
    maxWidth: '700px',
    margin: '2rem auto',
    padding: '2rem',
    backgroundColor: '#ffffff',
    boxShadow: '0 8px 16px rgba(0,0,0,0.1)',
    borderRadius: '16px',
    fontFamily: 'Arial, sans-serif',
    color: '#2c6e6a'
  },
  heading: {
    textAlign: 'center',
    marginBottom: '2rem'
  },
  fieldset: {
    marginBottom: '2rem',
    padding: '1rem',
    border: '1px solid #ccc',
    borderRadius: '6px',
    display: 'flex',
    flexDirection: 'column',
    textAlign: 'left'
  },
  legend: {
    fontWeight: 'bold',
    marginBottom: '1rem',
    textAlign: 'center'
  },
  dayPill: selected => ({
    padding: '10px 16px',
    borderRadius: '20px',
    backgroundColor: selected ? '#5ccdc1' : '#f5f5f5',
    border: '1px solid #ccc',
    color: selected ? '#fff' : '#333',
    cursor: 'pointer',
    userSelect: 'none'
  }),
  dayPillContainer: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: '12px',
    margin: '0 0 1.75rem 0'
  },
  filterBar: {
    display: 'flex',
    flexWrap: 'wrap',
    justifyContent: 'center',
    gap: '10px',
    marginBottom: '1rem'
  },
  filterButton: isActive => ({
    border: '1px solid #ccc',
    borderRadius: '8px',
    padding: '6px 12px',
    backgroundColor: isActive ? '#5ccdc1' : '#f5f5f5',
    color: isActive ? '#fff' : '#333',
    cursor: 'pointer',
    fontWeight: 'bold'
  }),
  filterNote: {
    textAlign: 'center',
    fontSize: '0.85rem',
    marginBottom: '1rem'
  },
  subjectGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))',
    gap: '1rem',
    position: 'relative'
  },
  subjectRow: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: '8px 16px',
    borderBottom: '1px solid #eee',
    position: 'relative'
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
    cursor: 'pointer',
    zIndex: 2
  },
  switchCircle: state => ({
    position: 'absolute',
    height: '28px',
    width: '60px',
    borderRadius: '14px',
    backgroundColor:
      state === 'learn' ? '#7dd3fc' :
        state === 'teach' ? '#38bdf8' :
          '#cbd5e1',
    top: '4px',
    left:
      state === 'learn' ? '4px' :
        state === 'teach' ? '116px' :
          '60px',
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

export default function UpdatePreference({ userId }) {
  const navigate = useNavigate();
  // Responsive state
  const [isMobile, setIsMobile] = useState(window.innerWidth <= MOBILE_BREAKPOINT);

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= MOBILE_BREAKPOINT);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Overrides for mobile view
  const mobileContainer = {
  width: '90vw',
  maxWidth: '100%',
  padding: '1rem',
  marginLeft: 'auto',
  marginRight: 'auto',
  boxSizing: 'border-box'
  };
  const mobileSubjectGrid = { gridTemplateColumns: '1fr' };
  const mobileOverrides = {
  subjectRow: {
    justifyContent: 'space-between',
    padding: '12px 12px',
  },
  switchWrapper: {
    marginLeft: '12px'
  }
};

  const [selectedDays, setSelectedDays] = useState([]);
  const [subjectStates, setSubjectStates] = useState(() =>
    SUBJECTS_WITH_CATEGORIES.reduce((acc, sub) => ({ ...acc, [sub.name]: 'none' }), {})
  );
  const [activeCategory, setActiveCategory] = useState('All');
  const [message, setMessage] = useState('');
  const [warning, setWarning] = useState('');

  // Load existing prefs
  useEffect(() => {
    axios.get(`/api/user/${userId}/preference`)
      .then(({ data }) => {
        setSelectedDays(
          data.availableDays
            ? data.availableDays.split(',').map(d => d.trim())
            : []
        );
        const learn = data.subjectsToLearn?.split(',').map(s => s.trim()) || [];
        const teach = data.subjectsToTeach?.split(',').map(s => s.trim()) || [];
        setSubjectStates(prev => {
          const updated = { ...prev };
          Object.keys(updated).forEach(sub => {
            updated[sub] = learn.includes(sub) ? 'learn'
              : teach.includes(sub) ? 'teach'
                : 'none';
          });
          return updated;
        });
      })
      .catch(() => setMessage('Failed to load existing preferences.'));
  }, [userId]);

  const handleClear = () => {
    axios.delete(`/api/user/${userId}/preference`)
      .then(() => {
        setSelectedDays([]);
        setSubjectStates(
          SUBJECTS_WITH_CATEGORIES.reduce((acc, sub) => ({ ...acc, [sub.name]: 'none' }), {})
        );
        setMessage('Preferences cleared.');
        setTimeout(() => setMessage(''), 3000); // Clear message after 3 seconds
      })
      .catch(() => setMessage('Failed to clear preferences.'));
  };

  const handleDayClick = day => {
    setSelectedDays(prev =>
      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
    );
  };

  const setSubjectState = (subject, state) => {
  const updated = { ...subjectStates, [subject]: state };
  const selectedCount = Object.values(updated).filter(val => val !== 'none').length;

  if (selectedCount > 14) {
    setWarning(`You have selected ${selectedCount} subjects. Limit is 14.`);
  } else {
    setWarning('');
  }

  setSubjectStates(updated);
};

  const filteredSubjects = activeCategory === 'All'
    ? SUBJECTS_WITH_CATEGORIES
    : SUBJECTS_WITH_CATEGORIES.filter(s => s.category === activeCategory);

  const handleSubmit = e => {
  e.preventDefault();

  const toLearn = [], toTeach = [];
  Object.entries(subjectStates).forEach(([sub, st]) => {
    if (st === 'learn') toLearn.push(sub);
    if (st === 'teach') toTeach.push(sub);
  });

  const total = toLearn.length + toTeach.length;

  // Hard block if nothing is selected
  if (total === 0) {
    setWarning("You must select at least one subject to learn or teach.");
    return;
  }

  // Hard block if over limit
  if (total > 14) {
    setWarning(`You’ve selected ${total} subjects. Please limit to 14.`);
    return;
  }

  // Clear warning if they previously exceeded limit and now corrected
  if (warning && total <= 14) {
    setWarning('');
  }

  // Soft warning if only one side is selected
  if (toLearn.length === 0) {
    setWarning("You haven’t selected any subjects to learn. You may have fewer match results.");
  } else if (toTeach.length === 0) {
    setWarning("You haven’t selected any subjects to teach. You may have fewer match results.");
  } else {
    setWarning(''); // clear warning if both are selected
  }

  axios.post(`/api/user/${userId}/preference`, {
    availableDays: selectedDays.join(', '),
    subjectsToLearn: toLearn.join(', '),
    subjectsToTeach: toTeach.join(', ')
  })
    .then(() => {
      setMessage('Preference updated successfully.');
      navigate('/matchlist');
    })
    .catch(err => {
      const status = err.response?.status;
      if (status === 500) {
        setMessage('Too many preferences picked. Please select fewer subjects.');
      } else {
        const data = err.response?.data;
        let msg = 'Update failed.';
        if (data) {
          if (typeof data === 'string') msg = data;
          else if (data.message) msg = data.message;
          else if (data.error) msg = data.error;
          else msg = JSON.stringify(data);
        }
        setMessage(msg);
      }
    });
};

  return (
    <div style={isMobile ? { ...styles.container, ...mobileContainer } : styles.container}>
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

          <div style={{ display: 'flex', justifyContent: 'center' }}>
            <fieldset style={styles.fieldset}>
              <legend style={styles.legend}>Subject Preferences</legend>
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
          <p style={styles.filterNote}>
            Please pick no more than 14 subjects for optimum results.
          </p>
          {warning && (
            <p style={{ color: 'red', textAlign: 'center', marginBottom: '1rem' }}>
              {warning}
            </p>
          )}
          <div style={{ width: '100%' }}>
  <div style={isMobile
    ? { ...styles.subjectGrid, ...mobileSubjectGrid }
    : styles.subjectGrid
  }>
    {filteredSubjects.map(({ name }) => (
      <div
        key={name}
        style={isMobile ? { ...styles.subjectRow, ...mobileOverrides.subjectRow } : styles.subjectRow}
      >
        <span style={styles.subjectName}>{name}</span>
        <div
  onClick={() => {
    const nextState =
      subjectStates[name] === 'none' ? 'learn' :
      subjectStates[name] === 'learn' ? 'teach' : 'none';
    setSubjectState(name, nextState);
  }}
  style={{
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: isMobile ? '100px' : '120px',
    height: isMobile ? '34px' : '38px',
    borderRadius: '999px',
    backgroundColor:
      subjectStates[name] === 'learn'
        ? '#aef4ff'
        : subjectStates[name] === 'teach'
          ? '#c3fccc'
          : '#e2e8f0',
    boxShadow:
      subjectStates[name] !== 'none'
        ? '0 0 6px rgba(0,0,0,0.2)'
        : 'inset 0 0 4px rgba(0,0,0,0.1)',
    color: subjectStates[name] === 'none' ? '#555' : '#065f46',
    fontWeight: 600,
    fontSize: isMobile ? '0.75rem' : '0.85rem',
    cursor: 'pointer',
    transition: 'all 0.25s ease',
    marginLeft: isMobile ? '8px' : '16px',
    userSelect: 'none'
  }}
  title="Click to cycle: None → Learn → Teach"
>
  {subjectStates[name] === 'learn'
    ? 'Learn'
    : subjectStates[name] === 'teach'
      ? 'Teach'
      : 'None'}
</div>
// edit the above if it doesn't look right 
            </div>
          ))}
      </div>
    </div>

        </fieldset>
      </div>

        <div style={styles.buttonContainer}>
          <button
            type="submit"
            style={{
              backgroundColor: '#5ccdc1',
              fontWeight: 'bold',
              padding: '0.5rem 1rem',
              borderRadius: '4px',
              color: '#fff',
              border: 'none',
              cursor: 'pointer'
            }}
          >
            Update Preferences
          </button>
          <button
            type="button"
            onClick={handleClear}
            style={{
              marginLeft: '1rem',
              backgroundColor: '#b0bec5', // Soft gray-blue
              fontWeight: 'bold',
              padding: '0.5rem 1rem',
              borderRadius: '4px',
              color: '#fff',
              border: 'none',
              cursor: 'pointer'
            }}
          >
            Clear Preferences
          </button>

        </div>
      </form>

      {warning && <p style={{ color: 'orange', textAlign: 'center' }}>{warning}</p>}
      {message && <p style={styles.error}>{message}</p>}
        
    </div>
  );
}
