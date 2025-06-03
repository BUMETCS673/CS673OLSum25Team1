import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { authService } from './authService';
import api from './api'; // Dependency to be mocked
import { jwtUtils } from '../utils/jwt'; // Dependency to be mocked

// Mock dependencies
vi.mock('./api', () => ({
  default: {
    post: vi.fn(),
  },
}));

vi.mock('../utils/jwt', () => ({
  jwtUtils: {
    setToken: vi.fn(),
    removeToken: vi.fn(),
    getToken: vi.fn(),
    isTokenExpired: vi.fn(),
  },
}));

// Mock localStorage
const localStorageMock = (() => {
  let store = {};
  return {
    getItem: vi.fn((key) => store[key] || null),
    setItem: vi.fn((key, value) => {
      store[key] = value.toString();
    }),
    removeItem: vi.fn((key) => {
      delete store[key];
    }),
    clear: vi.fn(() => {
      store = {};
    }),
  };
})();
Object.defineProperty(window, 'localStorage', { value: localStorageMock });


describe('authService', () => {
  beforeEach(() => {
    // Reset mocks before each test
    vi.clearAllMocks();
    localStorageMock.clear(); // Clear our mock store
  });

  describe('register', () => {
    it('should return success on successful registration', async () => {
      api.post.mockResolvedValue({}); // Simulate a successful API call
      const result = await authService.register('testuser', 'test@bu.edu', 'password');
      expect(api.post).toHaveBeenCalledWith('/register', { username: 'testuser', email: 'test@bu.edu', password: 'password' });
      expect(result).toEqual({ success: true, error: null });
    });

    it('should return error message from API on registration failure', async () => {
      const errorMessage = 'Email already taken';
      api.post.mockRejectedValue({ response: { data: { message: errorMessage } } });
      const result = await authService.register('testuser', 'test@bu.edu', 'password');
      expect(result).toEqual({ success: false, error: errorMessage });
    });

    it('should return default error message on generic registration failure', async () => {
      api.post.mockRejectedValue(new Error('Network error'));
      const result = await authService.register('testuser', 'test@bu.edu', 'password');
      expect(result).toEqual({ success: false, error: 'Register failed' });
    });
  });

  describe('login', () => {
    const loginCredentials = { username: 'testuser', password: 'password' };
    const mockUserData = { userId: '123', username: 'testuser', email: 'test@bu.edu' };
    const mockToken = 'mock-jwt-token';

    it('should set token and user data on successful login', async () => {
      api.post.mockResolvedValue({ data: { data: { token: mockToken, ...mockUserData } } });
      const result = await authService.login(loginCredentials.username, loginCredentials.password);

      expect(api.post).toHaveBeenCalledWith('/login', loginCredentials);
      expect(jwtUtils.setToken).toHaveBeenCalledWith('auth_token', mockToken);
      expect(localStorage.setItem).toHaveBeenCalledWith('userData', JSON.stringify({ userId: '123', username: 'testuser', userEmail: 'test@bu.edu' }));
      expect(result).toEqual({ success: true, userData: { userId: '123', username: 'testuser', userEmail: 'test@bu.edu' }, error: null });
    });

    it('should return error message from API on login failure', async () => {
      const errorMessage = 'Invalid credentials';
      api.post.mockRejectedValue({ response: { data: { message: errorMessage } } });
      const result = await authService.login(loginCredentials.username, loginCredentials.password);

      expect(result).toEqual({ success: false, error: errorMessage });
      expect(jwtUtils.setToken).not.toHaveBeenCalled();
      expect(localStorage.setItem).not.toHaveBeenCalled();
    });

    it('should return default error message on generic login failure', async () => {
      api.post.mockRejectedValue(new Error('Network error'));
      const result = await authService.login(loginCredentials.username, loginCredentials.password);

      expect(result).toEqual({ success: false, error: 'Login failed' });
      expect(jwtUtils.setToken).not.toHaveBeenCalled();
      expect(localStorage.setItem).not.toHaveBeenCalled();
    });
  });

  describe('logout', () => {
    it('should remove token and user data on logout, regardless of API call success', async () => {
      api.post.mockResolvedValue({}); // Simulate API success
      await authService.logout();

      expect(jwtUtils.removeToken).toHaveBeenCalledWith('auth_token');
      // expect(jwtUtils.removeToken).toHaveBeenCalledWith('refresh_token'); // Uncomment if refresh token is used
      expect(localStorage.removeItem).toHaveBeenCalledWith('userData');
    });

    it('should remove token and user data on logout, even if API call fails (due to finally)', async () => {
      api.post.mockRejectedValue(new Error('API logout failed')); // Simulate API failure
      
      // Expect the logout function to throw when the API call within it fails, but still perform cleanup
      await expect(authService.logout()).rejects.toThrow('API logout failed');

      expect(jwtUtils.removeToken).toHaveBeenCalledWith('auth_token');
      // expect(jwtUtils.removeToken).toHaveBeenCalledWith('refresh_token');
      expect(localStorage.removeItem).toHaveBeenCalledWith('userData');
    });
  });

  describe('getCurrentUser', () => {
    const mockUser = { userId: '1', username: 'test' };
    const mockUserString = JSON.stringify(mockUser);

    it('should return parsed user data if token and localStorage data are valid', async () => {
      localStorage.setItem('userData', mockUserString);
      jwtUtils.getToken.mockReturnValue('valid-token');
      jwtUtils.isTokenExpired.mockReturnValue(false);

      const user = await authService.getCurrentUser();
      expect(user).toEqual(mockUser);
    });

    it('should return null and clear data if no userData in localStorage', async () => {
      jwtUtils.getToken.mockReturnValue('valid-token'); // Token might still be there
      jwtUtils.isTokenExpired.mockReturnValue(false);

      const user = await authService.getCurrentUser();
      expect(user).toBeNull();
      expect(jwtUtils.removeToken).toHaveBeenCalledWith('auth_token');
      expect(localStorage.removeItem).toHaveBeenCalledWith('userData');
    });

    it('should return null and clear data if no token', async () => {
      localStorage.setItem('userData', mockUserString); // UserData might still be there
      jwtUtils.getToken.mockReturnValue(null);

      const user = await authService.getCurrentUser();
      expect(user).toBeNull();
      expect(jwtUtils.removeToken).toHaveBeenCalledWith('auth_token');
      expect(localStorage.removeItem).toHaveBeenCalledWith('userData');
    });

    it('should return null and clear data if token is expired', async () => {
      localStorage.setItem('userData', mockUserString);
      jwtUtils.getToken.mockReturnValue('expired-token');
      jwtUtils.isTokenExpired.mockReturnValue(true);

      const user = await authService.getCurrentUser();
      expect(user).toBeNull();
      expect(jwtUtils.removeToken).toHaveBeenCalledWith('auth_token');
      expect(localStorage.removeItem).toHaveBeenCalledWith('userData');
    });

    it('should return null and clear data if localStorage data is invalid JSON', async () => {
      localStorage.setItem('userData', 'invalid-json');
      jwtUtils.getToken.mockReturnValue('valid-token');
      jwtUtils.isTokenExpired.mockReturnValue(false);

      const user = await authService.getCurrentUser();
      expect(user).toBeNull();
      expect(jwtUtils.removeToken).toHaveBeenCalledWith('auth_token');
      expect(localStorage.removeItem).toHaveBeenCalledWith('userData'); // Due to catch block
    });
  });

  describe('isAuthenticated', () => {
    it('should return true if token exists and is not expired', () => {
      jwtUtils.getToken.mockReturnValue('valid-token');
      jwtUtils.isTokenExpired.mockReturnValue(false);
      expect(authService.isAuthenticated()).toBe(true);
    });

    it('should return false if no token exists', () => {
      jwtUtils.getToken.mockReturnValue(null);
      expect(authService.isAuthenticated()).toBe(false);
      expect(jwtUtils.isTokenExpired).not.toHaveBeenCalled();
    });

    it('should return false if token is expired', () => {
      jwtUtils.getToken.mockReturnValue('expired-token');
      jwtUtils.isTokenExpired.mockReturnValue(true);
      expect(authService.isAuthenticated()).toBe(false);
    });
  });
}); 