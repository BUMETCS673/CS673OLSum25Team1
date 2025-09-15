import React, { useEffect, useState } from 'react';
import ActivityCard from '../components/ActivityCard';
import { fetchActivities } from '../services/api';

const DiscoverPage = () => {
  const [activities, setActivities] = useState([]);

  useEffect(() => {
    fetchActivities().then(setActivities);
  }, []);

  return (
    <div className="p-4">
      <h1>Discover Activities</h1>
      <div>
        {activities.map((activity) => (
          <ActivityCard key={activity.id} activity={activity} />
        ))}
      </div>
    </div>
  );
};

export default DiscoverPage;
