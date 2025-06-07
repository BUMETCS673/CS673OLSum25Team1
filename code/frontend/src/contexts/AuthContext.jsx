import { createContext, useContext, useState, useEffect } from "react";
import { authService } from "../services/authService";

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
    const { success, userData, error } = await authService.login(
      username,
      password
    );
    console.log("userData", userData);
    if (success) {
      setLoading(false);
      setUser(userData);
    }
    return { success, error };
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
  };

  const register = async (username, email, password) => {
    const { success, error } = await authService.register(
      username,
      email,
      password
    );
    return { success, error };
  };

  /**
   * Confirms the user registration using the given token.
   * @param {*} token JWT token to confirm the registration
   * @returns success: boolean, error: string
   */
  const registerConfirm = async (token) => {
    const { success, error } = await authService.registerConfirm(token);
    return { success, error };
  };

  return (
    <AuthContext.Provider
      value={{ user, login, logout, register, registerConfirm, loading }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
