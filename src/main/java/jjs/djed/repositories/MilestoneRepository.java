package jjs.djed.repositories;


import jjs.djed.model.Milestone;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

import static jjs.djed.jooq.Tables.MILESTONES;

@Repository
public class MilestoneRepository {
    private final DSLContext dsl;
    public MilestoneRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Milestone insert(Milestone milestone) {
        return dsl.insertInto(MILESTONES)
                .set(MILESTONES.MILESTONE_ID, milestone.getMilestoneUuid())
                .set(MILESTONES.DESCRIPTION, milestone.getName())
                .set(MILESTONES.NAME, milestone.getName())
                .set(MILESTONES.TIME_SECONDS, milestone.getRequiredSeconds())
                .returning()
                .fetchOne(record -> new Milestone(
                        record.getMilestoneId(),
                        record.getDescription(),
                        record.getName(),
                        record.getTimeSeconds()
                ));
    }

    public Milestone update(Milestone milestone) {
        return dsl.update(MILESTONES)
                .set(MILESTONES.MILESTONE_ID, milestone.getMilestoneUuid())
                .set(MILESTONES.DESCRIPTION, milestone.getName())
                .set(MILESTONES.NAME, milestone.getName())
                .set(MILESTONES.TIME_SECONDS, milestone.getRequiredSeconds())
                .where(MILESTONES.MILESTONE_ID.eq(milestone.getMilestoneUuid()))
                .returning()
                .fetchOne(record -> new Milestone(
                        record.getMilestoneId(),
                        record.getDescription(),
                        record.getName(),
                        record.getTimeSeconds()
                ));
    }

    public Optional<Milestone> getById(UUID id) {
        return dsl.selectFrom(MILESTONES)
                .where(MILESTONES.MILESTONE_ID.eq(id))
                .fetchOptional()
                .map(record -> new Milestone(
                        record.getMilestoneId(),
                        record.getDescription(),
                        record.getName(),
                        record.getTimeSeconds()
                ));
    }
}
