package jjs.djed;

import com.zaxxer.hikari.HikariDataSource;
import jjs.djed.data.ConfigLoader;
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
        ConfigLoader.loadConfig();

    }
}