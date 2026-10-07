package com.example.personalityquest.Services.quest;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.quest.JournalEntryDAO;
import com.example.personalityquest.Model.quest.JournalEntry;

import java.sql.SQLException;
import java.util.List;

public class JournalEntryService {

    private JournalEntryDAO JournalEntryDAO;

    public JournalEntryService(){
        JournalEntryDAO = new JournalEntryDAO();
    }

    public JournalEntryService(JournalEntryDAO journalEntryDAO){
        this.JournalEntryDAO = journalEntryDAO;
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
}
