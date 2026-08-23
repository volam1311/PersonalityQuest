package com.example.personalityquest.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class UpdateAccountPasswordController {

    private final static String updatePasswordSql = "";
    @FXML
    private TextField currentPasswordEntry, newPasswordEntry, newPasswordReEntry;

    @FXML
    private Label Message;

    @FXML
    private void OnUpdatePassword(){
        Message.setText("Password Valid");
    }

    @FXML
    private void OnExit(){

    }
}
