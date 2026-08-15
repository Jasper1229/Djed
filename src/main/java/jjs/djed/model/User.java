package jjs.djed.model;

import java.time.Instant;
import java.util.UUID;

public class User {
    private final UUID userId;
    private String username;
    private final Instant dateCreated;

    /**
     * Create a user from already known parameters. Only use when loading from database
     * @param userId the UUID of the user
     * @param username the username of the user
     * @param dateCreated the date the user was created
     */
    public User(UUID userId, String username, Instant dateCreated) {
        this.userId = userId;
        this.username = username;
        this.dateCreated = dateCreated;
    }

    /**
     * Create new user with a username
     * @param username the username to use
     */
    public User(String username) {
        this.username = username;
        this.dateCreated = Instant.now();
        this.userId = UUID.randomUUID();
        saveToDatabase(this);
    }

    private void saveToDatabase(User user) {

    }

    public UUID getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
