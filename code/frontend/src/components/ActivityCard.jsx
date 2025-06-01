import React from 'react';

const ActivityCard = ({ activity }) => (
  <div className="border p-3 my-2">
    <h2>{activity.title}</h2>
    <p>{new Date(activity.datetime).toLocaleString()}</p>
    <p>{activity.location}</p>
  </div>
);

export default ActivityCard;