package jjs.djed.services;


import jjs.djed.model.Skill;
import jjs.djed.repositories.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public boolean doesSkillExist(UUID skillId) {
        return skillRepository.findById(skillId).isPresent();
    }

    public List<Skill> getRootsByUserId(UUID userId) {
        return skillRepository.findRootsByUserId(userId);
    }

    @Transactional
    public Skill updateParentSkill(UUID skillId, UUID newParentSkillId) {
        Skill skill = skillRepository.findById(skillId).orElse(null);
        Skill newParent = skillRepository.findById(newParentSkillId).orElse(null);
        if (newParent == null) {
            return null;
        }
        if (
                skill == null ||
                        newParentSkillId.equals(skill.getParentSkillId()) ||
                        !(newParent.getRootId().equals(skill.getRootId())) ||
                        newParentSkillId.equals(skillId)
        ) {
            return null;
        }
        UUID cursor = newParent.getParentSkillId();
        while (cursor != null) {
            if (cursor.equals(skillId)) {
                return null;
            }
            Skill current = skillRepository.findById(cursor).orElse(null);
            cursor = (current == null) ? null : current.getParentSkillId();
        }

        long subtreeTime = skill.getSkillTime();   // ← NEW: read once

        skillRepository.propagateFromParent(skill.getParentSkillId(), -subtreeTime);   // ← changed
        skillRepository.propagateFromParent(newParentSkillId, subtreeTime);            // ← changed
        return skillRepository.updateParent(skillId, newParentSkillId);
    }

    public List<Skill> getChildren(UUID skillId) {
        return skillRepository.findChildren(skillId);
    }
}
