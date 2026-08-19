package jjs.djed.web.post;

import java.util.UUID;

public record CreateSkillRequest(UUID userId, UUID parentId, String displayName, String description) {}