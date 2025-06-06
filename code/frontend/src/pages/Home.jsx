import { useAuth } from "../contexts/AuthContext";
import { Container, Typography, Box, Button, Stack, Paper } from "@mui/material";
import { Link as RouterLink } from "react-router-dom";

export default function Home() {
  const { user } = useAuth();

  return (
    <Container maxWidth="sm" sx={{ mt: 8 }}>
      <Paper elevation={3} sx={{ p: 4, borderRadius: 3, textAlign: "center" }}>
        <Typography variant="h4" gutterBottom>
          Welcome, {user.username}!
        </Typography>
        <Typography variant="subtitle1" gutterBottom>
          Email: {user.userEmail}
        </Typography>

        <Box sx={{ mt: 4 }}>
          <Typography variant="h6" gutterBottom>
            Get Started
          </Typography>
          <Stack spacing={2} direction="column" alignItems="center">
            <Button
              variant="contained"
              color="primary"
              component={RouterLink}
              to="/activity-discovery"
              fullWidth
            >
              Discover Activities
            </Button>
            <Button
              variant="outlined"
              color="secondary"
              component={RouterLink}
              to="/create-activity"
              fullWidth
            >
              Create New Activity
            </Button>
          </Stack>
        </Box>
      </Paper>
    </Container>
  );
}
