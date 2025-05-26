import { describe, it, expect } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Login from '../../pages/Login';
import authService from '../../services/authService';
import { AuthProvider } from '../../contexts/AuthContext';
import userEvent from '@testing-library/user-event';
const mockNavigate = vi.fn(); 
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal(); 
  return {
    ...actual, 
    useNavigate: () => mockNavigate, 
  };
});

// vi.mock('../../services/authService', () => ({
//   default: {
//     login: vi.fn(),
//   },
// }));

const mockLoginInUseAuth = vi.fn();

vi.mock('../../contexts/AuthContext', () => ({ // 假设你的 useAuth Hook 在这个路径
  useAuth: () => ({ // Mock useAuth 返回的对象
    login: mockLoginInUseAuth, // 将模拟函数赋给 login 属性
    // 如果 useAuth 还返回其他属性 (例如 user, logout)，也要在这里模拟，否则组件可能会报错
    user: null, // 例如，默认用户为 null
    logout: vi.fn(), // 模拟 logout 方法
  }),
  AuthProvider: ({ children }) => <div data-testid="MockAuthProvider">{children}</div>
}));

describe('LoginPage Unit Tests', () => {
  it('should allow a user to log in successfully', async () => {
      mockLoginInUseAuth.mockResolvedValueOnce({ success: true, userData: { userId: '1', username: 'testuser', userEmail: 'test@test.com' }, error: null });
      
      render(<AuthProvider><MemoryRouter><Login /></MemoryRouter></AuthProvider>);
      
      const user = userEvent.setup();

      await user.type(screen.getByLabelText(/username/i), 'testuser');
      await user.type(screen.getByLabelText(/password/i), 'correctpassword');
      //screen.debug();
      await user.click(screen.getByRole('button', { name: /Login/i }));

      //expect(authService.login).toHaveBeenCalledWith('testuser', 'correctpassword');
      expect(mockLoginInUseAuth).toHaveBeenCalledWith('testuser', 'correctpassword', expect.any(Boolean));
      
      //await waitFor(() => expect(screen.getByText(/Welcome test user/i)).toBeInTheDocument());
      await waitFor(() => {
        expect(screen.getByText((content, element) => {
          // content 是元素的textContent
          // element 是当前的HTML元素
          // 你可以检查 content 是否包含你想要的所有部分
          return content.includes('Welcome') && content.includes('test user');
        })).toBeInTheDocument();
      });
      
  });
  it('should display an error message on failed login', async () => {
    authService.login.mockRejectedValueOnce({ success: false, error: 'User or password is invalid' });
    
    render(<AuthProvider><MemoryRouter><Login /></MemoryRouter></AuthProvider>);
    const user = userEvent.setup();

    await user.type(screen.getByLabelText(/username/i), 'testuser');
    await user.type(screen.getByLabelText(/password/i), 'wrongpassword');
    //screen.debug();
    await user.click(screen.getByRole('button', { name: /Login/i }));

    expect(authService.login).toHaveBeenCalledWith('testuser', 'wrongpassword');

    await waitFor(() => {
        expect(screen.getByText(/Login failed/i)).toBeInTheDocument();
    });
  });

});

describe('LoginPage Navigation Test', () => {
    beforeEach(() => {
          mockNavigate.mockClear();
          //authService.login.mockClear();
          mockLoginInUseAuth.mockClear();
    });

    it('should navigate to /home on successful login', async () => {

        authService.login.mockResolvedValueOnce({ success: true, userData: { userId: '1', username: 'testuser', userEmail: 'test@test.com' }, error: null });
        
        render(<AuthProvider><MemoryRouter><Login /></MemoryRouter></AuthProvider>);
        const user = userEvent.setup();

        await user.type(screen.getByLabelText(/username/i), 'testuser'); 
        await user.type(screen.getByLabelText(/password/i), 'correctpassword'); 
        //screen.debug();
        await user.click(screen.getByRole('button', { name: /Login/i }));

        expect(authService.login).toHaveBeenCalledWith('testuser', 'correctpassword');

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledTimes(1);
            expect(mockNavigate).toHaveBeenCalledWith('/home'); 
        });
    });
});