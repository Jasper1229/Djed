package jjs.djed.controllers;

import jjs.djed.model.Milestone;
import jjs.djed.services.MilestoneService;
import jjs.djed.web.post.CreateMilestoneRequest;
import jjs.djed.web.patch.UpdateMilestoneRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class MilestoneController {
    private final MilestoneService milestoneService;
    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @GetMapping("/milestones/{id}")
    public ResponseEntity<Milestone> getMilestone(@PathVariable UUID id) {
        Milestone milestone = milestoneService.getMilestone(id);
        if(milestone == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(milestone);
    }

    @PostMapping("/milestones")
    public ResponseEntity<Milestone> createMilestone(@RequestBody CreateMilestoneRequest request) {
        Milestone milestone = milestoneService.createMilestone(request.skillId(), request.name(), request.description(), request.requiredSeconds());
        return ResponseEntity.ok(milestone);
    }

    @PatchMapping("/milestones/{id}")
    public ResponseEntity<Milestone> updateMilestone(@PathVariable UUID id, @RequestBody UpdateMilestoneRequest request) {
        Milestone milestone = milestoneService.getMilestone(id);
        if(milestone == null) {
            return ResponseEntity.notFound().build();
        }

        if (request.description() != null) {
            milestone.setDescription(request.description());
        }
        if (request.name() != null) {
            milestone.setName(request.name());
        }
        if (request.requiredSeconds() != null) {
            milestone.setRequiredSeconds(request.requiredSeconds());
        }

        milestoneService.updateMilestone(milestone);
        return ResponseEntity.ok(milestone);
    }
    @GetMapping("/skills/{id}/milestones")
    public ResponseEntity<List<Milestone>> getSkillMilestones(@PathVariable UUID id) {
        return ResponseEntity.ok(milestoneService.getSkillMilestones(id));
    }
}
