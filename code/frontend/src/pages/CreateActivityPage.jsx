import React, { useState } from 'react';
import { createActivity } from '../services/api';

const CreateActivityPage = () => {
  const [form, setForm] = useState({ title: '', datetime: '', location: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    await createActivity(form);
    alert('Activity created!');
  };

  return (
    <form onSubmit={handleSubmit}>
      <input name="title" onChange={(e) => setForm({ ...form, title: e.target.value })} placeholder="Title" required />
      <input type="datetime-local" name="datetime" onChange={(e) => setForm({ ...form, datetime: e.target.value })} required />
      <input name="location" onChange={(e) => setForm({ ...form, location: e.target.value })} placeholder="Location" required />
      <button type="submit">Create</button>
    </form>
  );
};

export default CreateActivityPage;
