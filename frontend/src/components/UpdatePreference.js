import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const DAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];

const SUBJECTS = [
  "Calculus 1", "Calculus 2", "Vector Calculus", "Differential Equations", "Linear Algebra", "Probability", 
  "Physics 1", "Physics 2", "Physics 3", "General Chemistry", "Organic Chemistry", "Biochemistry", 
  "Electronics I", "Electronics II", "Digital Logic", "Computer Architecture", "Data Structures and Algorithms",
  "Operating Systems", "Computer Networks", "Software Engineering", 
  "Concrete Structures", "Steel Structures", "Transportation Engineering", "Environmental Engineering",
  "Thermodynamics", "Fluid Mechanics", "Heat Transfer", "Mechanical Design", "Manufacturing Processes"
];

function UpdatePreference({ userId }) {
  const [selectedDays, setSelectedDays] = useState([]);
  const [selectedLearnSubjects, setSelectedLearnSubjects] = useState([]);
  const [selectedTeachSubjects, setSelectedTeachSubjects] = useState([]);
  const [message, setMessage] = useState('');
  
  const navigate = useNavigate(); // Added to enable navigation

  const containerStyle = {
    maxWidth: '600px',
    margin: '2rem auto',
    padding: '2rem'
  };

  const fieldsetStyle = {
    marginBottom: '1.5rem',
    padding: '1rem',
    border: '1px solid #ddd',
    borderRadius: '6px'
  };

  const legendStyle = {
    marginBottom: '0.5rem',
    fontWeight: 'bold'
  };

  const listStyle = {
    listStyle: 'none',
    paddingLeft: 0,
    margin: 0
  };

  const labelStyle = {
    display: 'inline-flex',
    alignItems: 'center',
    gap: '0.1rem',
    marginBottom: '0.5rem',
    fontSize: '0.9rem',
    color: '#555'
  };

  const handleCheckboxChange = (option, stateArray, setter) => {
    if (stateArray.includes(option)) {
      setter(stateArray.filter(item => item !== option));
    } else {
      setter([...stateArray, option]);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const preferenceData = {
      availableDays: selectedDays.join(', '),
      subjectsToLearn: selectedLearnSubjects.join(', '),
      subjectsToTeach: selectedTeachSubjects.join(', ')
    };
    axios.post(`/api/user/${userId}/preference`, preferenceData)
      .then(() => {
        // On successful update, navigate directly to the match page
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

  return (
    <div style={containerStyle}>
      <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Update Your Preferences</h2>
      <form onSubmit={handleSubmit}>
        <fieldset style={fieldsetStyle}>
          <legend style={legendStyle}>Available Days</legend>
          <ul style={listStyle}>
            {DAYS.map(day => (
              <li key={day}>
                <label style={labelStyle}>
                  <input
                    type="checkbox"
                    value={day}
                    checked={selectedDays.includes(day)}
                    onChange={() => handleCheckboxChange(day, selectedDays, setSelectedDays)}
                  />
                  {day}
                </label>
              </li>
            ))}
          </ul>
        </fieldset>

        <fieldset style={fieldsetStyle}>
          <legend style={legendStyle}>Subjects to Learn</legend>
          <ul style={listStyle}>
            {SUBJECTS.map(subject => (
              <li key={subject}>
                <label style={labelStyle}>
                  <input
                    type="checkbox"
                    value={subject}
                    checked={selectedLearnSubjects.includes(subject)}
                    onChange={() => handleCheckboxChange(subject, selectedLearnSubjects, setSelectedLearnSubjects)}
                  />
                  {subject}
                </label>
              </li>
            ))}
          </ul>
        </fieldset>

        <fieldset style={fieldsetStyle}>
          <legend style={legendStyle}>Subjects to Teach</legend>
          <ul style={listStyle}>
            {SUBJECTS.map(subject => (
              <li key={subject}>
                <label style={labelStyle}>
                  <input
                    type="checkbox"
                    value={subject}
                    checked={selectedTeachSubjects.includes(subject)}
                    onChange={() => handleCheckboxChange(subject, selectedTeachSubjects, setSelectedTeachSubjects)}
                  />
                  {subject}
                </label>
              </li>
            ))}
          </ul>
        </fieldset>

        <div style={{ textAlign: 'center' }}>
          <button type="submit">Update Preference</button>
        </div>
      </form>
      {message && <p style={{ color: 'red', marginTop: '1rem', textAlign: 'center' }}>{message}</p>}
    </div>
  );
}

export default UpdatePreference;
