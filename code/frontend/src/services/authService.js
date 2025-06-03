import api from './api';
import { jwtUtils } from '../utils/jwt';

export const authService = {
  register: async (username, email, password) => {
    try {
      const response = await api.post('/register', { username, email, password });
      return {
        success: true,
        error: null
      };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || 'Register failed'
      };
    }
  },

  login: async (username, password) => {
    try {
      const response = await api.post('/login', { username, password });
      const { data } = response.data;
      
      jwtUtils.setToken('auth_token', data.token);
      //jwtUtils.setToken('refresh_token', refreshToken);
      localStorage.setItem('userData', JSON.stringify({userId: data.userId, username: data.username, userEmail: data.email}));
      
      return { success: true, userData: {userId: data.userId, username: data.username, userEmail: data.email}, error: null};
    } catch (error) {
      return { 
        success: false, 
        error: error.response?.data?.message || 'Login failed' 
      };
    }
  },

  logout: async () => {
    try {
      await api.post('/logout');
    } catch (error) {
      throw new Error('API logout failed');
    } finally {
      jwtUtils.removeToken('auth_token');
      jwtUtils.removeToken('refresh_token');
      localStorage.removeItem('userData');
    }
  },

  getCurrentUser: async () => {
    const storedUserData = localStorage.getItem('userData');
    const token = jwtUtils.getToken('auth_token');

    if (storedUserData && token && !jwtUtils.isTokenExpired(token)) {
      try {
        return JSON.parse(storedUserData);
      } catch (error) {
        jwtUtils.removeToken('auth_token');
        localStorage.removeItem('userData');
        return null;
      }
    } else {
      jwtUtils.removeToken('auth_token');
      localStorage.removeItem('userData');
      return null;
    }
  },

  isAuthenticated: () => {
    const token = jwtUtils.getToken('auth_token');
    return Boolean(token && !jwtUtils.isTokenExpired(token));
  }
};