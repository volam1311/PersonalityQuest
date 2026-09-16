package com.example.personalityquest.Services.profile;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.profile.FriendsDAO;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Services.auth.EmailService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsService {
    /**
     * Gets the List of details for a friend
     * @param accountEmail the account you want to get the friends for
     * @return The List of Details for your friends
     * @throws Exception Database Access Failure
     */
    public static List<EmailDetails> GetFriendsForAccount(String accountEmail) throws Exception {
        return FriendsDAO.GetFriendsForEmail(accountEmail);
    }

    /**
     * Adds a friend to your account
     * @param accountEmail your accounts email
     * @param friendsEmail your friends email
     * @throws Exception Database Update Failure
     */
    public static void AddFriendForAccount(String accountEmail, String friendsEmail) throws Exception {
        if (EmailService.GetDetailsForEmail(friendsEmail) == null){
            throw new IllegalArgumentException("Friend with email" + friendsEmail + " Doesnt exist");
        }
        FriendsDAO.AddFriend(accountEmail, friendsEmail);
    }

    /**
     * Removes a friend from your account
     * @param accountEmail your accounts email
     * @param friendsEmail the email of the friend you want to remove
     * @throws Exception Database Update Failure
     */
    public static void RemoveFriend(String accountEmail, String friendsEmail) throws Exception {
        if (EmailService.GetDetailsForEmail(friendsEmail) == null){
            throw new IllegalArgumentException("Friend with email" + friendsEmail + " Doesnt exist");
        }

        FriendsDAO.RemoveFriend(accountEmail, friendsEmail);
    }
}
