package jjs.djed.data;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ConfigLoader {

    private static Map<String, Object> configData;


    private ConfigLoader() {}

    public static void loadConfig() {
        Yaml yaml = new Yaml();
        Path localConfig = Paths.get("config.yml");
        String targetFile = Files.exists(localConfig) ? "config.yml" : "config.example.yml";

        try (InputStream inputStream = Files.newInputStream(Paths.get(targetFile))) {
            configData = yaml.load(inputStream);
        } catch (Exception e) {
            throw new ExceptionInInitializerError("Failed to load configuration file: " + targetFile + " -> " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getDatabaseSection() {
        if (configData == null) return Map.of();
        return (Map<String, Object>) configData.get("database");
    }

    public static String getDatabaseUrl() {
        Map<String, Object> databaseSection = getDatabaseSection();
        return (String) databaseSection.get("url");
    }

    public static String getUsername() {
        Map<String, Object> databaseSection = getDatabaseSection();
        return (String) databaseSection.get("username");
    }

    public static String getPassword() {
        Map<String, Object> databaseSection = getDatabaseSection();
        return (String) databaseSection.get("password");
    }


}
