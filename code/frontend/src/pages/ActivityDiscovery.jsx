<<<<<<< HEAD
import React, { useEffect, useState } from "react";
import {
  TextField,
  Select,
  MenuItem,
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Container,
} from "@mui/material";

const ActivityDiscovery = () => {
  const [activities, setActivities] = useState([]);
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("");

  useEffect(() => {
    fetchActivities();
  }, [search, sort]);

  const fetchActivities = async () => {
    const mockData = [
      {
        id: "1",
        name: "Yoga Class",
        location: "Gym A",
        startDateTime: "2025-06-10T09:00",
        endDateTime: "2025-06-10T10:00",
      },
      {
        id: "2",
        name: "Basketball Match",
        location: "Court B",
        startDateTime: "2025-06-11T15:00",
        endDateTime: "2025-06-11T17:00",
      },
      {
        id: "3",
        name: "Coding Workshop",
        location: "Lab 101",
        startDateTime: "2025-06-09T14:00",
        endDateTime: "2025-06-09T16:00",
      },
    ];

    // Sort
    let sorted = [...mockData];
    if (sort === "name") {
      sorted.sort((a, b) => a.name.localeCompare(b.name));
    } else if (sort === "startDateTime") {
      sorted.sort((a, b) => new Date(a.startDateTime) - new Date(b.startDateTime));
    }

    // Filter by search
    const filtered = sorted.filter((activity) =>
      activity.name.toLowerCase().includes(search.toLowerCase())
    );

    setActivities(filtered);
  };

  return (
    <Container>
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>
          Discover Activities
        </Typography>
        <Box sx={{ display: "flex", gap: 2, mb: 3 }}>
          <TextField
            id="search"
            name="search"
            label="Search Activities"
            variant="outlined"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            fullWidth
          />
          <Select
            id="sort"
            name="sort"
            value={sort}
            onChange={(e) => setSort(e.target.value)}
            displayEmpty
            fullWidth
          >
            <MenuItem value="">No Sort</MenuItem>
            <MenuItem value="name">Sort by Name</MenuItem>
            <MenuItem value="startDateTime">Sort by Date</MenuItem>
          </Select>
        </Box>
        <Grid container spacing={3}>
          {activities.length > 0 ? (
            activities.map((activity) => (
              <Grid item xs={12} sm={6} md={4} key={activity.id}>
                <Card>
                  <CardContent>
                    <Typography variant="h6">{activity.name}</Typography>
                    <Typography variant="body2">
                      {new Date(activity.startDateTime).toLocaleString()} —{" "}
                      {new Date(activity.endDateTime).toLocaleString()}
                    </Typography>
                    <Typography variant="body2">{activity.location}</Typography>
                  </CardContent>
                </Card>
              </Grid>
            ))
          ) : (
            <Typography variant="body1" sx={{ ml: 2 }}>
              No activities found.
            </Typography>
          )}
        </Grid>
      </Box>
    </Container>
  );
};

export default ActivityDiscovery;
=======
import React, { useEffect, useState } from "react";
import { activityService } from "./services/activityService";
import {
  TextField,
  Select,
  MenuItem,
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Container,
} from "@mui/material";

const ActivityDiscovery = () => {
  const [activities, setActivities] = useState([]);
  const [search, setSearch] = useState("");
  const [sort, setSort] = useState("");

  useEffect(() => {
    fetchActivities();
  }, [search, sort]);

  const fetchActivities = async () => {
    try {
      const data = await activityService.getActivities(search, sort);
      setActivities(data);
    } catch (error) {
      console.error("Failed to fetch activities:", error);
    }
  };

  return (
    <Container>
      <Box sx={{ mt: 4 }}>
        <Typography variant="h4" gutterBottom>
          Discover Activities
        </Typography>
        <Box sx={{ display: "flex", gap: 2, mb: 3 }}>
          <TextField
            label="Search Activities"
            variant="outlined"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            fullWidth
          />
          <Select
            value={sort}
            onChange={(e) => setSort(e.target.value)}
            displayEmpty
            fullWidth
          >
            <MenuItem value="">No Sort</MenuItem>
            <MenuItem value="title">Sort by Title</MenuItem>
            <MenuItem value="date">Sort by Date</MenuItem>
          </Select>
        </Box>
        <Grid container spacing={3}>
          {activities.map((activity) => (
            <Grid item xs={12} sm={6} md={4} key={activity.id}>
              <Card>
                <CardContent>
                  <Typography variant="h6">{activity.title}</Typography>
                  <Typography variant="body2">
                    {new Date(activity.dateTime).toLocaleString()}
                  </Typography>
                  <Typography variant="body2">{activity.location}</Typography>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      </Box>
    </Container>
  );
};

export default ActivityDiscovery;
>>>>>>> 54ec64d268892d113ae16f829891470804b4c3fd
