// cypress/e2e/login.cy.jsx

describe('Login Page E2E Test', () => {
    beforeEach(() => {
      // 在每个测试开始前访问登录页面
      cy.visit('/login'); // 访问baseUrl下的/login路径
    });
  
    it('should allow a user to log in successfully', () => {
      // 找到用户名输入框并输入
      cy.findByLabelText(/username/i).type('testuser');
      // 找到密码输入框并输入
      cy.findByLabelText(/password/i).type('Password123!');
      // 找到登录按钮并点击
      cy.findByRole('button', { name: /Login/i }).click();
  
      // 断言登录成功后的页面内容或URL
      cy.url().should('include', '/home'); // 假设成功后跳转到 /home
      cy.contains(/Welcome testuser/i).should('be.visible'); // 假设页面显示欢迎信息
    });
  
    // it('should display an error message on failed login', () => {
    //   cy.findByLabelText(/username/i).type('testuser');
    //   cy.findByLabelText(/password/i).type('wrongpassword');
    //   cy.findByRole('button', { name: /Login/i }).click();
  
    //   // 断言错误信息是否显示
    //   cy.contains(/Invalid username or password/i).should('be.visible');
    //   // 确保没有跳转
    //   cy.url().should('not.include', '/home');
    // });
  });