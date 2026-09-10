package com.example.personalityquest.Services;

import at.favre.lib.crypto.bcrypt.BCrypt;
/**
 * This class managers everything to do with hashing and has utility functions to hash
 * a string or check whether a given hashed string and a plaintext string match
 */
public class HashingService {
    /**
     * Hashes the given string
     * @param text "The text you wish to hash"
     * @return "The hashed version of the input text"
     */
    public static String Hash(String text){

        return BCrypt.withDefaults().hashToString(12, text.toCharArray());
    }

    /**
     * Checks to see whether the given hashed text is the same as the textToCheck
     * @param hashedText "Hashed text you want to match"
     * @param textToCheck "The text you want to check"
     * @return "Whether hashedText == textToCheck"
     */
    public static boolean VerifyHash(String hashedText, String textToCheck){
        return BCrypt.verifyer().verify(textToCheck.toCharArray(), hashedText).verified;
    }
}
