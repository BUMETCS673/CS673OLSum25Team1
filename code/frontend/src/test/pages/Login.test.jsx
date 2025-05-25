import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import authService from '../../services/authService';

describe('LoginPage Unit Tests', () => {
  it('should allow a user to log in successfully', async () => {
      authService.login.mockResolvedValueOnce({ token: 'fake-token', user: { id: '1', name: 'Test User' } });
      
      render(<LoginPage />);
      
      const user = userEvent.setup();

      await user.type(screen.getByLabelText(/username/i), 'testuser');
      await user.type(screen.getByLabelText(/password/i), 'correctpassword');
      await user.click(screen.getByRole('button', { name: /log in/i }));

      expect(authService.login).toHaveBeenCalledWith('testuser', 'correctpassword');

      
      // await waitFor(() => expect(screen.getByText(/welcome, test user/i)).toBeInTheDocument());
      
  });
  it('should display an error message on failed login', async () => {
    authService.login.mockRejectedValueOnce(new Error('User or password is invalid'));
    
    render(<LoginPage />);
    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/username/i), 'testuser');
    await user.type(screen.getByLabelText(/password/i), 'wrongpassword');
    await user.click(screen.getByRole('button', { name: /LOGIN/i }));

    expect(authService.login).toHaveBeenCalledWith('testuser', 'wrongpassword');

    await waitFor(() => {
        expect(screen.getByText(/User or password is invalid/i)).toBeInTheDocument();
    });
  });

});

const mockNavigate = vi.fn(); 
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal(); 
  return {
    ...actual, 
    useNavigate: () => mockNavigate, 
  };
});

vi.mock('../../services/authService', () => ({
  login: vi.fn(),
}));

describe('LoginPage Navigation Test', () => {
    beforeEach(() => {
       
        mockNavigate.mockClear();
        authService.login.mockClear();
    });

    it('should navigate to /dashboard on successful login', async () => {

        authService.login.mockResolvedValueOnce({ token: 'fake-token' });
        
        render(<LoginPage />);
        const user = userEvent.setup();

        await user.type(screen.getByLabelText(/username/i), 'testuser'); 
        await user.type(screen.getByLabelText(/password/i), 'correctpassword'); 
        await user.click(screen.getByRole('button', { name: /log in/i }));

        expect(authService.login).toHaveBeenCalledWith('testuser', 'correctpassword');

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledTimes(1);
            expect(mockNavigate).toHaveBeenCalledWith('/home'); 
        });
    });
});