package jjs.djed.services;

import jjs.djed.model.Session;
import jjs.djed.repositories.SessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SkillService skillService;
    public SessionService(SessionRepository sessionRepository, SkillService skillService) {
        this.sessionRepository = sessionRepository;
        this.skillService = skillService;
    }

    @Transactional
    public Session create(UUID skillId, OffsetDateTime startTime, OffsetDateTime endTime) {
            UUID sessionId = UUID.randomUUID();
            Session session = new Session(
                    sessionId,
                    skillId,
                    startTime,
                    endTime
            );

            skillService.addDeltaTime(skillId, session.getDuration());

            return sessionRepository.insert(session);
    }

    @Transactional
    public void delete(UUID sessionId) {
        Session session = sessionRepository.delete(sessionId).orElse(null);
        if(session == null) {
            throw new RuntimeException("Session not found");
        }
        long duration = session.getDuration();
        skillService.addDeltaTime(session.getSkillId(), -duration);
    }

    @Transactional
    public Session updateDuration(Session session, OffsetDateTime newStart, OffsetDateTime newEnd) {
        long originalDuration = session.getDuration();

        session.setStartTime(newStart);
        session.setEndTime(newEnd);
        long newDuration = session.getDuration();

        long delta = newDuration - originalDuration;
        Session updated = sessionRepository.update(session);

        if (delta != 0) {
            skillService.addDeltaTime(session.getSkillId(), delta);
        }

        return updated;
    }

    @Transactional
    public Session updateSkill(Session session, UUID newSkillId) {
        if (!(skillService.doesSkillExist(newSkillId))) {
            throw new RuntimeException("Skill " + newSkillId + " not found");
        }

        long duration = session.getDuration();

        Session updated = new Session(
                session.getSessionId(),
                newSkillId,
                session.getStartTime(),
                session.getEndTime()
        );

        Session result = sessionRepository.update(updated);

        skillService.addDeltaTime(session.getSkillId(), -duration);
        skillService.addDeltaTime(newSkillId, duration);

        return result;
    }

    public Session getSession(UUID sessionId) {
        return sessionRepository.getById(sessionId).orElse(null);
    }
    public List<Session> getSessionsBySkillId(UUID skillId) {
        return sessionRepository.getSessionsBySkillId(skillId);
    }



}
