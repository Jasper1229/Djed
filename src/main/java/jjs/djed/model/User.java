package jjs.djed.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
    private final UUID userId;
    private String username;
    private final OffsetDateTime dateCreated;
    private final long displayId;

    /**
     * Create a user from already known parameters. Only use when loading from database
     * @param userId the UUID of the user
     * @param username the username of the user
     * @param dateCreated the date the user was created
     */
    public User(UUID userId, String username, OffsetDateTime dateCreated, long displayId) {
        this.userId = userId;
        this.username = username;
        this.dateCreated = dateCreated;
        this.displayId = displayId;
    }

    /**
     * Create new user with a username
     * @param username the username to use
     */
    public User(String username) {
        this.username = username;
        this.displayId = -1;
        // Users created with this constructor should not be cached. Only cache users after
        // they have been loaded from the database with their display id generated
        this.dateCreated = OffsetDateTime.now();
        this.userId = UUID.randomUUID();

    }


    public UUID getUserId() {
        return userId;
    }
    public String getUsername() {
        return username;
    }
    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
