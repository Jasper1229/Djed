package jjs.djed.model;

import jjs.djed.util.Patterns;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


public class Skill {

    private static final Duration DEFAULT_SKILL_TIME = Duration.ZERO;

    private final UUID skillId;
    private final UUID templateId;
    private final UUID userId;
    private final Instant dateCreated;

    private final Set<Skill> children = new HashSet<>();

    private UUID parentSkillId;


    private double weight; // Weight used when ordering skills. Example: Basic 1, Intermediate 2; Advanced 3;
    private String description;
    private Duration skillTime;
    private String displayName;

    /**
     * Method used to create a new skill instance. Only use when creating brand new.
     * Use
     * @param templateId the ID of the skill template
     * @param userId The ID of the user whom the skill belongs to
     * @param parentSkillId the ID of the parent skill (Null if no parent)
     * @param displayName The display name of the skill
     * @param description The description of the skill
     */
    public Skill(
            UUID templateId,
            UUID userId,
            UUID parentSkillId,
            String displayName,
            String description
    ) {
        this.skillId = UUID.randomUUID();
        this.templateId = Objects.requireNonNull(templateId, "templateId cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");

        this.parentSkillId = parentSkillId;

        this.displayName = Objects.requireNonNull(displayName, "displayName cannot be null");
        this.description = description;

        this.skillTime = DEFAULT_SKILL_TIME;

        this.dateCreated = Instant.now();
        this.weight = 0;
    }

    /**
     * Constructor to use when loading pre-made skill instance
     * @param skillId  UUID of the skill
     * @param templateId  UUID of the skill template
     * @param userId  UUID of the user whom the skill belongs to
     * @param parentSkillId  UUID of the parent skill (Null if there is no parent)
     * @param displayName  Display name of the skill
     * @param description  Description of the skill
     * @param skillTime Duration of skill
     * @param weight Weight of the skill (Used for sorting skills)
     * @param dateCreated Date created
     */
    public Skill(
                 UUID skillId,
                 UUID templateId,
                 UUID userId,
                 UUID parentSkillId,
                 String displayName,
                 String description,
                 Duration skillTime,
                 Double weight,
                 Instant dateCreated
    ) {
        this.displayName = displayName;
        this.skillTime = skillTime;
        this.description = description;
        this.weight = weight;
        this.parentSkillId = parentSkillId;
        this.dateCreated = dateCreated;
        this.userId = userId;
        this.templateId = templateId;
        this.skillId = skillId;
    }

    public UUID getSkillId() {
        return skillId;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getParentSkillId() {
        return parentSkillId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Duration getSkillTime() {
        return skillTime;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }


    public void setParentSkillId(UUID parentSkillId) {
        this.parentSkillId = parentSkillId;
    }

    public void addTime(Duration duration) {
        Objects.requireNonNull(duration, "duration cannot be null");
        this.skillTime = this.skillTime.plus(duration);
    }

    public boolean setDisplayName(String name) {
        if(Patterns.isValid(Patterns.SKILL_NAME_PATTERN, name)) {
            this.displayName = name;
            return true;
        }
        return false;
    }

    public boolean setDescription(String desc) {
        if(Patterns.isValid(Patterns.SKILL_DESCRIPTION_PATTERN, desc)) {
            this.description = desc;
            return true;
        }
        return false;
    }

    public Duration recalculateSkillDuration() {
        if (children.isEmpty()) return this.skillTime;

        Duration total = Duration.ZERO;
        for(Skill skill : children) {
            total = total.plus(skill.recalculateSkillDuration());
        }
        this.skillTime = total;
        return total;
    }

    private boolean upstreamDurationUpdate(Duration delta) {

    }




}