package jjs.djed.web.post;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateSessionRequest(UUID skillId, OffsetDateTime startTime, OffsetDateTime endTime) {
}
