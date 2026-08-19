package jjs.djed.web.post;

public record CreateMilestoneRequest(String description, String name, long requiredSeconds) {}
