package com.example.personalityquest.Services.auth;

import com.example.personalityquest.Model.auth.LoginCache;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.prefs.Preferences;

public class LoginCacheService {

    private final static int days = 7;


    /**
     * Gets the current Login Caches details
     * @return A Login Cache object containing an email and the last time the user logged in
     */
    public static LoginCache GetLoginCache(){
        return new LoginCache(
                Preferences.userRoot().get("LoginEmail", ""),
                LocalDate.parse(Preferences.userRoot().get("TimeOfLogin", LocalDate.now().toString()))
        );
    }

    /**
     * Updates the Login Cache with the new email and current date time
     * @param email The email you want to set the login cache to
     */
    public static void UpdateCache(String email){
        Preferences.userRoot().put("LoginEmail", email);
        Preferences.userRoot().put("TimeOfLogin", LocalDate.now().toString());
    }

    /**
     * Clears the current login data from the cache
     */
    public static void ClearCache(){
        Preferences.userRoot().put("LoginEmail", "");
        Preferences.userRoot().put("TimeOfLogin", LocalDate.now().toString());
    }

    /**
     * Checks to see if the given parsed date is past the allowed cache time. Clears the cache if it is past expired time.
     * @return Whether the login cache is now expired
     */
    public static boolean IsPastTimeLimit(LocalDate parsedDate){
        if (ChronoUnit.DAYS.between(parsedDate, LocalDate.now()) > days){
            ClearCache();
            return true;
        }

        return false;
    }
}
