package jjs.djed.controllers;

import jjs.djed.model.Session;
import jjs.djed.services.SkillSessionService;
import jjs.djed.services.SkillService;
import jjs.djed.web.patch.UpdateSessionSkillRequest;
import jjs.djed.web.patch.UpdateSessionTimesRequest;
import jjs.djed.web.post.CreateSessionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class SkillSessionController {
    private final SkillSessionService skillSessionService;
    private final SkillService skillService;
    public SkillSessionController(SkillSessionService skillSessionService, SkillService skillService) {
        this.skillSessionService = skillSessionService;
        this.skillService = skillService;
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<Session> getSession(@PathVariable UUID id) {
        Session session = skillSessionService.getSession(id);
        if (session == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(session);
    }

    @PostMapping("/sessions")
    public ResponseEntity<Session> createSession(@RequestBody CreateSessionRequest request) {
        if(!isValidDuration(request.startTime(), request.endTime())) {
            return ResponseEntity.badRequest().build();
        }
        Session session = skillSessionService.create(request.skillId(), request.startTime(), request.endTime());
        return ResponseEntity.ok(session);
    }

    @PatchMapping("/sessions/{id}/skill_id")
    public ResponseEntity<Session> updateSessionSkill(@PathVariable UUID id, @RequestBody UpdateSessionSkillRequest request) {
        Session getSession = skillSessionService.getSession(id);
        if(getSession == null) {
            return ResponseEntity.notFound().build();
        }
        Session session = skillSessionService.updateSkill(getSession, request.skillId());
        return ResponseEntity.ok(session);
    }

    @PatchMapping("/sessions/{id}/times")
    public ResponseEntity<Session> updateSessionTimes(@PathVariable UUID id, @RequestBody UpdateSessionTimesRequest request) {
        Session getSession = skillSessionService.getSession(id);
        if(getSession == null) {
            return ResponseEntity.notFound().build();
        }
        if(!isValidDuration(request.startTime(), request.endTime())) {
            return ResponseEntity.badRequest().build();
        }
        Session session = skillSessionService.updateDuration(getSession, request.startTime(), request.endTime());
        return ResponseEntity.ok(session);
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Session> deleteSession(@PathVariable UUID id) {
        Session session = skillSessionService.getSession(id);
        if(session == null) {
            return ResponseEntity.notFound().build();
        }
        skillSessionService.delete(id);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/skills/{id}/sessions")
    public ResponseEntity<List<Session>> getSessionsBySkillId(@PathVariable UUID id) {
        if (!(skillService.doesSkillExist(id))) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skillSessionService.getSessionsBySkillId(id));
    }
    private boolean isValidDuration(OffsetDateTime start, OffsetDateTime end) {
        return !start.isAfter(end);
    }



}
