package com.example.personalityquest;

import javafx.scene.text.Font;

import java.io.IOException;
import java.io.InputStream;

/** Loads fonts used by the application */
public final class AppFonts {
    private static final String[] FONT_FILES = {
            "/com/example/personalityquest/fonts/Poppins-Regular.ttf",
            "/com/example/personalityquest/fonts/Poppins-Bold.ttf",
            "/com/example/personalityquest/fonts/Poppins-Italic.ttf",
            "/com/example/personalityquest/fonts/Montserrat-Regular.ttf",
            "/com/example/personalityquest/fonts/Montserrat-Bold.ttf",
            "/com/example/personalityquest/fonts/MaterialSymbolsRounded.ttf"
    };

    private static boolean loaded;

    private AppFonts() {
    }

    /** Registers the bundled fonts with JavaFX */
    public static void load() {
        if (loaded) {
            return;
        }

        for (String fontFile : FONT_FILES) {
            try (InputStream stream = AppFonts.class.getResourceAsStream(fontFile)) {
                if (stream == null) {
                    System.err.println("Font resource not found: " + fontFile);
                    continue;
                }
                Font.loadFont(stream, 12);
            } catch (IOException exception) {
                System.err.println("Failed to load font: " + fontFile);
            }
        }

        loaded = true;
    }
}
