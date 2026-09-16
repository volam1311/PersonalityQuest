package com.example.personalityquest.Services.profile;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.profile.FriendsDAO;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Services.auth.EmailService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendsService {
    public static List<EmailDetails> GetFriendsForAccount(String accountEmail) throws Exception {
        return FriendsDAO.GetFriendsForEmail(accountEmail);
    }

    public static void AddFriendForAccount(String accountEmail, String friendsEmail) throws Exception {
        if (EmailService.GetDetailsForEmail(friendsEmail) == null){
            throw new IllegalArgumentException("Friend with email" + friendsEmail + " Doesnt exist");
        }
        FriendsDAO.AddFriend(accountEmail, friendsEmail);
    }

    public static void RemoveFriend(String accountEmail, String friendsEmail) throws Exception {
        if (EmailService.GetDetailsForEmail(friendsEmail) == null){
            throw new IllegalArgumentException("Friend with email" + friendsEmail + " Doesnt exist");
        }

        FriendsDAO.RemoveFriend(accountEmail, friendsEmail);
    }
}
