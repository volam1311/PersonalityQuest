package com.example.personalityquest.Applications;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.MainApplication.fxmlPrefix;

public class AccountCreationApplication {
    public static void launch(Stage stage) throws IOException {
        try {
            loadFonts();
            FXMLLoader fxmlLoader = new FXMLLoader(AccountCreationApplication.class.getResource(fxmlPrefix + "AccountCreation.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1100, 720);
            stage.setTitle("Account Creation");
            stage.setMinWidth(420);
            stage.setMinHeight(520);
            stage.setScene(scene);
            stage.setWidth(1100);
            stage.setHeight(720);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load Account Creation page");
            e.printStackTrace();
            throw e;
        }
    }

    private static void loadFonts() {
        String[] fontFiles = {
                "/com/example/personalityquest/fonts/Poppins-Regular.ttf",
                "/com/example/personalityquest/fonts/Poppins-Bold.ttf",
                "/com/example/personalityquest/fonts/Poppins-Italic.ttf",
                "/com/example/personalityquest/fonts/Montserrat-Regular.ttf",
                "/com/example/personalityquest/fonts/Montserrat-Bold.ttf"
        };
        for (String fontFile : fontFiles) {
            var stream = AccountCreationApplication.class.getResourceAsStream(fontFile);
            if (stream == null) {
                System.err.println("Font resource not found: " + fontFile);
                continue;
            }
            Font.loadFont(stream, 12);
        }
    }
}
