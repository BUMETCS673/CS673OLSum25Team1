// src/services/api.js
import axios from "axios";
import { jwtUtils } from "../utils/jwt";
import { useNavigate } from "react-router-dom";
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  timeout: 5000,
});

api.interceptors.request.use(
  (config) => {
    const token = jwtUtils.getToken("auth_token");
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const navigate = useNavigate();
      navigate("/login");
    }

    return Promise.reject(error);
  }
);

export default api;
