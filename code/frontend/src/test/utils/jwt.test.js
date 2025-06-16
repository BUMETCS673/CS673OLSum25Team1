import { describe, expect, vi, beforeEach, afterEach, test } from "vitest";
import { jwtUtils } from "../../utils/jwt";

describe("JWT Utils Tests", () => {
    const mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE3MTA0ODMwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    const mockExpiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwMjJ9.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    const mockInvalidToken = "invalid.token.format";

    beforeEach(() => {
        // Clear localStorage before each test
        localStorage.clear();
        // Mock localStorage
        vi.spyOn(Storage.prototype, "setItem");
        vi.spyOn(Storage.prototype, "getItem");
        vi.spyOn(Storage.prototype, "removeItem");
    });

    afterEach(() => {
        vi.clearAllMocks();
    });

    describe("Token Storage Operations", () => {
        test("setToken should store token in localStorage", () => {
            jwtUtils.setToken("test_key", mockToken);
            expect(localStorage.setItem).toHaveBeenCalledWith("test_key", mockToken);
        });

        test("getToken should retrieve token from localStorage", () => {
            localStorage.setItem("test_key", mockToken);
            const token = jwtUtils.getToken("test_key");
            expect(localStorage.getItem).toHaveBeenCalledWith("test_key");
            expect(token).toBe(mockToken);
        });

        test("removeToken should remove token from localStorage", () => {
            localStorage.setItem("test_key", mockToken);
            jwtUtils.removeToken("test_key");
            expect(localStorage.removeItem).toHaveBeenCalledWith("test_key");
        });
    });

    describe("Token Parsing", () => {
        test("parseJwt should correctly parse valid JWT token", () => {
            const decoded = jwtUtils.parseJwt(mockToken);
            expect(decoded).toEqual({
                sub: "1234567890",
                name: "John Doe",
                iat: 1516239022,
                exp: 1710483022
            });
        });

        test("parseJwt should return null for invalid token", () => {
            const decoded = jwtUtils.parseJwt(mockInvalidToken);
            expect(decoded).toBeNull();
        });

        test("parseJwt should handle malformed base64", () => {
            const malformedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.base64.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
            const decoded = jwtUtils.parseJwt(malformedToken);
            expect(decoded).toBeNull();
        });
    });

    describe("Token Expiration", () => {
        test("isTokenExpired should return true for expired token", () => {
            expect(jwtUtils.isTokenExpired(mockExpiredToken)).toBe(true);
        });

        test("isTokenExpired should return true for null token", () => {
            expect(jwtUtils.isTokenExpired(null)).toBe(true);
        });

        test("isTokenExpired should return true for invalid token", () => {
            expect(jwtUtils.isTokenExpired(mockInvalidToken)).toBe(true);
        });
    });
}); 