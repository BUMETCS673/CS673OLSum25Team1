import axios from "axios";
import { jwtUtils } from "../utils/jwt";

// Create the Axios instance with base URL from .env
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL, // e.g. http://localhost:3232/v1
  timeout: 5000,
});

// Automatically attach JWT token if available
api.interceptors.request.use(
  (config) => {
    const token = jwtUtils.getToken("auth_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Optionally handle response errors globally
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    return Promise.reject(error);
  }
);

// Export the createActivity function for use in CreateActivityPage
export const createActivity = async (activityData) => {
  try {
    const response = await api.post("/activities", activityData);
    return response.data;
  } catch (error) {
    console.error("Error creating activity:", error);
    throw error;
  }
};

// Default export is the Axios instance
export default api;
