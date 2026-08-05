package jjs.djed;

import com.zaxxer.hikari.HikariDataSource;
import jjs.djed.data.Database;
import jjs.djed.data.SkillDatabase;
import jjs.djed.model.Skill;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.UUID;

public class Main {
    public static HikariDataSource DATASOURCE;
    public static SkillDatabase SKILL_DATABASE;

    static void main() {
        try (Connection conn = Database.getConnection()) {
            System.out.println("Successfully connected to PostgreSQL via HikariCP!");
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
        }
        DATASOURCE = Database.getDataSource();
        SKILL_DATABASE = new SkillDatabase(DATASOURCE);

        testTwoBranchTree();
    }

    static void testTwoBranchTree() {
        UUID userId = UUID.randomUUID();
        UUID templateId = UUID.randomUUID();

        // --- Build the tree ---
        Skill root = new Skill(templateId, userId, null, "Root", "Top level skill");

        Skill childA = new Skill(templateId, userId, root.getSkillId(), "Branch A", "First branch");
        Skill childB = new Skill(templateId, userId, root.getSkillId(), "Branch B", "Second branch");

        Skill grandchildA1 = new Skill(templateId, userId, childA.getSkillId(), "A1", "Leaf");
        Skill grandchildA2 = new Skill(templateId, userId, childA.getSkillId(), "A2", "Leaf");
        Skill grandchildB1 = new Skill(templateId, userId, childB.getSkillId(), "B1", "Leaf");
        Skill grandchildB2 = new Skill(templateId, userId, childB.getSkillId(), "B2", "Leaf");

        root.addChild(childA);
        root.addChild(childB);
        childA.addChild(grandchildA1);
        childA.addChild(grandchildA2);
        childB.addChild(grandchildB1);
        childB.addChild(grandchildB2);

        persist(root);
        persist(childA);
        persist(childB);
        persist(grandchildA1);
        persist(grandchildA2);
        persist(grandchildB1);
        persist(grandchildB2);

        System.out.println("=== Initial state (all zero) ===");
        printTree(root.getSkillId(), "", true);

        // --- Add time on branch A only ---
        grandchildA1.addTime(Duration.ofMinutes(30));

        assertDbTime(grandchildA1.getSkillId(), Duration.ofMinutes(30), "A1");
        assertDbTime(grandchildA2.getSkillId(), Duration.ZERO, "A2 (untouched sibling)");
        assertDbTime(childA.getSkillId(), Duration.ofMinutes(30), "childA");
        assertDbTime(childB.getSkillId(), Duration.ZERO, "childB (should NOT receive A's time)");
        assertDbTime(grandchildB1.getSkillId(), Duration.ZERO, "B1 (untouched)");
        assertDbTime(grandchildB2.getSkillId(), Duration.ZERO, "B2 (untouched)");
        assertDbTime(root.getSkillId(), Duration.ofMinutes(30), "root");

        System.out.println("\n=== After A1.addTime(30m) — branch B should be untouched ===");
        printTree(root.getSkillId(), "", true);

        // --- Add time on branch B ---
        grandchildB1.addTime(Duration.ofMinutes(20));

        assertDbTime(grandchildB1.getSkillId(), Duration.ofMinutes(20), "B1");
        assertDbTime(grandchildB2.getSkillId(), Duration.ZERO, "B2 (untouched sibling)");
        assertDbTime(childB.getSkillId(), Duration.ofMinutes(20), "childB");
        assertDbTime(childA.getSkillId(), Duration.ofMinutes(30), "childA (should be unaffected by B's time)");
        assertDbTime(root.getSkillId(), Duration.ofMinutes(50), "root (30 + 20 across both branches)");

        System.out.println("\n=== After B1.addTime(20m) — root aggregates both branches ===");
        printTree(root.getSkillId(), "", true);

        // --- One more addition on branch A's second leaf ---
        grandchildA2.addTime(Duration.ofMinutes(10));

        assertDbTime(grandchildA2.getSkillId(), Duration.ofMinutes(10), "A2");
        assertDbTime(childA.getSkillId(), Duration.ofMinutes(40), "childA (30 + 10)");
        assertDbTime(root.getSkillId(), Duration.ofMinutes(60), "root (30 + 10 + 20)");

        System.out.println("\n=== After A2.addTime(10m) — final expected totals: root=60m, childA=40m, childB=20m ===");
        printTree(root.getSkillId(), "", true);

        // --- recalculateSkillDuration should independently derive the same root total from leaves ---
        Duration recalculated = root.recalculateSkillDuration();
        require(recalculated.equals(Duration.ofMinutes(60)),
                "recalculateSkillDuration should give 60m but was " + recalculated);
        System.out.println("\nrecalculateSkillDuration(root) = " + recalculated);

        // --- Full structural + value round-trip from DB ---
        Skill rA1 = SKILL_DATABASE.getSkillById(grandchildA1.getSkillId());
        Skill rA2 = SKILL_DATABASE.getSkillById(grandchildA2.getSkillId());
        Skill rB1 = SKILL_DATABASE.getSkillById(grandchildB1.getSkillId());
        Skill rB2 = SKILL_DATABASE.getSkillById(grandchildB2.getSkillId());

        require(childA.getSkillId().equals(rA1.getParentSkillId()), "A1's parent should be childA");
        require(childA.getSkillId().equals(rA2.getParentSkillId()), "A2's parent should be childA");
        require(childB.getSkillId().equals(rB1.getParentSkillId()), "B1's parent should be childB");
        require(childB.getSkillId().equals(rB2.getParentSkillId()), "B2's parent should be childB");

        require(rA1.getSkillTime().equals(Duration.ofMinutes(30)), "A1 should persist at 30m");
        require(rA2.getSkillTime().equals(Duration.ofMinutes(10)), "A2 should persist at 10m");
        require(rB1.getSkillTime().equals(Duration.ofMinutes(20)), "B1 should persist at 20m");
        require(rB2.getSkillTime().equals(Duration.ZERO), "B2 should still be untouched at 0");

        System.out.println("\nAll two-branch tree / propagation / isolation / persistence tests passed.");
    }

    /**
     * Recursively prints the skill tree as ASCII art, reloading each node fresh
     * from the database so the visualization always reflects persisted state.
     *
     *   Root [PT1H]
     *   ├── Branch A [PT40M]
     *   │   ├── A1 [PT30M]
     *   │   └── A2 [PT10M]
     *   └── Branch B [PT20M]
     *       ├── B1 [PT20M]
     *       └── B2 [PT0S]
     */
    private static void printTree(UUID skillId, String prefix, boolean isRoot) {
        Skill skill = SKILL_DATABASE.getSkillById(skillId);
        if (skill == null) {
            System.out.println(prefix + "(missing: " + skillId + ")");
            return;
        }

        if (isRoot) {
            System.out.println(skill.getDisplayName() + " [" + skill.getSkillTime() + "]");
        }

        UUID[] childIds = childIdsOf(skillId);
        for (int i = 0; i < childIds.length; i++) {
            boolean last = (i == childIds.length - 1);
            Skill child = SKILL_DATABASE.getSkillById(childIds[i]);

            System.out.println(prefix + (last ? "└── " : "├── ")
                    + child.getDisplayName() + " [" + child.getSkillTime() + "]");

            printTreeChildren(childIds[i], prefix + (last ? "    " : "│   "));
        }
    }

    private static void printTreeChildren(UUID skillId, String prefix) {
        UUID[] childIds = childIdsOf(skillId);
        for (int i = 0; i < childIds.length; i++) {
            boolean last = (i == childIds.length - 1);
            Skill child = SKILL_DATABASE.getSkillById(childIds[i]);

            System.out.println(prefix + (last ? "└── " : "├── ")
                    + child.getDisplayName() + " [" + child.getSkillTime() + "]");

            printTreeChildren(childIds[i], prefix + (last ? "    " : "│   "));
        }
    }

    // Hardcoded lookup since SkillDatabase has no "find children by parent" query yet.
    // Swap this out for a real SQL query (SELECT skill_id FROM skills.skill WHERE parent_skill_id = ?)
    // if you want printTree to work on arbitrary trees rather than this fixed test shape.
    private static UUID[] childIdsOf(UUID skillId) {
        return KNOWN_CHILDREN.getOrDefault(skillId, new UUID[0]);
    }

    private static final java.util.Map<UUID, UUID[]> KNOWN_CHILDREN = new java.util.HashMap<>();

    private static void registerChild(UUID parent, UUID child) {
        UUID[] existing = KNOWN_CHILDREN.getOrDefault(parent, new UUID[0]);
        UUID[] updated = java.util.Arrays.copyOf(existing, existing.length + 1);
        updated[existing.length] = child;
        KNOWN_CHILDREN.put(parent, updated);
    }

    private static void persist(Skill skill) {
        SKILL_DATABASE.upsertSkill(
                skill.getSkillId(),
                skill.getTemplateId(),
                skill.getUserId(),
                skill.getParentSkillId(),
                skill.getWeight(),
                skill.getSkillTime().getSeconds(),
                skill.getDisplayName(),
                skill.getDescription()
        );
        if (skill.getParentSkillId() != null) {
            registerChild(skill.getParentSkillId(), skill.getSkillId());
        }
    }

    private static void assertDbTime(UUID skillId, Duration expected, String label) {
        Skill fromDb = SKILL_DATABASE.getSkillById(skillId);
        require(fromDb.getSkillTime().equals(expected),
                label + " should be " + expected + " in DB but was " + fromDb.getSkillTime());
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}