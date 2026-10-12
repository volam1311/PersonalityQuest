package com.example.personalityquest.Services.profile;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.auth.AccountDAO;
import com.example.personalityquest.DAO.profile.FriendsDAO;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Services.auth.AccountService;
import com.example.personalityquest.Services.auth.EmailService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Coordinates friendship operations between accounts */
public class FriendsService {

    private FriendsDAO FriendsDAO;
    private EmailService EmailService;

    public FriendsService(){
        super();
        this.FriendsDAO = new FriendsDAO();
        EmailService = new EmailService();

    }

    public FriendsService(FriendsDAO friendsDAO){
        super();
        this.FriendsDAO = friendsDAO;

        AccountDAO accountDAO = new AccountDAO(FriendsDAO.getConnection());
        EmailService = new EmailService(accountDAO);
    }

    /**
     * Gets the List of details for a friend
     * @param accountEmail the account you want to get the friends for
     * @return The List of Details for your friends
     * @throws Exception Database Access Failure
     */
    public List<EmailDetails> GetFriendsForAccount(String accountEmail) throws Exception {
        return FriendsDAO.GetFriendsForEmail(accountEmail);
    }

    /**
     * Adds a friend to your account
     * @param accountEmail your accounts email
     * @param friendsEmail your friends email
     * @throws Exception Database Update Failure
     */
    public void AddFriendForAccount(String accountEmail, String friendsEmail) throws Exception {
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
    public void RemoveFriend(String accountEmail, String friendsEmail) throws Exception {
        if (EmailService.GetDetailsForEmail(friendsEmail) == null){
            throw new IllegalArgumentException("Friend with email" + friendsEmail + " Doesnt exist");
        }

        FriendsDAO.RemoveFriend(accountEmail, friendsEmail);
    }
}
