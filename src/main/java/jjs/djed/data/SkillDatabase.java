package jjs.djed.data;

import com.zaxxer.hikari.HikariDataSource;
import jjs.djed.model.Skill;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public class SkillDatabase {

    private final HikariDataSource dataSource;

    public SkillDatabase(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public Skill getSkillById(UUID skillUuid) {
        String sql = """
            SELECT 
                skill_id,
                template_id,
                user_id,
                created_at,
                parent_skill_id,
                display_weight,
                skill_time_seconds,
                display_name,
                description
            FROM skills.skill
            WHERE skill_id = ?;
            """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, skillUuid);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extract fields matching the 9-argument constructor
                    UUID skillId = rs.getObject("skill_id", UUID.class);
                    UUID templateId = rs.getObject("template_id", UUID.class);
                    UUID userId = rs.getObject("user_id", UUID.class);
                    UUID parentSkillId = rs.getObject("parent_skill_id", UUID.class);
                    String displayName = rs.getString("display_name");
                    String description = rs.getString("description");

                    // Convert bigint (seconds) -> java.time.Duration
                    long seconds = rs.getLong("skill_time_seconds");
                    Duration skillTime = Duration.ofSeconds(seconds);

                    int weight = rs.getInt("display_weight");

                    // Convert timestamp with time zone -> java.time.Instant
                    Timestamp timestamp = rs.getTimestamp("created_at");
                    Instant dateCreated = timestamp != null ? timestamp.toInstant() : null;

                    // Pass values to the second constructor matching your model
                    Skill skill = new Skill(
                            skillId,
                            templateId,
                            userId,
                            parentSkillId,
                            displayName,
                            description,
                            skillTime,
                            weight,
                            dateCreated
                    );

                    return (skill);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching skill with ID: " + skillUuid, e);
        }

        return null;
    }

    public void upsertSkill(
            UUID skillId,
            UUID templateId,
            UUID userId,
            UUID parentSkillId,
            int displayWeight,
            long skillTimeSeconds,
            String displayName,
            String description
    ) {
        String sql = """
            INSERT INTO skills.skill (
                skill_id,
                template_id,
                user_id,
                created_at,
                parent_skill_id,
                display_weight,
                skill_time_seconds,
                display_name,
                description
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (skill_id) 
            DO UPDATE SET
                template_id = EXCLUDED.template_id,
                user_id = EXCLUDED.user_id,
                parent_skill_id = EXCLUDED.parent_skill_id,
                display_weight = EXCLUDED.display_weight,
                skill_time_seconds = EXCLUDED.skill_time_seconds,
                display_name = EXCLUDED.display_name,
                description = EXCLUDED.description;
            """;

        // Obtain connection from HikariCP pool
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, skillId);
            stmt.setObject(2, templateId);
            stmt.setObject(3, userId);
            stmt.setTimestamp(4, Timestamp.from(Instant.now()));
            stmt.setObject(5, parentSkillId); // Sets SQL NULL if parentSkillId is null
            stmt.setInt(6, displayWeight);
            stmt.setLong(7, skillTimeSeconds);
            stmt.setString(8, displayName);
            stmt.setString(9, description);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error executing upsert for skill", e);
        }
    }
}
