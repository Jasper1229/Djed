package jjs.djed.repositories;


import jjs.djed.model.Session;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static jjs.djed.jooq.Tables.SESSIONS;


@Repository
public class SessionRepository {
    private final DSLContext dsl;

    public SessionRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Session insert(Session session) {
        return dsl.insertInto(SESSIONS)
                .set(SESSIONS.SESSION_ID, session.getSessionId())
                .set(SESSIONS.SKILL_ID, session.getSkillId())
                .set(SESSIONS.START_TIME, session.getStartTime())
                .set(SESSIONS.END_TIME, session.getEndTime())
                .returning()
                .fetchOne(record -> new Session(
                        record.getSessionId(),
                        record.getSkillId(),
                        record.getStartTime(),
                        record.getEndTime()
                ));
    }

    public Session update(Session session) {
        return dsl.update(SESSIONS)
                .set(SESSIONS.SKILL_ID, session.getSkillId())
                .set(SESSIONS.START_TIME, session.getStartTime())
                .set(SESSIONS.END_TIME, session.getEndTime())
                .where(SESSIONS.SESSION_ID.eq(session.getSessionId()))
                .returning()
                .fetchOne(record -> new Session(
                        record.getSessionId(),
                        record.getSkillId(),
                        record.getStartTime(),
                        record.getEndTime()
                ));
    }

    /**
     * Deletes session from database. Returns the deleted session object
     * @param sessionId
     * @return
     */
    public Optional<Session> delete(UUID sessionId) {
        return dsl.deleteFrom(SESSIONS)
                .where(SESSIONS.SESSION_ID.eq(sessionId))
                .returning()
                .fetchOptional(record -> new Session(
                        record.getSessionId(),
                        record.getSkillId(),
                        record.getStartTime(),
                        record.getEndTime()
                ));
    }

    public Optional<Session> getById(UUID sessionId) {
        return dsl.selectFrom(SESSIONS)
                .where(SESSIONS.SESSION_ID.eq(sessionId))
                .fetchOptional(record -> new Session(
                        record.getSessionId(),
                        record.getSkillId(),
                        record.getStartTime(),
                        record.getEndTime()
                ));
    }

    public List<Session> getSessionsBySkillId(UUID skillId) {
        return dsl.selectFrom(SESSIONS)
                .where(SESSIONS.SKILL_ID.eq(skillId))
                .orderBy(SESSIONS.START_TIME.desc())
                .fetch(record -> new Session(
                        record.getSessionId(),
                        record.getSkillId(),
                        record.getStartTime(),
                        record.getEndTime()
                ));
    }

}
