package jjs.djed.web.patch;

import java.util.UUID;

public record UpdateSkillRequest(UUID parentId, String name, String description, Short weight) {}
