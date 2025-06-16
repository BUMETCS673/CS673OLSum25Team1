import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { describe, expect, vi, beforeEach, afterEach, test } from "vitest";
import AvatarUpload from "../../components/Avator";

const mockUpdateAvatar = vi.fn();
vi.mock("../../contexts/AuthContext", () => {
  return {
    useAuth: () => ({
      updateAvatar: mockUpdateAvatar,
    }),
    AuthProvider: ({ children }) => <div data-testid="MockAuthProvider">{children}</div>,
  };
});

// Mock image compression library
vi.mock("browser-image-compression", () => ({
  default: vi.fn().mockImplementation((file) => Promise.resolve(file)),
}));

// Mock FileReader
class MockFileReader {
  onload = null;
  readAsDataURL() {
    this.onload({ target: { result: "data:image/jpeg;base64,mockBase64Data" } });
  }
}

global.FileReader = MockFileReader;
import { AuthProvider } from "../../contexts/AuthContext";

describe("AvatarUpload Component Tests", () => {
  const mockUser = {
    username: "testuser",
    userEmail: "test@example.com",
  };

  beforeEach(() => {
    vi.clearAllMocks();
    mockUpdateAvatar.mockResolvedValue({ success: true, avatarResponse: { avatar: "newAvatarUrl" } });
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  test("renders avatar with username initial when no avatar exists", () => {
    render(
      <AuthProvider>
        <AvatarUpload user={mockUser} />
      </AuthProvider>
    );
    expect(screen.getByText("T")).toBeInTheDocument();
  });

  test("renders avatar image when avatar exists", () => {
    const userWithAvatar = { ...mockUser, avatar: "avatarUrl" };
    render(
      <AuthProvider>
        <AvatarUpload user={userWithAvatar} />
      </AuthProvider>
    );
    const avatarImage = screen.getByRole("img");
    expect(avatarImage).toHaveAttribute("src", "avatarUrl");
  });

  test("shows success message when avatar is updated successfully", async () => {
    render(
      <AuthProvider>
        <AvatarUpload user={mockUser} />
      </AuthProvider>
    );

    const file = new File(["test"], "test.jpg", { type: "image/jpeg" });
    const input = screen.getByTestId("avatar-input");

    fireEvent.change(input, { target: { files: [file] } });

    await waitFor(() => {
      expect(screen.getByText("success to update avatar")).toBeInTheDocument();
    });
  });

  test("shows error message when uploading unsupported file type", async () => {
    render(
      <AuthProvider>
        <AvatarUpload user={mockUser} />
      </AuthProvider>
    );

    const file = new File(["test"], "test.gif", { type: "image/gif" });
    const input = screen.getByTestId("avatar-input");

    fireEvent.change(input, { target: { files: [file] } });

    await waitFor(() => {
      expect(screen.getByText("only jpeg and png are supported")).toBeInTheDocument();
    });
  });

  test("shows error message when avatar update fails", async () => {
    mockUpdateAvatar.mockRejectedValue(new Error("Update failed"));
    
    render(
      <AuthProvider>
        <AvatarUpload user={mockUser} />
      </AuthProvider>
    );

    const file = new File(["test"], "test.jpg", { type: "image/jpeg" });
    const input = screen.getByTestId("avatar-input");

    fireEvent.change(input, { target: { files: [file] } });

    await waitFor(() => {
      expect(screen.getByText("Update failed")).toBeInTheDocument();
    });
  });

  test("compresses image before upload", async () => {
    const { default: imageCompression } = await import("browser-image-compression");
    
    render(
      <AuthProvider>
        <AvatarUpload user={mockUser} />
      </AuthProvider>
    );

    const file = new File(["test"], "test.jpg", { type: "image/jpeg" });
    const input = screen.getByTestId("avatar-input");

    fireEvent.change(input, { target: { files: [file] } });

    await waitFor(() => {
      expect(imageCompression).toHaveBeenCalledWith(file, {
        maxSizeMB: 3,
        maxWidthOrHeight: 1024,
        useWebWorker: true,
      });
    });
  });
}); 