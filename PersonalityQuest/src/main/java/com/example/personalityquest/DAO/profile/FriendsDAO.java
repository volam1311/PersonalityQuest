package com.example.personalityquest.DAO.profile;

import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Services.auth.EmailService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Stores and retrieves account friendship relationships */
public class FriendsDAO {
    public static final String GET_FRIENDS = """
        SELECT friendsEmail
        FROM Friends
        WHERE accountEmail = ?
    """;

    public static final String INSERT_FRIEND = """
        INSERT INTO FRIENDS(accountEmail, friendsEmail)
        VALUES (?, ?)
    """;

    public static final String REMOVE_FRIEND = """
        DELETE FROM Friends
        WHERE accountEmail = ?
        AND friendsEmail = ?
    """;
    /** Returns account details for the friends of the supplied account
     * @param email the account email whose friends should be loaded
     * @return the friend's account details
     * @throws Exception if the friend records cannot be loaded
     */
    public static List<EmailDetails> GetFriendsForEmail(String email) throws Exception {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_FRIENDS);
        statement.setString(1, email);

        ResultSet rs = statement.executeQuery();
        List<EmailDetails> detailsForFriends = new ArrayList<>();
        while (rs.next()){
            EmailDetails details = EmailService.GetDetailsForEmail(rs.getString(1));
            detailsForFriends.add(details);
        }

        return detailsForFriends;
    }

    /** Adds a friendship between two accounts
     * @param yourEmail the email address of the account adding the friend
     * @param friendsEmail the email address of the account to add
     * @throws SQLException if the friendship cannot be stored
     */
    public static void AddFriend(String yourEmail, String friendsEmail) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_FRIEND);
        statement.setString(1, yourEmail);
        statement.setString(2, friendsEmail);

        statement.executeUpdate();
    }

    /** Removes a friendship between two accounts
     * @param yourEmail the email address of the account removing the friend
     * @param friendsEmail the email address of the friend to remove
     * @throws SQLException if the friendship cannot be removed
     */
    public static void RemoveFriend(String yourEmail, String friendsEmail) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(REMOVE_FRIEND);
        statement.setString(1, yourEmail);
        statement.setString(2, friendsEmail);

        statement.executeUpdate();
    }


}
