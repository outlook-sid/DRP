package com.example.drp.helpers;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.ViewGroup;

import com.example.drp.R;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class UtilHelper {


    public static boolean isInternetAvailable() {
        try {
            InetAddress ipAddress = InetAddress.getByName("google.com");
            //You can replace it with your name
            return !ipAddress.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileDataInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileDataInfo != null && mobileDataInfo.isConnected())) {
            return true;
        } else return false;
    }

    public static String formatPhoneNumber(String phoneNo) {
        if (phoneNo.length() == 13 && phoneNo.substring(0, 3).equals("+91")) {
            return phoneNo;
        } else if (phoneNo.charAt(0) == '0' && phoneNo.length() == 11) {
            return "+91" + phoneNo.substring(1);
        } else return "+91" + phoneNo;
    }

    public static String extract_year_month_date(String date) {
        HashMap<String, String> monthsNumber = new HashMap<>(12);
        monthsNumber.put("Jan", "01");
        monthsNumber.put("Feb", "02");
        monthsNumber.put("Mar", "03");
        monthsNumber.put("Apr", "04");
        monthsNumber.put("May", "05");
        monthsNumber.put("Jun", "06");
        monthsNumber.put("Jul", "07");
        monthsNumber.put("Aug", "08");
        monthsNumber.put("Sep", "09");
        monthsNumber.put("Oct", "10");
        monthsNumber.put("Nov", "11");
        monthsNumber.put("Dec", "12");
        String[] split_st = date.split(" ");
        if (split_st[0].length() == 1)
            split_st[0] = "0" + split_st[0];
        return split_st[2] + "_" + monthsNumber.get(split_st[1]) + "_" + split_st[0];
    }

    public static String extractYear_Month(String date) {
        String[] split_st = date.split(" ");
        return split_st[2] + "_" + split_st[1];
    }

    public static String prettyDate(String date) {
        HashMap<String, String> monthsName = new HashMap<>(12);
        monthsName.put("01", "Jan");
        monthsName.put("02", "Feb");
        monthsName.put("03", "Mar");
        monthsName.put("04", "Apr");
        monthsName.put("05", "May");
        monthsName.put("06", "Jun");
        monthsName.put("07", "Jul");
        monthsName.put("08", "Aug");
        monthsName.put("09", "Sep");
        monthsName.put("10", "Oct");
        monthsName.put("11", "Nov");
        monthsName.put("12", "Dec");
        String[] spDate = date.split("_");
        return spDate[2] + " " + monthsName.get(spDate[1]) + " " + spDate[0];
    }

    private Context context;
    private Dialog dialog;
    public UtilHelper(Context context) {
        this.context = context;
        dialog = new Dialog(context);
    }

    public  void showProgressBar() {

        dialog.setContentView(R.layout.layout_dialog_progress_bar);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();
    }
    public  void hideProgressBar() {
        dialog.setContentView(R.layout.layout_dialog_progress_bar);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.dismiss();
        //dialog = null;
    }

    public static boolean isRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (context.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }

}