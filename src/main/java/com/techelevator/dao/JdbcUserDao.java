package com.techelevator.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.techelevator.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Create a new user DAO with the supplied data source.
     *
     * @param dataSource an SQL data source
     */
    public JdbcUserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

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
    @Override
    public User createUser(String username, String hashedPassword, String saltString) {
        String sql = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, username, hashedPassword, saltString);

        User newUser = getUserByUsername(username);

        return newUser;
    }

    /**
     * Retrieve the user by the given username.
     *
     * @param username the username of the user to retrieve
     * @return the retrieved user
     */
    @Override
    public User getUserByUsername(String username) {
        User user = null;

        String sqlSearchForUser = "SELECT id, username FROM users WHERE UPPER(username) = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchForUser, username.toUpperCase());

        if (results.next()) {
            user = mapRowToUser(results);
        }
        return user;
    }

    /**
     * Retrieve the password and salt for a user with the given username.
     *
     * @param username the username of the user to retrieve
     * @return Map of password and salt
     */
    @Override
    public Map<String, String> getPasswordAndSaltByUsername(String username) {
        Map<String, String> passwordAndSalt = new HashMap<>();

        String sqlSearchForUser = "SELECT salt, password FROM users WHERE UPPER(username) = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSearchForUser, username.toUpperCase());
        
        if (results.next()) {
            passwordAndSalt.put("salt", results.getString("salt"));
            passwordAndSalt.put("password", results.getString("password"));
        }
        return passwordAndSalt;
    }

    /**
     * Get all the users from the database.
     * @return a List of user objects
     */
    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sqlSelectAllUsers = "SELECT id, username FROM users;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sqlSelectAllUsers);

        while (results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }

        return users;
    }

    private User mapRowToUser(SqlRowSet results) {
        User user = new User();
        user.setUserId(results.getInt("id"));
        user.setUsername(results.getString("username"));
        return user;
    }
}
