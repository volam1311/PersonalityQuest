package com.example.personalityquest.Managers;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HashingManager {
    /*
    * Hashes the given String text and then returns it
    * */
    public static String Hash(String text){

        return BCrypt.withDefaults().hashToString(12, text.toCharArray());
    }

    public static boolean VerifyHash(String hashedText, String textToCheck){
        return BCrypt.verifyer().verify(textToCheck.toCharArray(), hashedText).verified;
    }
}
