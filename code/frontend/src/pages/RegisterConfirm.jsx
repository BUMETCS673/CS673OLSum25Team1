import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import {
  Container,
  Paper,
  TextField,
  Button,
  Typography,
  Box,
  Alert,
  LinearProgress,
} from "@mui/material";
import { setSelection } from "@testing-library/user-event/dist/cjs/event/selection/setSelection.js";

const RegisterConfirm = () => {
  const [confirmToken, setConfirmToken] = useState("");
  const [confirmationSuccess, setConfirmationSuccess] = useState(false);
  const [error, setError] = useState(undefined);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const { registerConfirm } = useAuth();

  /**
   * Sub
   * @param {*} e
   * @returns
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(undefined);
    setLoading(true);
    if (confirmToken.trim() === "") {
      setError("Field cannot be empty");
      setLoading(false);
      return;
    }

    try {
      const { success, error } = await registerConfirm(confirmToken);
      if (success) {
        // Show success alert for 2 seconds, then redirect
        setConfirmationSuccess(true);
        setTimeout(() => {
          navigate("/login", { replace: true });
        }, 1000);
      } else {
        if (error?.errorCode === "TOKEN_INVALID") {
          setError("Invalid confirmation token");
        } else {
          setError("Registration confirmation failed. Please try again.");
        }
      }
    } catch (err) {
      console.error("Unexpected error during registration", err);
      setError("Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container component="main" maxWidth="xs">
      <Box
        sx={{
          marginTop: 8,
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
        }}
      >
        <Paper elevation={3} sx={{ p: 4, width: "100%" }}>
          <Typography component="h1" variant="h5" align="center" gutterBottom>
            Confirm Your Email
          </Typography>
          <Typography variant="body1" align="center" sx={{ mb: 3 }}>
            We've sent a confirmation code to your email. Please enter it below
            to complete your registration.
            <br />
            <br />
          </Typography>

          {loading && <LinearProgress sx={{ mb: 2 }} />}

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          {confirmationSuccess && (
            <Alert severity="success" sx={{ mb: 2 }}>
              Confirmation successful! Redirecting to login...
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <TextField
              label="Confirmation Code"
              variant="outlined"
              fullWidth
              value={confirmToken}
              onChange={(e) => setConfirmToken(e.target.value)}
              margin="normal"
              autoFocus
            />

            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
            >
              Confirm
            </Button>
          </form>
          <Typography
            variant="body2"
            align="center"
            color="textSecondary"
            sx={{ mt: 2 }}
          >
            Didn’t receive the code?{" "}
            <Button
              size="small"
              variant="text"
              onClick={() => alert("Resend feature to be implemented")}
            >
              Resend
            </Button>
          </Typography>
        </Paper>
      </Box>
    </Container>
  );
};

export default RegisterConfirm;
