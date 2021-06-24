package com.example.drp.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class UserSessionManager {

    SharedPreferences userSession;
    SharedPreferences.Editor editor;

    private static final String SESSION_DASHBOARD = "SessionDashboard";

    private  static  final  String IS_LOGIN = "isLogin";

    public   static  final  String KEY_NAME = "userName";
    public   static  final  String KEY_EMAIL = "userEmail";
    public   static  final  String KEY_PHONE = "userPhone";
    public   static  final  String KEY_PASSWORD = "userAccPassword";
    public   static  final  String KEY_DOB = "userDOB";
    public   static  final  String KEY_GENDER = "userGender";
    public   static  final  String KEY_RATION_CARD = "userRationCardID";
    public   static  final  String KEY_LOCATION1 = "userLocationState";
    public   static  final  String KEY_LOCATION2 = "userLocationDistrict";
    public   static  final  String KEY_LOCATION3 = "userLocationMunicipality";
    public   static  final  String KEY_LOCATION4 = "userLocationWardNo";
    public   static  final  String KEY_DEALER_NAME = "userDealerName";
    public   static  final  String KEY_DEALER_ID = "userDealerID";
    public   static  final  String KEY_LINKED_CARD_COUNT = "userLinkedCardCount";
    public   static  final  String KEY_TRANSACTION_COUNT = "userTransactionCount";
    public   static  final  String KEY_LINKED_CARDS_SET = "linkedCards";

    public UserSessionManager(Context context) {
        Context _context = context;
        userSession = _context.getSharedPreferences(SESSION_DASHBOARD, Context.MODE_PRIVATE);
        editor = userSession.edit();
        editor.apply();
    }

    //  DASHBOARD PAGE
    public void createDashboardSession(String name, String email, String phone, String password,
                                       String dob, String gender, String rationCard, String location1,
                                       String location2, String location3, String location4, String dealerName,
                                       String dealerID, String linkedCardCount, String transactionCount, Set<String> linkedCards) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_DOB, dob);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_RATION_CARD, rationCard);
        editor.putString(KEY_LOCATION1, location1);
        editor.putString(KEY_LOCATION2, location2);
        editor.putString(KEY_LOCATION3, location3);
        editor.putString(KEY_LOCATION4, location4);
        editor.putString(KEY_DEALER_NAME, dealerName);
        editor.putString(KEY_DEALER_ID, dealerID);
        editor.putString(KEY_LINKED_CARD_COUNT, linkedCardCount);
        editor.putString(KEY_TRANSACTION_COUNT, transactionCount);
        editor.putStringSet(KEY_LINKED_CARDS_SET, linkedCards);

        editor.commit();
    }

    public HashMap<String, String> getUserDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_NAME, userSession.getString(KEY_NAME,null));
        userData.put(KEY_PHONE, userSession.getString(KEY_PHONE,null));
        userData.put(KEY_EMAIL, userSession.getString(KEY_EMAIL,null));
        userData.put(KEY_PASSWORD, userSession.getString(KEY_PASSWORD,null));
        userData.put(KEY_DOB, userSession.getString(KEY_DOB,null));
        userData.put(KEY_GENDER, userSession.getString(KEY_GENDER,null));
        userData.put(KEY_RATION_CARD, userSession.getString(KEY_RATION_CARD,null));
        userData.put(KEY_LOCATION1, userSession.getString(KEY_LOCATION1,null));
        userData.put(KEY_LOCATION2, userSession.getString(KEY_LOCATION2,null));
        userData.put(KEY_LOCATION3, userSession.getString(KEY_LOCATION3,null));
        userData.put(KEY_LOCATION4, userSession.getString(KEY_LOCATION4,null));
        userData.put(KEY_DEALER_NAME, userSession.getString(KEY_DEALER_NAME,null));
        userData.put(KEY_DEALER_ID, userSession.getString(KEY_DEALER_ID,null));
        userData.put(KEY_LINKED_CARD_COUNT, userSession.getString(KEY_LINKED_CARD_COUNT,null));
        userData.put(KEY_TRANSACTION_COUNT, userSession.getString(KEY_TRANSACTION_COUNT,null));
        return userData;
    }

    public List<String> getUserLinkedCardsList() {
        List<String> cardList = new ArrayList<String>();
        cardList.addAll(userSession.getStringSet(KEY_LINKED_CARDS_SET, null));
        return cardList;
    }

    public boolean checkDashboardLogin() {
        if (userSession.getBoolean(IS_LOGIN, false)) {
            return true;
        } else return false;
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }

}
