package com.example.personalityquest;

import com.example.personalityquest.Applications.auth.AccountCreationApplication;
import com.example.personalityquest.Applications.auth.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.DAO.auth.UserDAO;
import com.example.personalityquest.DAO.personalisation.ArchetypeDAO;
import com.example.personalityquest.DAO.quest.*;
import com.example.personalityquest.Model.auth.LoginCache;
import com.example.personalityquest.Services.auth.*;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.quest.QuestService;
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

/** Starts the JavaFX application and opens its initial screen */
public class MainApplication extends Application {

    private final LoginCacheService loginCacheService = new LoginCacheService();
    /** Initialises application resources and displays the account screen
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException{
        AppFonts.load();

        QuestDAO QuestDAO = new QuestDAO();
        TaskDAO TaskDAO = new TaskDAO();
        JournalEntryDAO JournalEntryDAO = new JournalEntryDAO();
        // Runs once, before any other DB call has a chance to leave an open
        // statement on the shared connection. Doing this here (rather than on
        // every visit to the Archetype screen) avoids SQLITE_LOCKED errors caused
        // by other DAOs not closing their statements/result sets.
        try {
            ArchetypeDAO.ResetAndSeedCatalog();
            QuestDAO.ResetAndSeedCatalog();
            QuestOptionDAO.ResetAndSeedCatalog();
            ReflectionPromptDAO.ResetAndSeedCatalog();
            initialiseUserQuestData();
            TaskDAO.EnsureTables();
            TaskDAO.PopulateChallenges();
            JournalEntryDAO.EnsureTables();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            NavigationService.Init(stage);

            LocalDate now = LocalDate.now();

            LoginCache loginCache = loginCacheService.GetLoginCache();

            // do we have login details
            if (loginCache.GetEmail().isEmpty()){
                NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
                return;
            }

            try{
                AttemptLogin(loginCache);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    static void initialiseUserQuestData() throws Exception{
        UserQuestDAO userQuestDAO = new UserQuestDAO();
        userQuestDAO.EnsureTables();
    }

    void AttemptLogin(LoginCache loginCache) throws Exception {

        if (loginCacheService.IsPastTimeLimit(loginCache.GetLastLoginDate())){
            throw new Exception("Login Cache Expired");
        }

        EmailService emailService = new EmailService();
        if (!emailService.DoesAccountWithEmailExist(loginCache.GetEmail())){
            throw new Exception("Account does not exist with this email");
        }

        ApplicationManager.CurrentAccount.setCurrentEmail(loginCache.GetEmail());
        NavigationService.LoadScreen(ScreenEnum.DASHBOARD);
    }
}
