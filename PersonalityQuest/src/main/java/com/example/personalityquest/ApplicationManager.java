package com.example.personalityquest;

/**
 * This class managers everything to do with the overall system of the program
 * holding static data such as the current logged in account or screen dimensions
 * as well as global utility functions
 */
public class ApplicationManager {
    public static class SceneInfo {
        public static final int SCENEWIDTH = 1100;
        public static final int SCENEHEIGHT = 720;
    }

    public static class TaskConfig{
        private static int AMOUNT_OF_TASKS = 3;
        private static int DEFAULT_SEARTCH_NUM = 1;

        public static int getAmountOfTasks(){
            return AMOUNT_OF_TASKS;
        }
        public static int getDefaultSearchNum(){
            return DEFAULT_SEARTCH_NUM;
        }
        public static void setDefaultSearchNum(int defaultSeartchNum){
            DEFAULT_SEARTCH_NUM = defaultSeartchNum;
        }
    }

    public static class CurrentAccount{
        /*
        * The current email that the user is signed in with
        */
        private static String currentEmail = "";

        public static String getCurrentEmail() {
            return currentEmail;
        }

        public static void setCurrentEmail(String currentEmail) {
            CurrentAccount.currentEmail = currentEmail;
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
