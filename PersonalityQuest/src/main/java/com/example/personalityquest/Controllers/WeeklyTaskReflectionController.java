package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.DataClasses.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class WeeklyTaskReflectionController {
    @FXML
    private TextField reflection;

    private Task currentTask;

    public void setTask(Task task){
        this.currentTask = task;
        DisplayTask();
    }

    // Right How you want to display the Task info as this needs to be called
    // due to initialize running before the Task will be set
    private void DisplayTask(){

    }
    @FXML
    private void onBackClick() throws IOException {
        // nothing is saved and you go back to dashboard
        GoToDashboard();
    }

    @FXML
    private void onSaveDraft() throws IOException {
        // saves the current reflection progress to come back to later
        GoToDashboard();
    }

    @FXML
    private void onSubmitClick() throws IOException {
        // saves the current reflection progress and marks it as complete
        GoToDashboard();
    }

    private void GoToDashboard() throws IOException {
        DashboardApplication.launch((Stage)reflection.getScene().getWindow());
    }

}
