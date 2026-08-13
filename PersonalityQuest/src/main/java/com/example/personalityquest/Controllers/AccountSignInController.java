package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.AccountCreationApplication;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class AccountSignInController {
    @FXML
    private TextField emailEntry, passwordEntry;

    @FXML
    private void OnSignIn() {
        if (!isValidSignIn()) {
            System.out.println("Account Sign In Invalid");
            return;
        }
        System.out.println(emailEntry.getText() + passwordEntry.getText());
    }

    private boolean isValidSignIn(){
        return true;
    }
    @FXML
    private void OnSignUp(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountCreationApplication.launch(currentStage);
    }
}
