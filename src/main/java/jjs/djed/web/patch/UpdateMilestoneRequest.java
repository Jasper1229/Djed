package jjs.djed.web.patch;

public record UpdateMilestoneRequest(String description, String name, Long requiredSeconds) {}
