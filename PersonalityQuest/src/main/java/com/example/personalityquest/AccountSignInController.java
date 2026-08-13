package com.example.personalityquest;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.classfile.Label;

public class AccountSignInController {
    @FXML
    private TextField emailEntry, passwordEntry;

    @FXML
    private void OnSignIn() {
        System.out.println(emailEntry.getText() + passwordEntry.getText());
    }

    @FXML
    private void OnSignUp(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountCreationApplication.launch(currentStage);
    }
}
