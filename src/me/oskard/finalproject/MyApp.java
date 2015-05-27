package me.oskard.finalproject;

import android.content.Context;

/**
 * Created by Oskar on 5/27/2015.
 */
public class MyApp extends android.app.Application {

    public static final String LOG_TAG = "OskarsForum";

    private static MyApp instance;
    private static User activeUser;

    public MyApp() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

    public static User getActiveUser() {
        return activeUser;
    }

    public static void setActiveUser(User activeUser) {
        MyApp.activeUser = activeUser;
    }

    public static void resetActiveUser() {
        activeUser = null;
    }
}
