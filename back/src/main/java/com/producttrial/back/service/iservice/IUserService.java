package com.producttrial.back.service.iservice;

import com.producttrial.back.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    /**
     * Retrieves a list of all available users.
     *
     * @return a list containing all users. If no users are found, an empty list will be returned.
     */
    List<User> findAll();

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the unique identifier of the user to retrieve
     * @return an Optional containing the user if found, or an empty Optional if no user exists with the given id
     */
    Optional<User> findById(Long id);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to retrieve
     * @return an Optional containing the user if found, or an empty Optional if no user exists with the given email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Persists the given user in the database. If the user is already present, it will be updated.
     *
     * @param user the user object to be saved
     * @return the saved user object
     */
    User save(User user);

    /**
     * Updates an existing user entity with the provided details.
     * The user to update is identified by the specified unique ID.
     *
     * @param id the unique identifier of the user to be updated
     * @param user the user object containing the updated details
     * @return the updated user entity
     */
    User update(Long id, User user);

    /**
     * Deletes a user identified by the specified ID.
     *
     * @param id the unique identifier of the user to be deleted
     */
    void delete(Long id);

    /**
     * Deletes all user records from the database.
     * This operation removes all existing users, leaving the user table empty.
     * Typically used for cleanup or resetting user data.
     */
    void deleteAll();

}
