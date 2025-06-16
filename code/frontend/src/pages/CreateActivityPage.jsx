import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createActivity } from '../services/api'; // assuming createActivity sends the request to backend

const CreateActivityPage = () => {
  const [form, setForm] = useState({
    name: '',
    startDateTime: '',
    endDateTime: '',
    location: '',
  });

  const navigate = useNavigate();
  
  const token = localStorage.getItem('authToken'); // Assuming JWT is stored in localStorage

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("Submitting form:", form); // 👈 Add this line for debugging
    try {
      if (!token) {
        alert('You need to be logged in.');
        return;
      }

      const activityData = {
        name: form.name,
        description: "description",  // Assuming the description field is required or can be optional
        location: form.location,
        startDateTime: form.startDateTime,
        endDateTime: form.endDateTime
      };

      await createActivity(activityData, token);  // Send activity data with token
      alert('Activity created!');
      navigate('/activity-discovery');
    } catch (err) {
      console.error("Error creating activity:", err);
      alert('Failed to create activity.');
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '8px', marginTop: '20px' }}>
      <input
        name="name"
        placeholder="Activity Name"
        onChange={(e) => setForm({ ...form, name: e.target.value })}
        required
      />
      <input
        type="datetime-local"
        name="startDateTime"
        onChange={(e) => setForm({ ...form, startDateTime: e.target.value })}
        required
      />
      <input
        type="datetime-local"
        name="endDateTime"
        onChange={(e) => setForm({ ...form, endDateTime: e.target.value })}
        required
      />
      <input
        name="location"
        placeholder="Location"
        onChange={(e) => setForm({ ...form, location: e.target.value })}
        required
      />
      <button type="submit">Create</button>
    </form>
  );
};

export default CreateActivityPage;
