package jjs.djed.services;


import jjs.djed.model.Skill;
import jjs.djed.repositories.SkillRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SkillService {
    private final SkillRepository skillRepository;
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skill createSkill(UUID userId, UUID parentSkillId, String displayName, String description) {
        UUID skillId = UUID.randomUUID();
        Skill skill = new Skill(
                userId,
                skillId,
                parentSkillId,
                getRootSkillId(parentSkillId, skillId),
                displayName,
                description
        );

        return skillRepository.insert(skill);
    }

    public Skill updateSkill(Skill skill) {
        return skillRepository.update(skill);
    }

    private UUID getRootSkillId(UUID parentId, UUID selfId) {
        if (parentId == null) {
            return selfId;
        }
        return skillRepository.findById(parentId)
                .map(Skill::getRootId)
                .orElseThrow(() -> new IllegalStateException("Parent skill not found: " + parentId));
    }

    public Skill getSkill(UUID id) {
        Optional<Skill> os = skillRepository.findById(id);
        return os.orElse(null);
    }

    public int addDeltaTime(UUID skillId, long delta) {
        return skillRepository.propagate(skillId, delta);
    }
}
