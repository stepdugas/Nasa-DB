package com.techelevator.model;

/**
 * User
 */
public class User {

    private int userId;
    private String username;

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId the id to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
