package com.techelevator;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.sql.DataSource;

import com.techelevator.dao.JdbcUserDao;
import com.techelevator.model.User;
import com.techelevator.dao.UserDao;
import com.techelevator.security.PasswordHasher;

import org.apache.commons.dbcp2.BasicDataSource;
import org.bouncycastle.util.encoders.Base64;

public class UserManagerCli {

    private final UserDao userDao;
    private final Scanner userInput;
    private final PasswordHasher passwordHasher;
    private User loggedInUser;

    public static void main(String[] args) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/user-manager");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");

        UserManagerCli application = new UserManagerCli(dataSource);
        application.run();
    }

    /**
     * Set up the DAOs and scanner for the application
     *
     * @param datasource the connection information to the SQL database
     */
    public UserManagerCli(DataSource datasource) {
        passwordHasher = new PasswordHasher();
        userDao = new JdbcUserDao(datasource);
        userInput = new Scanner(System.in);
    }

    /**
     * The main run loop.
     */
    public void run() {
        printGreeting();

        while (true) {
            printMenu();
            String option = askPrompt();

            if ("a".equalsIgnoreCase(option)) {
                addNewUser();
            } else if ("s".equalsIgnoreCase(option)) {
                showUsers();
            } else if ("l".equalsIgnoreCase(option)) {
                loginUser();
            } else if ("q".equalsIgnoreCase(option)) {
                System.out.println("Thanks for using the User Manager!");
                break;
            } else {
                System.out.println(option + " is not a valid option. Please select again.");
            }
        }
    }

    /**
     * Take a username and password from the user and check it against
     * the DAO via the isUsernameAndPasswordValid() method.
     * If the method returns false it means the username/password aren't valid.
     * You don't know what's specifically wrong about the login, just that the combined
     * username & password aren't valid. You don't want to give an attacker any information about
     * what they got right or what they got wrong when trying this. Information
     * like that is gold to an attacker because then they know what they're
     * getting right and what they're getting wrong.
     */
    private void loginUser() {
        System.out.println("Log into the system");
        System.out.print("Username: ");
        System.out.flush();
        String username = userInput.nextLine();
        System.out.print("Password: ");
        System.out.flush();
        String password = userInput.nextLine();

        if (isUsernameAndPasswordValid(username, password)) {
            loggedInUser = new User();
            loggedInUser.setUsername(username);
            System.out.println("Welcome " + username + "!");
            System.out.println();
        } else {
            System.out.println("That login is not valid, please try again.");
            System.out.println();
        }
    }

    /**
     * Check the username and password are valid.
     *
     * @param username the supplied username to validate
     * @param password the supplied password to validate
     * @return true is username and password are valid and correct
     */
    private boolean isUsernameAndPasswordValid(String username, String password) {
        Map<String, String> passwordAndSalt = userDao.getPasswordAndSaltByUsername(username);
        String storedSalt = passwordAndSalt.get("salt");
        String storedPassword = passwordAndSalt.get("password");
        String hashedPassword = passwordHasher.computeHash(password, Base64.decode(storedSalt));
        return storedPassword.equals(hashedPassword);
    }

    /**
     * Add a new user to the system. Anyone can register a new account like
     * this. It calls createUser() in the DAO to save it to the data store.
     */
    private void addNewUser() {
        System.out.println("Enter the following information for a new user: ");
        System.out.print("Username: ");
        System.out.flush();
        String username = userInput.nextLine();
        System.out.print("Password: ");
        System.out.flush();
        String password = userInput.nextLine();

        // generate hashed password and salt
        byte[] salt = passwordHasher.generateRandomSalt();
        String hashedPassword = passwordHasher.computeHash(password, salt);
        String saltString = new String(Base64.encode(salt));

        User user = userDao.createUser(username, hashedPassword, saltString);
        System.out.println("User " + user.getUsername() + " added with ID " + user.getUserId() + "!");
        System.out.println();
    }

    /**
     * Show all the users that are in the data store. You can't show passwords
     * because you don't have them! Passwords in the data store are hashed and
     * you can see that by opening up a SQL client like pgAdmin or DbVisualizer
     * and looking at what's stored in the `users` table.
     *
     * Only allow access to this to logged-in users. If a user isn't logged
     * in, give them a message and leave. Having an `if` statement like this
     * at the top of the method is a common way of handling authorization checks.
     */
    private void showUsers() {
        if (loggedInUser == null) {
            System.out.println("Sorry. Only logged in users can see other users.");
            System.out.println("Press enter to continue...");
            System.out.flush();
            userInput.nextLine();
            return;
        }

        List<User> users = userDao.getUsers();
        System.out.println("Users currently in the system are: ");
        for (User user : users) {
            System.out.println(user.getUserId() + ". " + user.getUsername());
        }
        System.out.println();
        System.out.println("Press enter to continue...");
        System.out.flush();
        userInput.nextLine();
        System.out.println();
    }

    private void printMenu() {
        System.out.println("(A)dd a new User");
        System.out.println("(S)how all users");
        System.out.println("(L)og in");
        System.out.println("(Q)uit");
        System.out.println();
    }

    private String askPrompt() {
        String name;
        if (loggedInUser == null) {
            name = "Unauthenticated User";
        } else {
            name = loggedInUser.getUsername();
        }

        System.out.println("Welcome " + name + "!");
        System.out.print("What would you like to do today? ");
        System.out.flush();
        String selection;
        try {
            selection = userInput.nextLine();
        } catch (Exception ex) {
            selection = "*";
        }
        return selection;
    }

    private void printGreeting() {
        System.out.println("Welcome to the User Manager Application!");
        System.out.println();
    }
}
