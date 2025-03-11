import React, { useState } from 'react';
import axios from 'axios';

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

  // Container style
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

  // Grid: two columns per row.
  const checkboxGroupStyle = {
    display: 'grid',
    gridTemplateColumns: 'repeat(2, 1fr)',
    gap: '0.5rem'
  };

  // Each list item is a flex container.
  const listItemStyle = {
    display: 'flex',
    alignItems: 'center',
    marginBottom: '0.5rem'
  };

  // A fixed-width container for the checkbox ensures all text lines up.
  const checkboxContainerStyle = {
    width: '20px', // fixed width for checkbox
    display: 'flex',
    justifyContent: 'center'
  };

  // Text style with a small left margin.
  const textStyle = {
    marginLeft: '0.5rem'
  };

  const listStyle = {
    listStyle: 'none',
    padding: 0,
    margin: 0
  };

  const submitBtnStyle = {
    padding: '0.75rem 2rem',
    fontSize: '1rem',
    backgroundColor: '#5ccdc1',
    color: '#ffffff',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'background-color 0.3s ease',
    display: 'block',
    margin: '0 auto'
  };

  const messageStyle = {
    marginTop: '1rem',
    color: 'red',
    textAlign: 'center'
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
      .then(() => setMessage('Preference updated successfully.'))
      .catch(error => setMessage(error.response?.data || 'Update failed.'));
  };

  return (
    <div style={containerStyle}>
      <h2 style={{ textAlign: 'center', marginBottom: '1.5rem' }}>Update Your Preferences</h2>
      <form onSubmit={handleSubmit}>
        <fieldset style={fieldsetStyle}>
          <legend style={legendStyle}>Available Days</legend>
          <ul style={listStyle}>
            {DAYS.map(day => (
              <li key={day} style={listItemStyle}>
                <div style={checkboxContainerStyle}>
                  <input
                    type="checkbox"
                    value={day}
                    checked={selectedDays.includes(day)}
                    onChange={() => handleCheckboxChange(day, selectedDays, setSelectedDays)}
                  />
                </div>
                <span style={textStyle}>{day}</span>
              </li>
            ))}
          </ul>
        </fieldset>

        <fieldset style={fieldsetStyle}>
          <legend style={legendStyle}>Subjects to Learn</legend>
          <ul style={listStyle}>
            {SUBJECTS.map(subject => (
              <li key={subject} style={listItemStyle}>
                <div style={checkboxContainerStyle}>
                  <input
                    type="checkbox"
                    value={subject}
                    checked={selectedLearnSubjects.includes(subject)}
                    onChange={() => handleCheckboxChange(subject, selectedLearnSubjects, setSelectedLearnSubjects)}
                  />
                </div>
                <span style={textStyle}>{subject}</span>
              </li>
            ))}
          </ul>
        </fieldset>

        <fieldset style={fieldsetStyle}>
          <legend style={legendStyle}>Subjects to Teach</legend>
          <ul style={listStyle}>
            {SUBJECTS.map(subject => (
              <li key={subject} style={listItemStyle}>
                <div style={checkboxContainerStyle}>
                  <input
                    type="checkbox"
                    value={subject}
                    checked={selectedTeachSubjects.includes(subject)}
                    onChange={() => handleCheckboxChange(subject, selectedTeachSubjects, setSelectedTeachSubjects)}
                  />
                </div>
                <span style={textStyle}>{subject}</span>
              </li>
            ))}
          </ul>
        </fieldset>

        <div style={{ textAlign: 'center' }}>
          <button type="submit" style={submitBtnStyle}>Update Preference</button>
        </div>
      </form>
      {message && <p style={messageStyle}>{message}</p>}
    </div>
  );
}

export default UpdatePreference;
