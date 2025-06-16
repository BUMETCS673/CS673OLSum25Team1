/* eslint-disable no-undef */
describe("Home Page E2E Test", () => {
  const testActivity = {
    name: "Test Activity",
    description: "This is a test activity created by Cypress",
    location: "Test Location",
    startDateTime: "2025-09-16T10:00",
    endDateTime: "2025-09-16T12:00",
  };
  it("should allow a user to register successfully", () => {
    // Intercept the registration request to capture the token
    cy.intercept("POST", "/v1/register", (req) => {
      req.continue((res) => {
        const token = res.body?.data?.token;
        Cypress.env("token", token);
      });
    }).as("registrationToken");

    cy.visit("/register");

    cy.findByLabelText(/Username/i).type("testuser2");
    cy.findByLabelText(/Email/i).type("testuser2@bu.edu");
    cy.get('input[id="password"]').should("be.visible").type("Password123!");
    cy.get('input[id="confirmpassword"]').should("be.visible").type("Password123!");

    cy.findByRole("button", { name: /Register/i }).click();

    // Wait for the backend to respond
    cy.wait("@registrationToken").then(() => {
      const token = Cypress.env("token");
      expect(token).to.exist;

      // Wait for us to be redirected to the confirmation page
      cy.url().should("include", "/register/confirmation");

      cy.findByLabelText(/Registration Code/i).type(token);
      cy.findByRole("button", { name: /Confirm/i }).click();

      cy.contains("Confirmation successful").should("be.visible");

      // Check that user is redirected or sees success
      cy.url().should("include", "/login");
    });
  });

  it("should allow a user to log in to home page and join/leave activities successfully", () => {
    cy.visit("/login");
    cy.findByLabelText(/username/i).type("testuser2");
    cy.findByLabelText(/password/i).type("Password123!");
    cy.findByRole("button", { name: /Login/i }).click();

    cy.url().should("include", "/home");
    cy.contains(/ActivityHub/i).should("be.visible");
    cy.contains("Rock Climbing")
      .closest("div")
      .within(() => {
        cy.contains("button", "Join Activity").click();
      });
    cy.contains(/Participated Activities/i).click();
    cy.contains(/Rock Climbing/i).should("be.visible");
    cy.contains("Rock Climbing")
      .closest("div")
      .within(() => {
        cy.contains("button", "Leave Activity").click();
      });
    cy.contains(/Rock Climbing/i).should("not.exist");
  });

  it("should create a new activity and display it on the home page", () => {
    cy.visit("/login");
    cy.findByLabelText(/username/i).type("testuser2");
    cy.findByLabelText(/password/i).type("Password123!");
    cy.findByRole("button", { name: /Login/i }).click();
    cy.url().should("include", "/home");

    cy.contains("button", "+ Create Activity").click();

    cy.url().should("include", "/create-activity");

    cy.get('input[id="activity-name"]').type(testActivity.name);
    cy.get('textarea[id="activity-description"]').type(testActivity.description);
    cy.get('input[id="activity-location"]').type(testActivity.location);
    cy.get('input[id="activity-start-date-time"]').type(testActivity.startDateTime);
    cy.get('input[id="activity-end-date-time"]').type(testActivity.endDateTime);

    cy.findByRole("button", { name: /Create Activity/i }).click();

    cy.url().should("include", "/home");

    cy.contains(testActivity.name).should("be.visible");
    cy.contains(testActivity.description).should("be.visible");
    cy.contains(testActivity.location).should("be.visible");

    const startDate = new Date(testActivity.startDateTime);
    const endDate = new Date(testActivity.endDateTime);
    const formatDateTime = (date) =>
      `${date.toLocaleDateString()} ${date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}`;

    cy.contains(formatDateTime(startDate)).should("be.visible");
    cy.contains(formatDateTime(endDate)).should("be.visible");
  });

});
