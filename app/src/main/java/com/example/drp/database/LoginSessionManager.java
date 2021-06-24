package com.example.drp.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class LoginSessionManager {

    SharedPreferences loginSession;
    SharedPreferences.Editor editor;

    private static final String SESSION_LOGIN = "SessionLogIn";
    private  static  final  String IS_REMEMBER_ME = "isRememberMe";

    public   static  final  String KEY_PHONE = "userPhone";
    public   static  final  String KEY_PASSWORD = "userAccPassword";

    public LoginSessionManager(Context context) {
        Context _context = context;
        loginSession = _context.getSharedPreferences(SESSION_LOGIN, Context.MODE_PRIVATE);
        editor = loginSession.edit();
        editor.apply();
    }

    public void createRememberMeSession(String phone, String password) {
        editor.putBoolean(IS_REMEMBER_ME, true);

        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_PASSWORD, password);

        editor.commit();
    }

    public HashMap<String, String> getRememberMeDetailsFromSession() {
        HashMap<String, String> loginCred = new HashMap<>();

        loginCred.put(KEY_PHONE, loginSession.getString(KEY_PHONE,null));
        loginCred.put(KEY_PASSWORD, loginSession.getString(KEY_PASSWORD,null));

        return loginCred;
    }

    public boolean checkLogin() {
        if (loginSession.getBoolean(IS_REMEMBER_ME, false)) {
            return true;
        } else return false;
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }
}
