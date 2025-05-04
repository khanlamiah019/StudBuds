import React, { useState, useEffect } from 'react';
import axios from '../axiosSetup';
import { useNavigate } from 'react-router-dom';

const DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
const MOBILE_BREAKPOINT = 600;

const SUBJECTS_WITH_CATEGORIES = [
  { name: "Calculus", category: "Math" },
  { name: "Differential Equations", category: "Math" },
  { name: "Linear Algebra", category: "Math" },
  { name: "Probability", category: "Math" },
  { name: "Discrete Math", category: "Math" },
  { name: "General Chemistry", category: "Chemical" },
  { name: "Organic Chemistry", category: "Chemical" },
  { name: "Biochemistry", category: "Chemical" },
  { name: "Circuits", category: "Electrical" },
  { name: "Electronics", category: "Electrical" },
  { name: "Digital Logic Design", category: "Electrical" },
  { name: "Computer Architecture", category: "Electrical" },
  { name: "Computer Science 101", category: "Computer Science" },
  { name: "Data Structures and Algorithms", category: "Computer Science" },
  { name: "Operating Systems", category: "Computer Science" },
  { name: "Computer Networks", category: "Computer Science" },
  { name: "Software Engineering", category: "Computer Science" },
  { name: "Concrete Structures", category: "Civil" },
  { name: "Steel Structures", category: "Civil" },
  { name: "Transportation Engineering", category: "Civil" },
  { name: "Environmental Engineering", category: "Civil" },
  { name: "Thermodynamics", category: "Mechanical" },
  { name: "Fluid Mechanics", category: "Mechanical" },
  { name: "Heat Transfer", category: "Mechanical" },
  { name: "Mechanical Design", category: "Mechanical" },
  { name: "Manufacturing Processes", category: "Mechanical" },
  { name: "Classical Mechanics", category: "Physics" },
  { name: "Electromagnetism", category: "Physics" },
  { name: "Quantum Mechanics", category: "Physics" },
  { name: "Optics", category: "Physics" }
];

const CATEGORIES = [
  "All", "Civil", "Chemical", "Electrical", "Mechanical",
  "Computer Science", "Math", "Physics"
];

const BackgroundAnimation = () => (
  <div style={{
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    background: 'radial-gradient(circle at bottom, #f0f9ff, #a2d4e6)',
    overflow: 'hidden',
    zIndex: -1,
    animation: 'backgroundFlow 20s infinite linear'
  }}>
    {[...Array(20)].map((_, i) => (
      <div
        key={i}
        style={{
          position: 'absolute',
          bottom: '-40px',
          left: `${Math.random() * 100}%`,
          width: '20px',
          height: '20px',
          backgroundColor: '#bae6fd',
          borderRadius: '50%',
          opacity: 0.6,
          animation: `bubbleUp ${10 + Math.random() * 10}s ease-in infinite`,
          animationDelay: `${Math.random() * 10}s`
        }}
      />
    ))}
    <style>{`
      @keyframes bubbleUp {
        0% { transform: translateY(0); opacity: 0.6; }
        100% { transform: translateY(-120vh); opacity: 0; }
      }
    `}</style>
  </div>
);

export default function UpdatePreference({ userId }) {
  const navigate = useNavigate();
  const [isMobile, setIsMobile] = useState(window.innerWidth <= MOBILE_BREAKPOINT);
  const [selectedDays, setSelectedDays] = useState([]);
  const [subjectStates, setSubjectStates] = useState(() =>
    SUBJECTS_WITH_CATEGORIES.reduce((acc, sub) => ({ ...acc, [sub.name]: 'none' }), {})
  );
  const [activeCategory, setActiveCategory] = useState('All');
  const [message, setMessage] = useState('');
  const [warning, setWarning] = useState('');

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= MOBILE_BREAKPOINT);
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  useEffect(() => {
    axios.get(`/api/user/${userId}/preference`).then(({ data }) => {
      setSelectedDays(
        data.availableDays ? data.availableDays.split(',').map(d => d.trim()) : []
      );
      const learn = data.subjectsToLearn?.split(',').map(s => s.trim()) || [];
      const teach = data.subjectsToTeach?.split(',').map(s => s.trim()) || [];
      setSubjectStates(prev => {
        const updated = { ...prev };
        Object.keys(updated).forEach(sub => {
          updated[sub] = learn.includes(sub) ? 'learn'
            : teach.includes(sub) ? 'teach' : 'none';
        });
        return updated;
      });
    });
  }, [userId]);

  const setSubjectState = (subject, state) => {
    const updated = { ...subjectStates, [subject]: state };
    const selectedCount = Object.values(updated).filter(val => val !== 'none').length;
    setWarning(selectedCount > 14 ? `Selected ${selectedCount}. Limit is 14.` : '');
    setSubjectStates(updated);
  };

  const handleSubmit = e => {
    e.preventDefault();
    const toLearn = [], toTeach = [];
    Object.entries(subjectStates).forEach(([sub, st]) => {
      if (st === 'learn') toLearn.push(sub);
      if (st === 'teach') toTeach.push(sub);
    });
    const total = toLearn.length + toTeach.length;
    if (total === 0) return setWarning("Select at least one subject.");
    if (total > 14) return setWarning(`Selected ${total}. Limit is 14.`);
    axios.post(`/api/user/${userId}/preference`, {
      availableDays: selectedDays.join(', '),
      subjectsToLearn: toLearn.join(', '),
      subjectsToTeach: toTeach.join(', ')
    }).then(() => navigate('/matchlist'))
      .catch(() => setMessage("Failed to update preferences."));
  };

  const filteredSubjects = activeCategory === 'All'
    ? SUBJECTS_WITH_CATEGORIES
    : SUBJECTS_WITH_CATEGORIES.filter(s => s.category === activeCategory);

  return (
    <div style={{ position: 'relative', minHeight: '100vh' }}>
      <BackgroundAnimation />
      <div style={{ maxWidth: '700px', margin: '0 auto', padding: '2rem', backgroundColor: '#ffffffcc', borderRadius: '16px', position: 'relative', zIndex: 1 }}>
        <h2 style={{ textAlign: 'center' }}>Update Preferences</h2>
        <form onSubmit={handleSubmit}>
          <fieldset>
            <legend>Available Days</legend>
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
              {DAYS.map(day => (
                <span
                  key={day}
                  style={{
                    padding: '8px 14px',
                    borderRadius: '20px',
                    backgroundColor: selectedDays.includes(day) ? '#5ccdc1' : '#eee',
                    cursor: 'pointer'
                  }}
                  onClick={() =>
                    setSelectedDays(prev =>
                      prev.includes(day) ? prev.filter(d => d !== day) : [...prev, day]
                    )
                  }
                >
                  {day}
                </span>
              ))}
            </div>
          </fieldset>

          <fieldset>
            <legend>Subject Preferences</legend>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              {CATEGORIES.map(cat => (
                <button key={cat} type="button" onClick={() => setActiveCategory(cat)} style={{ backgroundColor: activeCategory === cat ? '#5ccdc1' : '#ddd', padding: '6px 12px', borderRadius: '8px', border: 'none' }}>{cat}</button>
              ))}
            </div>
            <p style={{ fontSize: '0.85rem', textAlign: 'center' }}>🎯 Stay under 14 subjects to match with the best partners!</p>
            {warning && <p style={{ color: 'red', textAlign: 'center' }}>{warning}</p>}
            {filteredSubjects.map(({ name }) => (
              <div key={name} style={{ display: 'flex', justifyContent: 'space-between', marginTop: '10px' }}>
                <span>{name}</span>
                <button
                  type="button"
                  onClick={() => {
                    const next = subjectStates[name] === 'none' ? 'learn' : subjectStates[name] === 'learn' ? 'teach' : 'none';
                    setSubjectState(name, next);
                  }}
                  style={{
                    borderRadius: '999px',
                    padding: '6px 16px',
                    border: 'none',
                    backgroundColor: subjectStates[name] === 'learn' ? '#aef4ff' : subjectStates[name] === 'teach' ? '#c3fccc' : '#e2e8f0',
                    color: '#065f46',
                    fontWeight: 'bold',
                    cursor: 'pointer'
                  }}
                >
                  {subjectStates[name] === 'none' ? 'None' : subjectStates[name]}
                </button>
              </div>
            ))}
          </fieldset>

          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <button type="submit" style={{ backgroundColor: '#5ccdc1', color: '#fff', padding: '0.5rem 1rem', borderRadius: '6px', border: 'none', cursor: 'pointer', marginRight: '1rem' }}>Update</button>
            <button type="button" onClick={() => window.location.reload()} style={{ backgroundColor: '#ccc', color: '#fff', padding: '0.5rem 1rem', borderRadius: '6px', border: 'none', cursor: 'pointer' }}>Reset</button>
          </div>
          {message && <p style={{ color: 'blue', textAlign: 'center', marginTop: '1rem' }}>{message}</p>}
        </form>
      </div>
    </div>
  );
}
