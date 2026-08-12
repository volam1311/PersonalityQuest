package com.example.personalityquest;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AccountCreationController {

    private final String sqlSignUp = "INSERT INTO Accounts (email, userName, firstName, lastName, password) VALUES(?, ?, ?, ?, ?)";
    @FXML
    private TextField emailEntry;
    @FXML
    private TextField userNameEntry;
    @FXML
    private TextField firstNameEntry;
    @FXML
    private TextField lastNameEntry;
    @FXML
    private TextField passwordEntry;
    @FXML
    private TextField reEnterPasswordEntry;

    @FXML
    private void OnSignUp(){
        System.out.println(emailEntry.getText() + userNameEntry.getText() +
                firstNameEntry.getText() + lastNameEntry.getText() +
                passwordEntry.getText());
    }
}
