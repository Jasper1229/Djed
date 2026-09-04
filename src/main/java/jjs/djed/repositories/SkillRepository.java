package jjs.djed.repositories;

import jjs.djed.model.Skill;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jjs.djed.jooq.Tables.SKILLS;
import static org.jooq.impl.DSL.*;

@Repository
public class SkillRepository {

    private final DSLContext dsl;

    public SkillRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Skill insert(Skill skill) {
        return dsl.insertInto(SKILLS)
                .set(SKILLS.USER_ID, skill.getUserId())
                .set(SKILLS.SKILL_ID, skill.getSkillId())
                .set(SKILLS.PARENT_ID, skill.getParentSkillId())
                .set(SKILLS.ROOT_ID, skill.getRootId())
                .set(SKILLS.SKILL_TIME, skill.getSkillTime())
                .set(SKILLS.SKILL_TIME_LOCAL, skill.getSkillTimeLocal())
                .set(SKILLS.DATE_CREATED, skill.getDateCreated())
                .set(SKILLS.NAME, skill.getDisplayName())
                .set(SKILLS.DESCRIPTION, skill.getDescription())
                .set(SKILLS.WEIGHT, skill.getWeight())
                .returning()
                .fetchOne(record -> new Skill(
                        record.getUserId(),
                        record.getSkillId(),
                        record.getParentId(),
                        record.getRootId(),
                        record.getName(),
                        record.getDescription(),
                        record.getSkillTime(),
                        record.getSkillTimeLocal(),
                        record.getWeight(),
                        record.getDateCreated()
                ));
    }

    public Skill update(Skill skill) {
        return dsl.update(SKILLS)
                .set(SKILLS.NAME, skill.getDisplayName())
                .set(SKILLS.DESCRIPTION, skill.getDescription())
                .set(SKILLS.WEIGHT, skill.getWeight())
                .where(SKILLS.SKILL_ID.eq(skill.getSkillId()))
                .returning()
                .fetchOne(record -> new Skill(
                        record.getUserId(),
                        record.getSkillId(),
                        record.getParentId(),
                        record.getRootId(),
                        record.getName(),
                        record.getDescription(),
                        record.getSkillTime(),
                        record.getSkillTimeLocal(),
                        record.getWeight(),
                        record.getDateCreated()
                ));
    }

    public Skill updateParent(UUID skillId, UUID newParentId) {
        if(newParentId == null) {
            return null;
        }

        return dsl.update(SKILLS)
                .set(SKILLS.PARENT_ID, newParentId)
                .where(SKILLS.SKILL_ID.eq(skillId))
                .returning()
                .fetchOne(record -> new Skill(
                        record.getUserId(),
                        record.getSkillId(),
                        record.getParentId(),
                        record.getRootId(),
                        record.getName(),
                        record.getDescription(),
                        record.getSkillTime(),
                        record.getSkillTimeLocal(),
                        record.getWeight(),
                        record.getDateCreated()
                ));
    }

    public Optional<Skill> findById(UUID skillId) {
        return dsl.selectFrom(SKILLS)
                .where(SKILLS.SKILL_ID.eq(skillId))
                .fetchOptional()
                .map(record -> new Skill(
                        record.getUserId(),
                        record.getSkillId(),
                        record.getParentId(),
                        record.getRootId(),
                        record.getName(),
                        record.getDescription(),
                        record.getSkillTime(),
                        record.getSkillTimeLocal(),
                        record.getWeight(),
                        record.getDateCreated()
                ));
    }

    /**
     * Adds an amount, delta, to the local time of the specified skill. Then propagates up the tree to SkillTime of parent nodes
     * @param skillId
     * @param delta
     * @return number of rows affected
     */
    //TODO: Revamp this sometime to actually understand it. Will admit that this was converted from pure postgres query to DSL using AI
    public int propagate(UUID skillId, long delta) {
        return dsl.withRecursive(
                        name("ancestors").fields("skill_id", "parent_id").as(
                                select(SKILLS.SKILL_ID, SKILLS.PARENT_ID)
                                        .from(SKILLS)
                                        .where(SKILLS.SKILL_ID.eq(skillId))
                                        .unionAll(
                                                select(SKILLS.SKILL_ID, SKILLS.PARENT_ID)
                                                        .from(SKILLS)
                                                        .join(table(name("ancestors")))
                                                        .on(SKILLS.SKILL_ID.eq(field(name("ancestors", "parent_id"), UUID.class)))
                                        )
                        )
                )
                .update(SKILLS)
                .set(SKILLS.SKILL_TIME, SKILLS.SKILL_TIME.plus(delta))
                .set(SKILLS.SKILL_TIME_LOCAL,
                        when(SKILLS.SKILL_ID.eq(skillId), SKILLS.SKILL_TIME_LOCAL.plus(delta))
                                .otherwise(SKILLS.SKILL_TIME_LOCAL))
                .where(SKILLS.SKILL_ID.in(
                        select(field(name("ancestors", "skill_id"), UUID.class)).from(name("ancestors"))
                ))
                .execute();
    }

    /**
     * Adds or subtracts the moved skill's SKILL_TIME from the given parent and
     * all of its ancestors. Used when reparenting: call with the OLD parent and
     * adding=false to remove the moved subtree's time from its former ancestors,
     * then with the NEW parent and adding=true to add it to the new ancestors.
     *
     * @param parentId the ancestor chain to update (inclusive)`1
     * @return number of rows affected
     */
    public int propagateFromParent(UUID parentId, long delta) {   // ← signature changed
        return dsl.withRecursive(
                        name("ancestors").fields("skill_id", "parent_id").as(
                                select(SKILLS.SKILL_ID, SKILLS.PARENT_ID)
                                        .from(SKILLS)
                                        .where(SKILLS.SKILL_ID.eq(parentId))
                                        .unionAll(
                                                select(SKILLS.SKILL_ID, SKILLS.PARENT_ID)
                                                        .from(SKILLS)
                                                        .join(table(name("ancestors")))
                                                        .on(SKILLS.SKILL_ID.eq(field(name("ancestors", "parent_id"), UUID.class)))
                                        )
                        )
                )
                .update(SKILLS)
                .set(SKILLS.SKILL_TIME, SKILLS.SKILL_TIME.plus(delta))   // ← no more subquery/boolean
                .where(SKILLS.SKILL_ID.in(
                        select(field(name("ancestors", "skill_id"), UUID.class)).from(name("ancestors"))
                ))
                .execute();
    }

    public List<Skill> findRootsByUserId(UUID userId) {
        return dsl.selectFrom(SKILLS)
                .where(SKILLS.USER_ID.eq(userId)
                        .and(SKILLS.PARENT_ID.isNull()))
                .orderBy(SKILLS.DATE_CREATED.desc())
                .fetch(record -> new Skill(
                        record.getUserId(),
                        record.getSkillId(),
                        record.getParentId(),
                        record.getRootId(),
                        record.getName(),
                        record.getDescription(),
                        record.getSkillTime(),
                        record.getSkillTimeLocal(),
                        record.getWeight(),
                        record.getDateCreated()
                ));
    }

    public List<Skill> findChildren(UUID skillId) {
        return dsl.selectFrom(SKILLS)
                .where(SKILLS.PARENT_ID.eq(skillId))
                .orderBy(SKILLS.WEIGHT)
                .fetch(record -> new Skill(
                        record.getUserId(),
                        record.getSkillId(),
                        record.getParentId(),
                        record.getRootId(),
                        record.getName(),
                        record.getDescription(),
                        record.getSkillTime(),
                        record.getSkillTimeLocal(),
                        record.getWeight(),
                        record.getDateCreated()
                ));
    }
}
