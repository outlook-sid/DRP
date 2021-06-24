package com.example.drp.database;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class QRCodeSessionManager {

    SharedPreferences qrCodeSession;
    SharedPreferences.Editor editor;

    private static final String SESSION_QR_CODE = "SessionQRCode";

    private  static  final  String IS_QR_CODE = "isQRCode";

    public   static  final  String KEY_ENCODED_QR_CODE = "encodedQRCode";
    public   static  final  String KEY_TRANSACTION_DATE = "transactionDate";
    public   static  final  String KEY_SELECTED_DATE = "selectedDate";
    public   static  final  String KEY_SELECTED_TIME_SLOT = "selectedTimeSlot";
    public   static  final  String KEY_PAYMENT_METHOD = "paymentMethod";
    public   static  final  String KEY_PAYMENT_SUBTOTAL = "paymentSubtotal";
    public   static  final  String KEY_RICE_PRICE_AMT = "ricePriceAmount";
    public   static  final  String KEY_WHEAT_PRICE_AMT = "wheatPriceAmount";
    public   static  final  String KEY_SUGAR_PRICE_AMT = "sugarPriceAmount";
    public   static  final  String KEY_KEROSENE_PRICE_AMT = "kerosenePriceAmount";


    public QRCodeSessionManager(Context context) {
        Context _context = context;
        qrCodeSession = _context.getSharedPreferences(SESSION_QR_CODE, Context.MODE_PRIVATE);
        editor = qrCodeSession.edit();
        editor.apply();
    }

    //  QR CODE GENERATE
    public void createQRCodeSession(String encodedQRCode,  String transactionDate, String selectedDate,
                                    String selectedTimeSlot, String paymentMethod, String paymentSubtotal, String ricePriceAmount,
                                    String wheatPriceAmount, String sugarPriceAmount, String kerosenePriceAmount) {
        editor.putBoolean(IS_QR_CODE, true);
        editor.putString(KEY_ENCODED_QR_CODE, encodedQRCode);
        editor.putString(KEY_TRANSACTION_DATE, transactionDate);
        editor.putString(KEY_SELECTED_DATE, selectedDate);
        editor.putString(KEY_SELECTED_TIME_SLOT, selectedTimeSlot);
        editor.putString(KEY_PAYMENT_METHOD, paymentMethod);
        editor.putString(KEY_PAYMENT_SUBTOTAL, paymentSubtotal);
        editor.putString(KEY_RICE_PRICE_AMT, ricePriceAmount);
        editor.putString(KEY_WHEAT_PRICE_AMT, wheatPriceAmount);
        editor.putString(KEY_SUGAR_PRICE_AMT, sugarPriceAmount);
        editor.putString(KEY_KEROSENE_PRICE_AMT, kerosenePriceAmount);

        editor.commit();
    }

    public HashMap<String, String> getQRCodeDetailsFromSession() {
        HashMap<String, String> userData = new HashMap<>();

        userData.put(KEY_ENCODED_QR_CODE, qrCodeSession.getString(KEY_ENCODED_QR_CODE,null));
        userData.put(KEY_TRANSACTION_DATE, qrCodeSession.getString(KEY_TRANSACTION_DATE,null));
        userData.put(KEY_SELECTED_DATE, qrCodeSession.getString(KEY_SELECTED_DATE,null));
        userData.put(KEY_SELECTED_TIME_SLOT, qrCodeSession.getString(KEY_SELECTED_TIME_SLOT,null));
        userData.put(KEY_PAYMENT_METHOD, qrCodeSession.getString(KEY_PAYMENT_METHOD,null));
        userData.put(KEY_PAYMENT_SUBTOTAL, qrCodeSession.getString(KEY_PAYMENT_SUBTOTAL,null));
        userData.put(KEY_RICE_PRICE_AMT, qrCodeSession.getString(KEY_RICE_PRICE_AMT,null));
        userData.put(KEY_WHEAT_PRICE_AMT, qrCodeSession.getString(KEY_WHEAT_PRICE_AMT,null));
        userData.put(KEY_SUGAR_PRICE_AMT, qrCodeSession.getString(KEY_SUGAR_PRICE_AMT,null));
        userData.put(KEY_KEROSENE_PRICE_AMT, qrCodeSession.getString(KEY_KEROSENE_PRICE_AMT,null));

        return userData;
    }


    public boolean checkIncompleteTransaction() {
        if (qrCodeSession.getBoolean(IS_QR_CODE, false)) {
            return true;
        } else return false;
    }

    public void logoutUserFromSession() {
        editor.clear();
        editor.commit();
    }


}
