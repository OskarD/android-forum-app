package me.oskard.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Oskar on 5/29/2015.
 */
public class AppSharedPreferences {

    public static final String
            DEFAULT_SHARED_PREFS        = "mySharedPreferences",
            TAG_STORED_USERNAME         = "stored_username",
            TAG_STORED_LOGIN_STRING     = "stored_login_string";

    private SharedPreferences sharedPreferences;

    private SharedPreferences.Editor sharedPreferencesEditor;

    private String preferencesString;

    public AppSharedPreferences() {
        this(DEFAULT_SHARED_PREFS);
    }

    public AppSharedPreferences(String preferencesString) {
        this.preferencesString = preferencesString;
    }

    public SharedPreferences getSharedPreferences(Activity referencedActivity) {
        if(sharedPreferences == null) {
            synchronized (AppSharedPreferences.class) {
                if(sharedPreferences == null) {
                    sharedPreferences = referencedActivity.getSharedPreferences(preferencesString, Context.MODE_PRIVATE);
                }
            }
        }
        return sharedPreferences;
    }

    public SharedPreferences.Editor getSharedPreferencesEditor(Activity referencedActivity) {
        if(sharedPreferencesEditor == null) {
            synchronized (AppSharedPreferences.class) {
                if(sharedPreferencesEditor == null)
                    sharedPreferencesEditor = getSharedPreferences(referencedActivity).edit();
            }
        }
        return sharedPreferencesEditor;
    }
}
