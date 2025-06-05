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
