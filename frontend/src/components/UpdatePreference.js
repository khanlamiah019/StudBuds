import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

/** CONSTANTS */
const DAYS = ["Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"];

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
  "All","Civil","Chemical","Electrical","Mechanical",
  "Computer Science","Math","Physics"
];

/** STYLES */
const styles = {
  container: { width:'60vw',maxWidth:'900px',minWidth:'300px',margin:'3rem auto',padding:'2rem',borderRadius:'12px',backgroundColor:'#fff',boxShadow:'0 6px 12px rgba(0,0,0,0.1)',textAlign:'left' },
  heading:   { textAlign:'center',marginBottom:'2rem' },
  fieldset:  { marginBottom:'2rem',padding:'1rem',border:'1px solid #ccc',borderRadius:'6px',display:'flex',flexDirection:'column',textAlign:'left' },
  legend:    { fontWeight:'bold',marginBottom:'1rem',textAlign:'center' },
  dayPill: selected => ({
    padding:'10px 16px',borderRadius:'20px',backgroundColor:selected?'#5ccdc1':'#f5f5f5',
    border:'1px solid #ccc',color:selected?'#fff':'#333',cursor:'pointer',userSelect:'none'
  }),
  dayPillContainer:{ display:'flex',flexWrap:'wrap',justifyContent:'center',gap:'12px',margin:'0 0 1.75rem 0' },
  filterBar:{ display:'flex',flexWrap:'wrap',justifyContent:'center',gap:'10px',marginBottom:'1rem' },
  filterButton:isActive=>({
    border:'1px solid #ccc',borderRadius:'8px',padding:'6px 12px',
    backgroundColor:isActive?'#5ccdc1':'#f5f5f5',color:isActive?'#fff':'#333',cursor:'pointer',fontWeight:'bold'
  }),
  filterNote:{ textAlign:'center',fontSize:'0.85rem',marginBottom:'1rem' },
  subjectGrid:{ display:'grid',gridTemplateColumns:'repeat(auto-fit,minmax(300px,1fr))',gap:'1rem',position:'relative' },
  subjectRow:{ display:'flex',justifyContent:'space-between',alignItems:'center',padding:'8px 16px',borderBottom:'1px solid #eee',position:'relative' },
  subjectName:{ flex:1,fontSize:'0.95rem' },
  switchWrapper:{ width:'180px',height:'36px',borderRadius:'18px',backgroundColor:'#e5e7eb',position:'relative',display:'flex',overflow:'hidden',boxShadow:'inset 0 0 5px rgba(0,0,0,0.1)' },
  switchZone:{ flex:1,display:'flex',alignItems:'center',justifyContent:'center',fontSize:'0.85rem',fontWeight:500,cursor:'pointer',zIndex:2 },
  switchCircle:state=>({
    position:'absolute',height:'28px',width:'60px',borderRadius:'14px',
    backgroundColor: state==='learn'?'#7dd3fc': state==='teach'?'#38bdf8':'#cbd5e1',
    top:'4px',
    left: state==='learn'?'4px': state==='teach'?'116px':'60px',
    transition:'left 0.25s ease, background-color 0.25s ease',zIndex:1
  }),
  buttonContainer:{ textAlign:'center',marginTop:'2rem' },
  error:{ color:'blue',textAlign:'center',marginTop:'1rem' },
};

export default function UpdatePreference({ userId }) {
  const navigate = useNavigate();

  const [selectedDays,  setSelectedDays]  = useState([]);
  const [subjectStates, setSubjectStates] = useState(() =>
    SUBJECTS_WITH_CATEGORIES.reduce((acc, sub) => ({ ...acc, [sub.name]: 'none' }), {})
  );
  const [activeCategory, setActiveCategory] = useState('All');
  const [message,        setMessage]        = useState('');

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
      })
      .catch(() => setMessage('Failed to clear preferences.'));
  };

  const handleDayClick = day => {
    setSelectedDays(prev =>
      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
    );
  };

  const setSubjectState = (subject, state) => {
    setSubjectStates(prev => ({ ...prev, [subject]: state }));
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

    axios.post(`/api/user/${userId}/preference`, {
      availableDays:   selectedDays.join(', '),
      subjectsToLearn: toLearn.join(', '),
      subjectsToTeach: toTeach.join(', ')
    })
    .then(()   => { setMessage('Preference updated successfully.'); navigate('/matchlist'); })
    .catch(err  => {
      let msg = 'Update failed.';
      const data = err.response?.data;
      if (data) {
        if (typeof data === 'string')     msg = data;
        else if (data.message)            msg = data.message;
        else if (data.error)              msg = data.error;
        else                              msg = JSON.stringify(data);
      }
      if (msg.includes('Too many')) {
        msg = 'Too many preferences picked. Please select fewer subjects.';
      }
      setMessage(msg);
    });
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
          <div style={styles.filterBar}>
            {CATEGORIES.map(cat => (
              <button
                key={cat}
                type="button"
                onClick={() => setActiveCategory(cat)}
                style={styles.filterButton(activeCategory===cat)}
              >
                {cat}
              </button>
            ))}
          </div>
          <p style={styles.filterNote}>
            Please pick no more than 14 subjects for optimum results.
          </p>
          <div style={styles.subjectGrid}>
            {filteredSubjects.map(({ name }) => (
              <div key={name} style={styles.subjectRow}>
                <span style={styles.subjectName}>{name}</span>
                <div style={styles.switchWrapper}>
                  <div style={styles.switchCircle(subjectStates[name])}/>
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
          </div>
        </fieldset>

        <div style={styles.buttonContainer}>
          <button type="submit">Update Preferences</button>
          <button
            type="button"
            onClick={handleClear}
            style={{ marginLeft: '1rem' }}
          >
            Clear Preferences
          </button>
        </div>
      </form>

      {message && <p style={styles.error}>{message}</p>}
    </div>
  );
}