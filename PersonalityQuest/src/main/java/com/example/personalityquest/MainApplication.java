package com.example.personalityquest;

import com.example.personalityquest.Applications.auth.AccountCreationApplication;
import com.example.personalityquest.Applications.auth.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.DAO.auth.UserDAO;
import com.example.personalityquest.DAO.personalisation.ArchetypeDAO;
import com.example.personalityquest.DAO.quest.QuestDAO;
import com.example.personalityquest.DAO.quest.UserQuestDAO;
import com.example.personalityquest.Services.auth.AccountService;
import com.example.personalityquest.Services.auth.EmailService;
import com.example.personalityquest.Services.auth.HashingService;
import com.example.personalityquest.Services.auth.PasswordService;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.prefs.Preferences;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException{
        AppFonts.load();

        // Runs once, before any other DB call has a chance to leave an open
        // statement on the shared connection. Doing this here (rather than on
        // every visit to the Archetype screen) avoids SQLITE_LOCKED errors caused
        // by other DAOs not closing their statements/result sets.
        try {
            ArchetypeDAO.ResetAndSeedCatalog();
            QuestDAO.ResetAndSeedCatalog();
            initialiseUserQuestData();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            NavigationService.Init(stage);

            LocalDate now = LocalDate.now();

            String dateNow = Preferences.userRoot().get("TimeOfLogin", now.toString());
            String loginEmail = Preferences.userRoot().get("LoginEmail", "");

            // do we have login details
            if (!loginEmail.isEmpty()){
                try{
                    AttemptLogin(loginEmail, dateNow);
                    return;
                }
                catch (Exception e) {
                   System.out.println(e.getMessage());
                }
            }
            NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    static void initialiseUserQuestData() throws Exception{
        UserQuestDAO.EnsureTables();
    }

    static void AttemptLogin(String email, String dateNow) throws Exception {
        LocalDate parsedDate = LocalDate.parse(dateNow);

        if (ChronoUnit.DAYS.between(parsedDate, LocalDate.now()) > 7){
            Preferences.userRoot().put("LoginEmail", "");
            Preferences.userRoot().get("TimeOfLogin", LocalDate.now().toString());
            NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
            return;
        }


        if (!EmailService.DoesAccountWithEmailExist(email)){
            throw new Exception("Account does not exist with this email");
        }

        ApplicationManager.CurrentAccount.setCurrentEmail(email);
        NavigationService.LoadScreen(ScreenEnum.DASHBOARD);
    }
}
