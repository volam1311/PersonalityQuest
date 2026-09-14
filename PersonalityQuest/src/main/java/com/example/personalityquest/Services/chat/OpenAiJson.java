package com.example.personalityquest.Services.chat;

/**
 * Minimal JSON helpers for OpenAI request bodies and chat-completion responses.
 */
public final class OpenAiJson {
    private OpenAiJson() {
    }

    /**
     * Escapes a string so it can be placed inside a JSON value.
     */
    public static String Escape(String value) {
        if (value == null) {
            return "";
        }

        StringBuilder escaped = new StringBuilder(value.length());
        for (int index = 0; index < value.length(); index++) {
            char character = value.charAt(index);
            switch (character) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> {
                    if (character < 0x20) {
                        escaped.append(String.format("\\u%04x", (int) character));
                    } else {
                        escaped.append(character);
                    }
                }
            }
        }
        return escaped.toString();
    }

    /**
     * Reads the first JSON string for a key, starting after {@code afterKey} when that key exists.
     */
    public static String ExtractString(String json, String key, String afterKey) {
        if (json == null || key == null) {
            return "";
        }

        int searchFrom = 0;
        if (afterKey != null && !afterKey.isEmpty()) {
            int afterIndex = json.indexOf("\"" + afterKey + "\"");
            if (afterIndex >= 0) {
                searchFrom = afterIndex;
            }
        }

        String needle = "\"" + key + "\"";
        int keyIndex = json.indexOf(needle, searchFrom);
        if (keyIndex < 0) {
            return "";
        }

        int colon = json.indexOf(':', keyIndex + needle.length());
        if (colon < 0) {
            return "";
        }

        int firstQuote = json.indexOf('"', colon + 1);
        if (firstQuote < 0) {
            return "";
        }

        StringBuilder value = new StringBuilder();
        boolean escaped = false;
        for (int index = firstQuote + 1; index < json.length(); index++) {
            char character = json.charAt(index);
            if (escaped) {
                switch (character) {
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case '"' -> value.append('"');
                    case '\\' -> value.append('\\');
                    case '/' -> value.append('/');
                    case 'u' -> {
                        if (index + 4 < json.length()) {
                            String hex = json.substring(index + 1, index + 5);
                            value.append((char) Integer.parseInt(hex, 16));
                            index += 4;
                        }
                    }
                    default -> value.append(character);
                }
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '"') {
                return value.toString();
            } else {
                value.append(character);
            }
        }
        return "";
    }
}
