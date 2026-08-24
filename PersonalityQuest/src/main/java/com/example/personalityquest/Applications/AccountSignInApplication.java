package com.example.personalityquest.Applications;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;

import static com.example.personalityquest.MainApplication.fxmlPrefix;
public class AccountSignInApplication {
    public static void launch(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AccountCreationApplication.class.getResource(fxmlPrefix +  "AccountSignIn.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1100, 720);
            stage.setTitle("Account Login");
            stage.setMinWidth(420);
            stage.setMinHeight(480);
            stage.setScene(scene);
            stage.setWidth(1100);
            stage.setHeight(720);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load Account Sign In page");
            e.printStackTrace();
            throw e;
        }
    }
}
