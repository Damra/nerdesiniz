package com.bilgetech.nerdesiniz.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.bilgetech.nerdesiniz.R;


/**
 * Created by damra on 29.07.2017.
 */

public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();

    // (make variable "public" to access from outside)

    // Sharedpref file name
    private static final String PREF_NAME = "BilgeTechPre";

    // All Shared Preferences Keys
    private static final String KEY_THEME_STYLE = "THEME_STYLE";
    private static final String KEY_THEME_RADIO_BUTTON = "THEME_RADIO_BUTTON"; // int r.id.rbRed vsvs
    private static final String KEY_UPDATES_REQUESTED = "KEY_UPDATES_REQUESTED";

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private static Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static SessionManager instance;

    private static Object LOCK = SessionManager.class;

    public static SessionManager getInstance(Context context) {
        if (instance == null && _context == null) {
            synchronized (LOCK) {
                if (instance == null && _context == null) {
                    _context = context;
                    instance = new SessionManager();
                }
            }
        }
        return instance;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public SharedPreferences getPref() {
        return pref;
    }

    // Constructor
    private SessionManager() {
        Log.d(TAG, "Initializing SessionManager ");
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * save theme color data
     */

    public SessionManager setThemeStyle(int style) {

        editor.putInt(KEY_THEME_STYLE, style);

        // commit changes
        editor.commit();

        return this;

    }

    /**
     * get theme style res int
     */
    public int getThemeStyle() {
        return pref.getInt(KEY_THEME_STYLE, R.style.PrimaryColorRed);
    }

    public SessionManager setThemeRBResId(int resId) {

        editor.putInt(KEY_THEME_RADIO_BUTTON, resId);

        // commit changes
        editor.commit();

        return this;

    }

    public int getThemeRBResId() {
        return pref.getInt(KEY_THEME_RADIO_BUTTON, R.id.rbRed);
    }

}