package com.example.personalityquest.Controllers;

import com.example.personalityquest.AccountSignUpValidator;
import com.example.personalityquest.Managers.EmailManager;
import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.PasswordManager;
import com.example.personalityquest.Managers.SystemManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Objects;


public class UpdateAccountPasswordController {

    private final static String updatePasswordSql = "";
    @FXML
    private TextField currentPasswordEntry, newPasswordEntry, newPasswordReEntry;

    @FXML
    private Label Message;

    @FXML
    private void OnUpdatePassword() throws SQLException {
        // email doesnt exist
        if (!EmailManager.DoesAccountWithEmailExist(SystemManager.CurrentAccount.currentEmail)){
            Message.setText("Wow you reached something you should not have.");
            return;
        }

        // current password entered does not match account
        if (!PasswordManager.isPasswordForEmail(SystemManager.CurrentAccount.currentEmail, currentPasswordEntry.getText())){
            Message.setText("Current Password does not match account");
            return;
        }

        // new password does not match with rentry
        if (!Objects.equals(newPasswordEntry.getText(), newPasswordReEntry.getText())){
            Message.setText("New Password does not match Re-Enter field");
            return;
        }

        PasswordManager.UpdatePasswordForEmail(SystemManager.CurrentAccount.currentEmail, newPasswordEntry.getText());
        Message.setText("Password Updated for account");

        OnExit();
    }

    @FXML
    private void OnExit(){

    }
}
