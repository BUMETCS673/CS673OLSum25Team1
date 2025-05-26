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
      await api.post('/auth/logout');
    } finally {
      jwtUtils.removeToken('auth_token');
      jwtUtils.removeToken('refresh_token');
    }
  },

  getCurrentUser: async () => {
    try {
      const response = await api.get('/auth/verify');
      return response.data;
    } catch (error) {
      return null;
    }
  },

  isAuthenticated: () => {
    const token = jwtUtils.getToken();
    return token && !jwtUtils.isTokenExpired(token);
  }
};