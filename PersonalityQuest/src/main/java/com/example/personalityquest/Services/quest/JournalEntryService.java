package com.example.personalityquest.Services.quest;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.quest.JournalEntryDAO;
import com.example.personalityquest.Model.quest.JournalEntry;

import java.sql.SQLException;
import java.util.List;

public class JournalEntryService {

    public static JournalEntry AddChallengeEntry(String email, int labourId, String title, String body) throws SQLException{
        if (ApplicationManager.isEmpty(email)) {
            throw new IllegalArgumentException("Email is Empty");
        }
        return JournalEntryDAO.Upsert(email, labourId, JournalEntry.EntryType.CHALLENGE, title, body);
    }

    public static List<JournalEntry> GetChallengesForEmail(String email) throws SQLException{
        if (ApplicationManager.isEmpty(email)) {
            throw new IllegalArgumentException("Email is Empty");
        }
        return JournalEntryDAO.GetForEmailAndType(email, JournalEntry.EntryType.CHALLENGE);
    }

    public static boolean HasAddedChallengeForLabour(String email, int labourId) throws SQLException{
        if (ApplicationManager.isEmpty(email)) {
            throw new IllegalArgumentException("Email is Empty");
        }
        return JournalEntryDAO.ExistsForLabourAndType(email, labourId, JournalEntry.EntryType.CHALLENGE);
    }
}
