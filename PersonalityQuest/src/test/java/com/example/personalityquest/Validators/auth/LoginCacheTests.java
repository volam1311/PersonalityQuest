package com.example.personalityquest.Validators.auth;

import com.example.personalityquest.Model.auth.LoginCache;
import com.example.personalityquest.Services.auth.LoginCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;

public class LoginCacheTests {

    LoginCacheService loginCacheService;

    String email;
    LocalDate now;
    @BeforeEach
    void setUp(){
        loginCacheService = new LoginCacheService(Preferences.userRoot().node("Test"));
        email = "TestEmail";
        now = LocalDate.now();

        loginCacheService.UpdateCache("TestEmail");

    }

    @Test
    public void GetDetailsTest(){
        LoginCache loginCache = loginCacheService.GetLoginCache();

        assertEquals(email, loginCache.GetEmail());
        assertEquals(now, loginCache.GetLastLoginDate());
    }

    @Test
    public void ClearCache(){
        loginCacheService.ClearCache();
        LoginCache loginCache = loginCacheService.GetLoginCache();
        assertEquals("", loginCache.GetEmail());
    }

    @Test
    public void UpdateCacheDetails(){
        loginCacheService.UpdateCache("NewEmail");
        LoginCache loginCache = loginCacheService.GetLoginCache();

        assertEquals("NewEmail", loginCache.GetEmail());
    }

    @Test
    public void IsPastTimeLimitNo(){

        assertFalse(loginCacheService.IsPastTimeLimit(now));
    }

    @Test
    public void IsPastTimeLimitYes(){
        assertTrue(loginCacheService.IsPastTimeLimit(LocalDate.of(2022, 1, 2)));
    }
}
