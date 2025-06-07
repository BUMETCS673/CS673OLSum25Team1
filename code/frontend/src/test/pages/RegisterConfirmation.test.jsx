import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import RegisterConfirmation from "../../pages/RegisterConfirmation";
import { describe, it, expect, vi } from "vitest";
import { BrowserRouter } from "react-router-dom";
import { AuthContext } from "../../contexts/AuthContext";
import { MemoryRouter } from "react-router-dom";

// Mock the AuthContext with a successful resendConfirmation
const mockResendConfirmation = vi.fn().mockResolvedValue({ success: true });

vi.mock("../../contexts/AuthContext", () => ({
  useAuth: () => ({
    registerConfirmation: vi.fn(),
    resendConfirmation: mockResendConfirmation,
  }),
}));

describe("RegisterConfirmation Page", () => {
  it("Confirmation token can be entered", async () => {
    render(
      <MemoryRouter>
        <RegisterConfirmation />
      </MemoryRouter>
    );

    const tokenElement = screen.getByLabelText(/Registration Code/i);
    expect(tokenElement).toBeInTheDocument();

    const confirmBttn = await screen.findByRole("button", { name: "Confirm" });
    expect(confirmBttn).toBeInTheDocument();
    expect(confirmBttn).toBeDisabled();

    // Enter a token and verify the button is enabled
    fireEvent.change(tokenElement, { target: { value: "test-token" } });
    expect(tokenElement.value).toBe("test-token");
    expect(confirmBttn).toBeEnabled();
  });

  it("Error alert is shown when invalid token is entered", async () => {
    render(
      <MemoryRouter>
        <RegisterConfirmation />
      </MemoryRouter>
    );

    const tokenElement = screen.getByLabelText(/Registration Code/i);
    expect(tokenElement).toBeInTheDocument();
    const confirmBttn = await screen.findByRole("button", { name: "Confirm" });
    expect(confirmBttn).toBeInTheDocument();
    expect(confirmBttn).toBeDisabled();

    // Enter an invalid token and verify the button is enabled
    fireEvent.change(tokenElement, { target: { value: "invalid-token" } });
    expect(tokenElement.value).toBe("invalid-token");
    expect(confirmBttn).toBeEnabled();
    fireEvent.click(confirmBttn);

    // Verify the error message appears
    const errorMessage = await screen.findByTestId("error-alert");
    expect(errorMessage).toBeInTheDocument();
    expect(confirmBttn).toBeDisabled();
  });

  it("Resend Dialog opens with expected input fields", async () => {
    render(
      <MemoryRouter>
        <RegisterConfirmation />
      </MemoryRouter>
    );

    // Click the resend button and verify the dialog opens
    const resendButton = await screen.findByRole("button", { name: "Resend" });
    expect(resendButton).toBeInTheDocument();

    fireEvent.click(resendButton);
    const dialogTitle = await screen.findByText("Resend Confirmation Email");
    expect(dialogTitle).toBeInTheDocument();

    const usernameInput = screen.getByTestId("usernameId");
    expect(usernameInput).toBeInTheDocument();
    const emailInput = screen.getByTestId("emailId");
    expect(emailInput).toBeInTheDocument();
  });

  it("Resend Dialog Nonempty username field is required", async () => {
    render(
      <MemoryRouter>
        <RegisterConfirmation />
      </MemoryRouter>
    );

    // Open the resend dialog
    const resendButton = await screen.findByRole("button", { name: "Resend" });
    fireEvent.click(resendButton);
    await screen.findByText("Resend Confirmation Email");

    // Click the resend button without entering a username
    const resendConfirmButton = screen.getByRole("button", { name: "Resend" });
    fireEvent.click(resendConfirmButton);

    // Verify the error message for username
    const usernameError = screen.getByText("Username is required");
    expect(usernameError).toBeInTheDocument();

    // Verify the error message for username
    const usernameInput = screen.getByTestId("usernameId");
    fireEvent.change(usernameInput, { target: { value: "           " } });

    // Verify the error message for username
    const usernameErrorAfterChange = screen.getByText("Username is required");
    expect(usernameErrorAfterChange).toBeInTheDocument();
  });

  it("Resend Dialog valid email field is required", async () => {
    render(
      <MemoryRouter>
        <RegisterConfirmation />
      </MemoryRouter>
    );

    // Open the resend dialog
    const resendButton = await screen.findByRole("button", { name: "Resend" });
    fireEvent.click(resendButton);
    await screen.findByText("Resend Confirmation Email");

    // Click the resend button without entering an email
    const resendConfirmButton = screen.getByRole("button", { name: "Resend" });
    fireEvent.click(resendConfirmButton);

    // Verify the error message when email is not entered
    let emailError = screen.getByText("Email is required");
    expect(emailError).toBeInTheDocument();

    // Verify the error message for email domain
    const emailInput = screen.getByTestId("emailId");
    fireEvent.change(emailInput, { target: { value: "testuser@bu.not.edu" } });
    emailError = screen.getByText("Must be '@bu.edu' email");
    expect(emailError).toBeInTheDocument();

    // Verify the error message for invalid email format
    fireEvent.change(emailInput, { target: { value: "invalid-email" } });
    emailError = screen.getByText("Invalid email format");
    expect(emailError).toBeInTheDocument();
  });

  it("Resend Dialog closes when username and valid BU email is entered", async () => {
    render(
      <MemoryRouter>
        <RegisterConfirmation />
      </MemoryRouter>
    );

    // Open the resend dialog
    const resendButton = await screen.findByRole("button", { name: "Resend" });
    fireEvent.click(resendButton);
    await screen.findByText("Resend Confirmation Email");

    // Enter a username and email
    const usernameInput = screen.getByTestId("usernameId");
    fireEvent.change(usernameInput, { target: { value: "testuser" } });

    const emailInput = screen.getByTestId("emailId");
    fireEvent.change(emailInput, { target: { value: "testuser@bu.edu" } });

    // Click the resend button without entering an email
    const resendConfirmButton = screen.getByRole("button", { name: "Resend" });
    fireEvent.click(resendConfirmButton);

    // Verify the dialog closes using the 'resendDialogId' test ID
    await waitFor(() => {
      expect(screen.queryByTestId("resendDialogId")).not.toBeInTheDocument();
    });

    // Verify the mocked resendConfirmation was called
    expect(mockResendConfirmation).toHaveBeenCalledWith("testuser", "testuser@bu.edu");
  });
});
