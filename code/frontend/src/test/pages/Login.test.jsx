import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Login from '../../pages/Login';
import userEvent from '@testing-library/user-event';

// Mock navigation
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

// Mock login function
const mockLoginInUseAuth = vi.fn();
vi.mock('../../contexts/AuthContext', () => ({
  useAuth: () => ({
    login: mockLoginInUseAuth,
    user: { userId: '1', username: 'testuser', userEmail: 'test@test.com' },
    logout: vi.fn(),
  }),
  AuthProvider: ({ children }) => <>{children}</>,
}));

describe('LoginPage Unit Test', () => {
  beforeEach(() => {
    mockNavigate.mockClear();
    mockLoginInUseAuth.mockClear();
  });

  it('allows user to input BU email and password', async () => {
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    const user = userEvent.setup();
    const emailInput = screen.getByLabelText(/username/i);
    const passwordInput = screen.getByLabelText(/password/i);

    await user.type(emailInput, 'student@bu.edu');
    await user.type(passwordInput, 'securePassword123');

    expect(emailInput).toHaveValue('student@bu.edu');
    expect(passwordInput).toHaveValue('securePassword123');
  });

  it('disables the login button during submission', async () => {
    // mock login as delayed to simulate loading state
    mockLoginInUseAuth.mockImplementation(
      () =>
        new Promise((resolve) =>
          setTimeout(() => resolve({ success: true }), 500)
        )
    );

    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>
    );

    const user = userEvent.setup();
    await user.type(screen.getByLabelText(/username/i), 'student@bu.edu');
    await user.type(screen.getByLabelText(/password/i), 'securePassword123');

    const loginButton = screen.getByRole('button', { name: /login/i });

    // Ensure button is initially enabled
    expect(loginButton).not.toBeDisabled();

    // Click to trigger submission
    await user.click(loginButton);

    // Button should be disabled immediately after clicking
    expect(loginButton).toBeDisabled();

    // Wait for login process to complete
    await waitFor(() => {
      expect(mockLoginInUseAuth).toHaveBeenCalled();
    });
  });
});
