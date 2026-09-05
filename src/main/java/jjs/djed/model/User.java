package jjs.djed.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class User {
    private final UUID userId;
    private final String username;
    private final OffsetDateTime dateCreated;
    private String displayName;

    /**
     * Create a user from already known parameters. Only use when loading from database
     * @param userId the UUID of the user
     * @param username the username of the user
     * @param dateCreated the date the user was created
     */
    public User(UUID userId, String username, OffsetDateTime dateCreated, String displayName) {
        this.userId = userId;
        this.username = username;
        this.dateCreated = dateCreated;
        this.displayName = displayName;
    }

    /**
     * Create new user with a username
     * @param username the username to use
     */
    public User(String username) {
        this.username = username;
        // Users created with this constructor should not be cached. Only cache users after
        // they have been loaded from the database with their display id generated
        this.dateCreated = OffsetDateTime.now();
        this.userId = UUID.randomUUID();
        this.displayName = username;

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

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
