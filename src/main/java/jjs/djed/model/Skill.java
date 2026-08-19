package jjs.djed.model;

import jjs.djed.util.Patterns;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;


public class Skill {

    private static final int DEFAULT_SKILL_TIME = 0;

    private final UUID skillId;
    //private final UUID templateId;
    private final UUID userId;
    private final UUID rootId;
    private final OffsetDateTime dateCreated;

    private UUID parentSkillId;


    private short weight; // Weight used when ordering skills. Example: Basic 1, Intermediate 2; Advanced 3;
    private String description;
    private long skillTime;
    private long skillTimeLocal;
    private String displayName;

    /**
     * Method used to create a new skill instance. Only use when creating brand new.
     * Use
     * //@param templateId the ID of the skill template
     * @param userId The ID of the user whom the skill belongs to
     * @param parentSkillId the ID of the parent skill (Null if no parent)
     * @param displayName The display name of the skill
     * @param description The description of the skill
     */
    public Skill(
            //UUID templateId,
            UUID userId,
            UUID skillId,
            UUID parentSkillId,
            UUID rootId,
            String displayName,
            String description
    ) {
        this.rootId = rootId;
        this.skillId = skillId;
        //this.templateId = Objects.requireNonNull(templateId, "templateId cannot be null");
        this.userId = Objects.requireNonNull(userId, "userId cannot be null");

        this.parentSkillId = parentSkillId;


        this.displayName = Objects.requireNonNull(displayName, "displayName cannot be null");
        this.description = description;

        this.skillTime = DEFAULT_SKILL_TIME;
        this.skillTimeLocal = DEFAULT_SKILL_TIME;

        this.dateCreated = OffsetDateTime.now();
        this.weight = 0;
    }

    /**
     * Constructor to use when loading pre-made skill instance
     * @param skillId  UUID of the skill
     * //@param templateId  UUID of the skill template
     * @param userId  UUID of the user whom the skill belongs to
     * @param parentSkillId  UUID of the parent skill (Null if there is no parent)
     * @param displayName  Display name of the skill
     * @param description  Description of the skill
     * @param skillTime Duration of skill
     * @param weight Weight of the skill (Used for sorting skills)
     * @param dateCreated Date created
     */
    public Skill(
            UUID userId,
            UUID skillId,
            //UUID templateId,
            UUID parentSkillId,
            UUID rootId,
            String displayName,
            String description,
            long skillTime,
            long skillTimeLocal,
            short weight,
            OffsetDateTime dateCreated
    ) {
        this.rootId = rootId;
        this.displayName = displayName;
        this.skillTime = skillTime;
        this.skillTimeLocal = skillTimeLocal;
        this.description = description;
        this.weight = weight;
        this.parentSkillId = parentSkillId;
        this.dateCreated = dateCreated;
        this.userId = userId;
        //this.templateId = templateId;
        this.skillId = skillId;
    }

    public UUID getSkillId() {
        return skillId;
    }

    //public UUID getTemplateId() {
    //    return templateId;
    //}

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

    public long getSkillTime() {
        return skillTime;
    }

    public long getSkillTimeLocal() {return skillTimeLocal;}

    public OffsetDateTime getDateCreated() {
        return dateCreated;
    }


    public void setParentSkillId(UUID parentSkillId) {
        this.parentSkillId = parentSkillId;
    }

    public short getWeight() {
        return weight;
    }

    public void addTime(int duration) {
        this.skillTimeLocal = this.skillTimeLocal + duration;
    }

    public void setWeight(short weight) {
        this.weight = weight;
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

    public UUID getRootId() {
        return rootId;
    }
}