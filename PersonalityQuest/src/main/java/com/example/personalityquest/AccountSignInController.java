package com.example.personalityquest;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.classfile.Label;

public class AccountSignInController {
    @FXML
    private Label signUpLabel;

    @FXML
    private void OnSignUp(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountCreationApplication.launch(currentStage);
    }
}
