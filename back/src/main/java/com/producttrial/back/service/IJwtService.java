package com.producttrial.back.service;

public interface IJwtService {
    /**
     * Generates a JWT (JSON Web Token) for the given email address.
     *
     * @param userId the user id for which the token is to be generated
     * @return a JWT string representing the token
     */
    String generateToken(Long userId);

    /**
     * Extracts the user ID from the given JWT token.
     *
     * @param token the JWT token from which the user ID is to be extracted
     * @return the user ID extracted from the token as a string
     */
    String extractUserId(String token);

    /**
     * Validates the provided token to check if it is correctly signed and not expired.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean isValidToken(String token);

}
