package com.example.personalityquest.Model.quest;

public class JournalEntry {
    public enum EntryType{CHALLENGE, REFLECTION, STANDALONE}

    private final int id;
    private final String accountEmail;
    public final Integer labourId;
    private final EntryType entryType;
    private final String title;
    private final String body;
    private final String aiFeedback;
    private final String createdAt;

    public JournalEntry(int id, String accountEmail, Integer labourId, EntryType entryType,
                        String title, String body, String aiFeedback, String createdAt) {
        this.id = id;
        this.accountEmail = accountEmail;
        this.labourId = labourId;
        this.entryType = entryType;
        this.title = title;
        this.body = body;
        this.aiFeedback = aiFeedback;
        this.createdAt = createdAt;
    }

    public int getId() {return id;}
    public String getAccountEmail() {return accountEmail;}
    public Integer getLabourId() {return labourId;}
    public EntryType getEntryType() {return entryType;}
    public String getTitle() {return title;}
    public String getBody() {return body;}
    public String getAiFeedback() {return aiFeedback;}
    public String getCreatedAt() {return createdAt;}


}
