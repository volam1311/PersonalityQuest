package com.example.personalityquest.Managers;


public  class SystemManager {
    public static class SceneInfo {
        public static final int SCENEWIDTH = 720;
        public static final int SCENEHEIGHT = 480;
    }

    public static class CurrentAccount{
        /*
        * The current email that the user is signed in with
        */
        public static String currentEmail = "";
    }

    public static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
