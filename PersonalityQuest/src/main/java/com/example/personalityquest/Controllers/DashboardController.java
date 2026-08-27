package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.WeeklyTaskReflectionApplication;
import com.example.personalityquest.DataClasses.EmailDetails;
import com.example.personalityquest.DataClasses.WeeklyTask;
import com.example.personalityquest.Managers.EmailManager;
import com.example.personalityquest.Managers.SystemManager;
import com.example.personalityquest.Managers.WeeklyTaskManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Label welcomeMessage;
    @FXML
    private ListView<WeeklyTask> weeklyTasks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate the given fxml with the users weekly tasks,
        // their current questline and their progress graph
        try {
            SetWelcome();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PopulateQuestline();
        try {
            PopulateWeeklyTasks();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        PopulateProgressGraph();
    }

    /*
     * Sets the welcome name
     * */
    private void SetWelcome() throws Exception {
        EmailDetails emailDetails = EmailManager.GetDetailsForEmail(SystemManager.CurrentAccount.currentEmail);
        welcomeMessage.setText("Welcome back, " + emailDetails.getFirstName());
        System.out.println("Set Welcome Name");
    }

    /*
    * Populates the Questline fxml with the users data
    * */
    private void PopulateQuestline(){
        System.out.println("Populated Questline");
    }

    /*
     * Populates the Weekly tasks fxml with the users data
     * */
    private void PopulateWeeklyTasks() throws Exception {
        // Need Weekly Quests Before I can do this so this is just dummy data
        WeeklyTask[] tasks = WeeklyTaskManager.GetTasksForEmailForThisWeek(SystemManager.CurrentAccount.currentEmail);

        // first time this week logging in so Generate Tasks
        if (Objects.equals(tasks, null)){
            System.out.println("No tasks this week so generating some");
            tasks = WeeklyTaskManager.GenerateTasksForThisWeek(SystemManager.CurrentAccount.currentEmail);
        }


        for (int i = 0; i < tasks.length; i++){
            weeklyTasks.getItems().add(i, tasks[i]);
        }

        weeklyTasks.setMinHeight(weeklyTasks.getItems().size() * 24);
        System.out.println("Populated Weekly Tasks");
    }

    /*
     * Populates the graph fxml with the users data
     * */
    private void PopulateProgressGraph(){
        System.out.println("Populated Progress Graph");
    }
    @FXML
    private void OnTaskClick(MouseEvent event) throws IOException {
        WeeklyTask selectedTask = weeklyTasks.getSelectionModel().getSelectedItem();
        System.out.println("Selected Task Reflection: " + selectedTask.getReflection());
        if (selectedTask != null) {
            WeeklyTaskReflectionApplication.launch((Stage)weeklyTasks.getScene().getWindow(), selectedTask);
        }
    }
}
