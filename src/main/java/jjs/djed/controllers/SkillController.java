package jjs.djed.controllers;

import jjs.djed.model.Skill;
import jjs.djed.services.SkillService;
import jjs.djed.web.patch.UpdateSkillRequest;
import jjs.djed.web.post.CreateSkillRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills/{id}")
    public ResponseEntity<Skill> getSkill(@PathVariable UUID id) {
        Skill skill = skillService.getSkill(id);
        if(skill == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skill);
    }

    @PostMapping("/skills")
    public ResponseEntity<Skill> createSkill(@RequestBody CreateSkillRequest request) {
        Skill skill = skillService.createSkill(request.userId(), request.parentId(), request.displayName(), request.description());
        return ResponseEntity.ok(skill);
    }

    @PatchMapping("/skills/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable UUID id, @RequestBody UpdateSkillRequest request) {
        Skill skill = skillService.getSkill(id);
        if(skill == null) {
            return ResponseEntity.notFound().build();
        }
        if(request.parentId() != null) {
            skill.setParentSkillId(request.parentId());
        }
        if(request.name() != null) {
            skill.setDisplayName(request.name());
        }
        if(request.description() != null) {
            skill.setDescription(request.description());
        }
        if(request.weight() != null) {
            skill.setWeight(request.weight());
        }
        skillService.updateSkill(skill);
        return ResponseEntity.ok(skill);
    }

    
}
