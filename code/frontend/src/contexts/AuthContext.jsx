import { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/auth';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      if (authService.isAuthenticated()) {
        try {
            const userData = await authService.getCurrentUser();
            setUser(userData);
        } catch (error) {
          authService.logout();
        }
      }
      setLoading(false);
    };
    initAuth();
  }, []);

  const login = async (username, password) => {
    const { success, user: userData, error } = await authService.login(username, password);
    if (success) {
      setUser(userData);
    }
    return { success, error };
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
  };

  const register = async (username, password) => {
    const { success, error } = await authService.register(username, password);
    return { success, error };
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, register, loading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);