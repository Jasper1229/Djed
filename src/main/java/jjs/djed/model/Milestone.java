package jjs.djed.model;

import java.util.UUID;

public class Milestone {
    private final UUID milestoneUuid;
    private String name;
    private String description;
    private long requiredSeconds;

    /**
     * Method used when creating a milestone object from database
     * @param milestoneUuid
     * @param name
     * @param description
     * @param requiredSeconds
     */
    public Milestone(UUID milestoneUuid, String description, String name, long requiredSeconds) {
        this.milestoneUuid = milestoneUuid;
        this.description = description;
        this.name = name;
        this.requiredSeconds = requiredSeconds;
    }

    public Milestone(String description, String name, long requiredSeconds) {
        this.milestoneUuid = UUID.randomUUID();
        this.description = description;
        this.name = name;
        this.requiredSeconds = requiredSeconds;
    }

    public UUID getMilestoneUuid() {
        return milestoneUuid;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRequiredSeconds(long seconds) {
        this.requiredSeconds = seconds;
    }



}
