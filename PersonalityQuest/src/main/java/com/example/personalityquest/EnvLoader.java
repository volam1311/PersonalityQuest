package com.example.personalityquest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads key=value pairs from a local .env file, with process environment variables taking priority.
 */
public final class EnvLoader {
    private static Map<String, String> cachedFileValues;

    private EnvLoader() {
    }

    /**
     * Looks up a value from the process environment first, then from a nearby .env file.
     */
    public static String Get(String key) {
        if (ApplicationManager.isEmpty(key)) {
            return "";
        }

        String fromProcess = System.getenv(key);
        if (!ApplicationManager.isEmpty(fromProcess)) {
            return fromProcess.trim();
        }

        String fromFile = FileValues().get(key);
        return fromFile == null ? "" : fromFile;
    }

    /**
     * Parses dotenv-style contents into a map. Later duplicate keys overwrite earlier ones.
     */
    public static Map<String, String> Parse(String contents) {
        Map<String, String> values = new HashMap<>();
        if (contents == null || contents.isEmpty()) {
            return values;
        }

        for (String rawLine : contents.split("\\R")) {
            String line = rawLine.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("export ")) {
                line = line.substring("export ".length()).trim();
            }

            int separator = line.indexOf('=');
            if (separator <= 0) {
                continue;
            }

            String key = line.substring(0, separator).trim();
            String value = Unquote(line.substring(separator + 1).trim());
            if (!key.isEmpty()) {
                values.put(key, value);
            }
        }
        return values;
    }

    /**
     * Clears the cached .env file so the next lookup reads from disk again.
     */
    public static void Reset() {
        cachedFileValues = null;
    }

    private static Map<String, String> FileValues() {
        if (cachedFileValues != null) {
            return cachedFileValues;
        }

        Map<String, String> values = new HashMap<>();
        for (Path path : CandidatePaths()) {
            if (path == null || !Files.isRegularFile(path)) {
                continue;
            }
            try {
                values.putAll(Parse(Files.readString(path)));
                break;
            } catch (IOException ignored) {
                // Try the next candidate path.
            }
        }
        cachedFileValues = values;
        return cachedFileValues;
    }

    private static List<Path> CandidatePaths() {
        Path workingDirectory = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        return List.of(
                Path.of(".env"),
                Path.of("PersonalityQuest", ".env"),
                workingDirectory.resolve(".env"),
                workingDirectory.resolve("PersonalityQuest").resolve(".env"),
                workingDirectory.getParent() == null
                        ? null
                        : workingDirectory.getParent().resolve(".env"),
                workingDirectory.getParent() == null
                        ? null
                        : workingDirectory.getParent().resolve("PersonalityQuest").resolve(".env")
        );
    }

    private static String Unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
