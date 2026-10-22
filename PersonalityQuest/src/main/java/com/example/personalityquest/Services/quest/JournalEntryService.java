package com.example.personalityquest.Services.quest;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.quest.JournalEntryDAO;
import com.example.personalityquest.Model.quest.JournalEntry;
import com.example.personalityquest.Services.quiz.QuizService;

import java.sql.SQLException;
import java.util.List;

/** Saves and loads journal entries for the signed-in account. */
public class JournalEntryService {

    private JournalEntryDAO JournalEntryDAO;

    public JournalEntryService(){
        super();
        JournalEntryDAO = new JournalEntryDAO();
    }

    public JournalEntryService(JournalEntryDAO journalEntryDAO){
        super();
        this.JournalEntryDAO = journalEntryDAO;
    }

    private static JournalEntryService instance;
    public static JournalEntryService GetInstance(){
        if (instance == null){
            instance = new JournalEntryService();
        }

        return instance;
    }


    public JournalEntry AddChallengeEntry(String email, int labourId, String title, String body) throws SQLException{
        if (ApplicationManager.isEmpty(email)) {
            throw new IllegalArgumentException("Email is Empty");
        }
        return JournalEntryDAO.Upsert(email, labourId, JournalEntry.EntryType.CHALLENGE, title, body);
    }

    public List<JournalEntry> GetChallengesForEmail(String email) throws SQLException{
        if (ApplicationManager.isEmpty(email)) {
            throw new IllegalArgumentException("Email is Empty");
        }
        return JournalEntryDAO.GetForEmailAndType(email, JournalEntry.EntryType.CHALLENGE);
    }

    public boolean HasAddedChallengeForLabour(String email, int labourId) throws SQLException{
        if (ApplicationManager.isEmpty(email)) {
            throw new IllegalArgumentException("Email is Empty");
        }
        return JournalEntryDAO.ExistsForLabourAndType(email, labourId, JournalEntry.EntryType.CHALLENGE);
    }

    /** Inserts a personal reflection that is not attached to a quest. */
    public JournalEntry AddStandaloneEntry(String email, String title, String body) throws SQLException {
        if (ApplicationManager.isEmpty(email) || IsBlank(body)) {
            throw new IllegalArgumentException("Email or reflection is empty");
        }
        return JournalEntryDAO.Insert(email, null, JournalEntry.EntryType.STANDALONE, StandaloneTitle(title), body.trim());
    }

    /** Replaces the title and body of a personal reflection owned by this account. */
    public JournalEntry UpdateStandaloneEntry(String email, int id, String title, String body) throws SQLException {
        if (ApplicationManager.isEmpty(email) || IsBlank(body)) {
            throw new IllegalArgumentException("Email or reflection is empty");
        }
        JournalEntry updated = JournalEntryDAO.UpdateStandalone(email, id, StandaloneTitle(title), body.trim());
        if (updated == null) {
            throw new IllegalArgumentException("Standalone reflection was not found");
        }
        return updated;
    }

    /** Loads personal reflections for this account, newest first. */
    public List<JournalEntry> GetStandaloneForEmail(String email) throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            throw new IllegalArgumentException("Email is Empty");
        }
        return JournalEntryDAO.GetForEmailAndType(email, JournalEntry.EntryType.STANDALONE);
    }

    /** Stores AI feedback against a personal reflection. Empty feedback is ignored. */
    public void SaveStandaloneFeedback(String email, int id, String feedback) throws SQLException {
        if (ApplicationManager.isEmpty(email) || IsBlank(feedback)) {
            return;
        }
        JournalEntryDAO.UpdateAiFeedback(email, id, feedback.trim());
    }

    private static String StandaloneTitle(String title) {
        return IsBlank(title) ? "Personal reflection" : title.trim();
    }

    private static boolean IsBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
