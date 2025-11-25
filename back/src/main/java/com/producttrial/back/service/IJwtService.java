package com.producttrial.back.service;

public interface IJwtService {
    /**
     * Generates a JWT (JSON Web Token) for the given email address.
     *
     * @param email the email address for which the token is to be generated
     * @return a JWT string representing the token
     */
    String generateToken(String email);
    /**
     * Extracts the email address from a given JSON Web Token (JWT).
     *
     * @param token the JWT from which the email address is extracted
     * @return the email address contained within the token
     */
    String extractEmail(String token);
    /**
     * Validates the provided token to check if it is correctly signed and not expired.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    boolean isValidToken(String token);

}
