// cypress/e2e/login.cy.jsx

describe('Login Page E2E Test', () => {

    it('should allow a user to register successfully', () => {
        cy.visit('/register');
        
        cy.findByLabelText(/Username/i).type('testuser');
        cy.findByLabelText(/Email/i).type('testuser@bu.edu');
        //cy.logTestingPlaygroundURL();
        //cy.pause();
        cy.get('input[type="password"]').type('Password123!');
        //cy.findByLabelText(/Password/i).type('Password123!');
        cy.findByLabelText(/Confirm Password/i).type('Password123!');
        
        cy.findByRole('button', { name: /Register/i }).click();
    
        cy.url().should('include', '/login');
      });
  
    it('should display an error message on failed login', () => {
      cy.visit('/login');
      cy.findByLabelText(/username/i).type('testuser');
      cy.findByLabelText(/password/i).type('wrongpassword');
      cy.findByRole('button', { name: /Login/i }).click();
  
      cy.contains(/Login failed/i).should('be.visible');
      cy.url().should('include', '/login');
    });

    it('should allow a user to log in successfully', () => {
      cy.visit('/login');
      cy.findByLabelText(/username/i).type('testuser');
      cy.findByLabelText(/password/i).type('Password123!');
      cy.findByRole('button', { name: /Login/i }).click();
  
      cy.url().should('include', '/home');
      cy.contains(/Welcome testuser/i).should('be.visible');
    });
  });