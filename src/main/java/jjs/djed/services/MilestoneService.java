package jjs.djed.services;

import jjs.djed.model.Milestone;
import jjs.djed.repositories.MilestoneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MilestoneService {
    private final MilestoneRepository milestoneRepository;
    public MilestoneService(MilestoneRepository milestoneRepository) {
        this.milestoneRepository = milestoneRepository;
    }

    public Milestone createMilestone(UUID skillId, String description, String name, long requiredSeconds) {
        Milestone milestone = new Milestone(skillId, description, name, requiredSeconds);
        return milestoneRepository.insert(milestone);
    }

    public Milestone updateMilestone(Milestone milestone) {
        return milestoneRepository.update(milestone);
    }

    public Milestone getMilestone(UUID id) {
        return milestoneRepository.getById(id).orElse(null);
    }

    public List<Milestone> getSkillMilestones(UUID skillId) {
        return milestoneRepository.getSkillMilestones(skillId);
    }
}
