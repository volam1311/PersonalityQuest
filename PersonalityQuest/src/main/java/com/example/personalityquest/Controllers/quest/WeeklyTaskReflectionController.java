package com.example.personalityquest.Controllers.quest;

import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.Applications.navigation.DashboardApplication;
import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.quest.WeeklyTaskService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Controls the weekly-reflection form */
public class WeeklyTaskReflectionController implements Initializable {
    @FXML
    private NavBarController navBarController;
    @FXML
    private TextArea reflection;

    private WeeklyTask currentTask;

    /** Initialises the weekly-reflection form
     * @param location the location used to resolve relative paths
     * @param resources the localisation resources for the screen
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.TASKS);
    }

    /** Sets the task displayed by the reflection form
     * @param task the weekly task to display
     */
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
        NavigationService.LoadScreen(ScreenEnum.DASHBOARD);
    }

}
