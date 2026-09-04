package jjs.djed.model;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Session {
    private final UUID sessionId;
    private final UUID skillId;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;

    public Session(UUID sessionId, UUID skillId, OffsetDateTime startTime, OffsetDateTime endTime) {
        this.sessionId = sessionId;
        this.skillId = skillId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public UUID getSessionId() {
        return sessionId;
    }
    public UUID getSkillId() {
        return skillId;
    }
    public OffsetDateTime getStartTime() {
        return startTime;
    }
    public OffsetDateTime getEndTime() {
        return endTime;
    }
    public void setStartTime(OffsetDateTime startTime) {
        this.startTime = startTime;
    }
    public void setEndTime(OffsetDateTime endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return endTime.toEpochSecond() - startTime.toEpochSecond();
    }
}
