package com.example.personalityquest.Services.auth;

import com.example.personalityquest.Model.auth.LoginCache;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.ParentService;
import com.example.personalityquest.Services.navigation.NavigationService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.prefs.Preferences;

public class LoginCacheService extends ParentService {


    public Preferences preferences;

    public LoginCacheService(){
        super();
        preferences = Preferences.userRoot();
    }

    public LoginCacheService(Preferences preferences){
        super();
        this.preferences = preferences;
    }

    private final static int days = 7;


    /**
     * Gets the current Login Caches details
     * @return A Login Cache object containing an email and the last time the user logged in
     */
    public LoginCache GetLoginCache(){
        return new LoginCache(
                preferences.get("LoginEmail", ""),
                LocalDate.parse(preferences.get("TimeOfLogin", LocalDate.now().toString()))
        );
    }

    /**
     * Updates the Login Cache with the new email and current date time
     * @param email The email you want to set the login cache to
     */
    public void UpdateCache(String email){
        preferences.put("LoginEmail", email);
        preferences.put("TimeOfLogin", LocalDate.now().toString());
    }

    /**
     * Clears the current login data from the cache
     */
    public void ClearCache(){
        preferences.put("LoginEmail", "");
        preferences.put("TimeOfLogin", LocalDate.now().toString());
    }

    /**
     * Checks to see if the given parsed date is past the allowed cache time. Clears the cache if it is past expired time.
     * @return Whether the login cache is now expired
     */
    public boolean IsPastTimeLimit(LocalDate parsedDate){
        if (ChronoUnit.DAYS.between(parsedDate, LocalDate.now()) > days){
            ClearCache();
            return true;
        }

        return false;
    }
}
