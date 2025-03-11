package com.techelevator.dao;

import com.techelevator.model.User;

import java.util.List;
import java.util.Map;

public interface UserDao {

    /**
     * Save a new user to the database. The original password isn't passed in,
     * it's salted and hashed before being passed. The original password is never
     * stored in the system.
     *
     * @param username the username to give the new user
     * @param hashedPassword the user's hashed password
     * @param saltString the salt of the user's hashed password
     * @return the new user
     */
    User createUser(String username, String hashedPassword, String saltString);

    /**
     * Retrieve the user by the given username.
     *
     * @param username the username of the user to retrieve
     * @return the retrieved user
     */
    User getUserByUsername(String username);

    /**
     * Retrieve the password and salt for a user with the given username.
     *
     * @param username the username of the user to retrieve
     * @return Map of password and salt
     */
    Map<String, String> getPasswordAndSaltByUsername(String username);

    /**
     * Get all the users from the database.
     * @return a List of user objects
     */
    List<User> getUsers();

}
