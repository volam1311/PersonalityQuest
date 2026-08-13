package com.example.personalityquest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountSignInApplication {
    public static void launch(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AccountCreationApplication.class.getResource("AccountSignIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 620);
        stage.setTitle("Account Login");
        stage.setScene(scene);
        stage.show();
    }
}
