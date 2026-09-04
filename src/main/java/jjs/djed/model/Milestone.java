package jjs.djed.model;

import java.util.UUID;

public class Milestone {
    private final UUID milestoneUuid;
    private String name;
    private String description;
    private long requiredSeconds;
    private final UUID skillId;

    /**
     * Method used when creating a milestone object from database
     * @param skillUuid
     * @param milestoneUuid
     * @param name
     * @param description
     * @param requiredSeconds
     */
    public Milestone(UUID skillUuid, UUID milestoneUuid, String description, String name, long requiredSeconds) {
        this.skillId = skillUuid;
        this.milestoneUuid = milestoneUuid;
        this.description = description;
        this.name = name;
        this.requiredSeconds = requiredSeconds;
    }

    public Milestone(UUID skillId, String name, String description, long requiredSeconds) {
        this.skillId = skillId;
        this.milestoneUuid = UUID.randomUUID();
        this.description = description;
        this.name = name;
        this.requiredSeconds = requiredSeconds;
    }

    public UUID getMilestoneUuid() {
        return milestoneUuid;
    }

    public UUID getSkillId() {
        return skillId;
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
