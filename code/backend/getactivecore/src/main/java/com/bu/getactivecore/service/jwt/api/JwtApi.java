package com.bu.getactivecore.service.jwt.api;

import com.bu.getactivecore.shared.exception.ApiException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface for JWT (JSON Web Token) operations.
 */
public interface JwtApi {
    /**
     * Generates a JWT token for the given username.
     *
     * @param username the username for which to generate the token
     * @return the generated JWT token
     */
    String generateToken(String username);

    /**
     * Retrieves the username (subject) embedded in the token.
     *
     * @param token to get username from
     * @return the username extracted from the token or null if the token is invalid
     */
    String getUsername(String token);

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean isValid(String token);

    /**
     * Validates the given JWT token against the provided user details.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid, false otherwise
     */
    boolean validateToken(String token, UserDetails userDetails) throws ApiException;
}
