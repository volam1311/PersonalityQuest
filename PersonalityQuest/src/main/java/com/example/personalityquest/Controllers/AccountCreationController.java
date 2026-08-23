package com.example.personalityquest.Controllers;

import com.example.personalityquest.AccountRepository;
import com.example.personalityquest.AccountSignUpValidator;
import com.example.personalityquest.Applications.AccountSignInApplication;
import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Managers.SystemManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AccountCreationController {

    @FXML
    private TextField emailEntry, userNameEntry, firstNameEntry,
            lastNameEntry, passwordEntry, reEnterPasswordEntry;

    @FXML
    private void OnSignUp() throws SQLException, IOException {
        String validationError = AccountSignUpValidator.validationError(
                emailEntry.getText(),
                userNameEntry.getText(),
                firstNameEntry.getText(),
                lastNameEntry.getText(),
                passwordEntry.getText(),
                reEnterPasswordEntry.getText());

        if (validationError != null) {
            System.out.println(validationError);
            System.out.println("Account creation Invalid");
            return;
        }

        AccountRepository.createAccount(
                SQLite.getConnection(),
                emailEntry.getText(),
                userNameEntry.getText(),
                firstNameEntry.getText(),
                lastNameEntry.getText(),
                passwordEntry.getText());

        DashboardApplication.launch((Stage)emailEntry.getScene().getWindow());

        /*
        * Set currently logged in account
        * */
        SystemManager.CurrentAccount.currentEmail = emailEntry.getText();

        System.out.println("Account created");
    }

    @FXML
    private void OnSignIn(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountSignInApplication.launch(currentStage);
    }
}
