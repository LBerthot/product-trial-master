package com.producttrial.back.service.iservice;

public interface IAuthorizationService {

    /**
     * Retrieves the email address of the currently authenticated user.
     *
     * @return a string representing the email address of the current user,
     *         or null if no user is authenticated.
     */
    String currentUserEmail();

    /**
     * Ensures that the current user has administrative privileges.
     * This method performs a validation to check if the user's role meets
     * the required admin permissions.
     * If the user does not have admin rights, an appropriate exception is thrown
     * to indicate the lack of authorization.
     * This method is typically used to secure access to features or actions
     * that should only be available to administrative users.
     */
    void ensureAdmin();

    /**
     * Retrieves the unique identifier of the currently authenticated user.
     *
     * @return a Long representing the user ID of the current user,
     *         or null if no user is authenticated.
     */
    Long getCurrentUserId();
}
