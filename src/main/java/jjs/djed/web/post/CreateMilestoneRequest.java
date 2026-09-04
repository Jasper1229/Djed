package jjs.djed.web.post;

import java.util.UUID;

public record CreateMilestoneRequest(UUID skillId, String name, String description, long requiredSeconds) {}
