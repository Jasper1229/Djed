package jjs.djed.web.patch;

public record UpdateMilestoneRequest(String name, String description, Long requiredSeconds) {}
