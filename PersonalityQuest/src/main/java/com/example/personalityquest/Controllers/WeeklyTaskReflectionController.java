package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.Model.WeeklyTask;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Services.WeeklyTaskService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class WeeklyTaskReflectionController implements Initializable {
    @FXML
    private NavBarController navBarController;
    @FXML
    private TextArea reflection;

    private WeeklyTask currentTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.TASKS);
    }

    public void setTask(WeeklyTask task){
        this.currentTask = task;


        DisplayTask();
    }

    // Right How you want to display the Task info as this needs to be called
    // due to initialize running before the Task will be set
    private void DisplayTask(){
        System.out.println("Current Reflection: " + currentTask.getReflection());
        reflection.setText(currentTask.getReflection());
    }
    @FXML
    private void onBackClick() throws IOException {
        // nothing is saved and you go back to dashboard
        GoToDashboard();
    }

    @FXML
    private void onSaveDraft() throws Exception {
        // saves the current reflection progress to come back to later


        System.out.println(reflection.getText());
        WeeklyTaskService.UpdateGivenTaskToDraft(currentTask, reflection.getText(), ApplicationManager.CurrentAccount.getCurrentEmail());
        GoToDashboard();
    }

    @FXML
    private void onSubmitClick() throws Exception {
        // saves the current reflection progress and marks it as complete

        // Use this to mark a task as Finished
        System.out.println(reflection.getText());
        WeeklyTaskService.UpdateGivenTaskToBeFinished(currentTask, reflection.getText(), ApplicationManager.CurrentAccount.getCurrentEmail());

        GoToDashboard();
    }

    private void GoToDashboard() throws IOException {
        DashboardApplication.launch((Stage)reflection.getScene().getWindow());
    }

}
