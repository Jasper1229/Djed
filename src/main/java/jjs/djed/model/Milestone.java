package jjs.djed.model;

import java.util.UUID;

public class Milestone {
    private final UUID milestoneUuid;
    private final String name;
    private final String description;
    private final long requiredSeconds;

    /**
     * Method used when creating a milestone object from database
     * @param milestoneUuid
     * @param name
     * @param description
     * @param requiredSeconds
     */
    public Milestone(UUID milestoneUuid, String name, String description, long requiredSeconds) {
        this.milestoneUuid = milestoneUuid;
        this.name = name;
        this.description = description;
        this.requiredSeconds = requiredSeconds;
    }

    public Milestone(String name, String description, long requiredSeconds) {
        this.name = name;
        this.description = description;
        this.requiredSeconds = requiredSeconds;
        this.milestoneUuid = UUID.randomUUID();
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public long getRequiredSeconds() {
        return requiredSeconds;
    }

    public boolean isReached(long totalSeconds) {
        return totalSeconds >= requiredSeconds;
    }



}
