package com.example.personalityquest.Controllers.auth;

import com.example.personalityquest.Services.auth.EmailService;
import com.example.personalityquest.Services.auth.PasswordService;
import com.example.personalityquest.ApplicationManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Objects;


/** Controls account password updates */
public class UpdateAccountPasswordController {

    private final static String updatePasswordSql = "";
    @FXML
    private TextField currentPasswordEntry, newPasswordEntry, newPasswordReEntry;

    @FXML
    private Label Message;

    @FXML
    private void OnUpdatePassword() throws SQLException {
        // new password does not match with reentry
        if (!Objects.equals(newPasswordEntry.getText(), newPasswordReEntry.getText())){
            Message.setText("New Password does not match Re-Enter field");
            return;
        }

        PasswordService.UpdatePasswordForEmail(ApplicationManager.CurrentAccount.getCurrentEmail(), currentPasswordEntry.getText(), newPasswordEntry.getText());
        Message.setText("Password Updated for account");

        OnExit();
    }

    @FXML
    private void OnExit(){

    }
}
