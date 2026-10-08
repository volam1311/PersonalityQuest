package com.example.personalityquest;

/**
 * This class managers everything to do with the overall system of the program
 * holding static data such as the current logged in account or screen dimensions
 * as well as global utility functions
 */
public class ApplicationManager {
    /** Stores the information needed to display a scene */
    public static class SceneInfo {
        public static final int SCENEWIDTH = 1200;
        public static final int SCENEHEIGHT = 720;
    }

    /** Stores settings used when generating tasks */
    public static class TaskConfig{
        private final static int AMOUNT_OF_TASKS = 1;
        private static int DEFAULT_SEARTCH_NUM = 228;

        /** Returns the configured number of tasks
         * @return the configured number of tasks
         */
        public static int getAmountOfTasks(){
            return AMOUNT_OF_TASKS;
        }
        /** Returns the default number of tasks to search for
         * @return the default search number
         */
        public static int getDefaultSearchNum(){
            return DEFAULT_SEARTCH_NUM;
        }
        /** Sets the default number of tasks to search for
         * @param defaultSeartchNum the new default search number
         */
        public static void setDefaultSearchNum(int defaultSeartchNum){
            DEFAULT_SEARTCH_NUM = defaultSeartchNum;
        }
    }

    /** Stores the email address of the signed-in account */
    public static class CurrentAccount{
        /*
        * The current email that the user is signed in with
        */
        private static String currentEmail = "";

        /** Returns the signed-in account email
         * @return the current account email
         */
        public static String getCurrentEmail() {
            return currentEmail;
        }

        /** Sets the signed-in account email
         * @param currentEmail the email address to store
         */
        public static void setCurrentEmail(String currentEmail) {
            CurrentAccount.currentEmail = currentEmail;
        }
    }

    public static class ThemeSettings {
        public enum Theme {DARK, LIGHT}

        private static Theme currentTheme = Theme.DARK;

        /**
         * Returns the active theme
         * @return the current theme
         */

        public static Theme getCurrentTheme(){
            return currentTheme;
        }

        /** Sets the active theme
         * @param theme the theme to switch to
         */
        public static void setCurrentTheme(Theme theme){
            currentTheme = theme;
        }

        /** Returns the CSS file name for the active theme
         * @return the theme stylesheet's file name
         */
        public static String getStylesheetName(){
            return currentTheme == Theme.LIGHT ? "theme-light.css" : "theme-dark.css";
        }
    }

    /**
     * Checks to see if a given string is null or empty
     * @param value the string you want to check
     * @return Whether a given string isEmpty
     */
    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
