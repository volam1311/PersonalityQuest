package com.example.personalityquest;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountCreationController {

    private final String sqlSignUp = "INSERT INTO Accounts (email, userName, firstName, lastName, password) VALUES(?, ?, ?, ?, ?)";

    @FXML
    private TextField emailEntry, userNameEntry, firstNameEntry,
            lastNameEntry, passwordEntry, reEnterPasswordEntry;

    @FXML
    private void OnSignUp(){
        System.out.println(emailEntry.getText() + userNameEntry.getText() +
                firstNameEntry.getText() + lastNameEntry.getText() +
                passwordEntry.getText());
    }

    @FXML
    private void OnSignIn(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountSignInApplication.launch(currentStage);
    }
}
