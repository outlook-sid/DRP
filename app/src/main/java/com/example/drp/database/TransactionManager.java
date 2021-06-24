package com.example.drp.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TransactionManager {

    private List<String> timeSlotList;

    public TransactionManager() {
        String[] time = new String[16];
        time[0] = "10:00 am - 10:20 am";
        time[1] = "10:20 am - 10:40 am";
        time[2] = "10:40 am - 11:00 am";
        time[3] = "11:00 am - 11:20 am";
        time[4] = "11:20 am - 11:40 am";
        time[5] = "11:40 am - 12:00 pm";
        time[6] = "12:00 pm - 12:20 pm";
        time[7] = "12:20 pm - 12:40 pm";
        time[8] = "12:40 pm - 01:00 pm";
        time[9] = "01:00 pm - 01:20 pm";
        time[10] = "01:20 pm - 01:40 pm";
        time[11] = "01:40 pm - 02:00 pm";
        time[12] = "03:00 pm - 03:20 pm";
        time[13] = "03:20 pm - 03:40 pm";
        time[14] = "03:40 pm - 04:00 pm";
        time[15] = "04:00 pm - 04:20 pm";

        timeSlotList = new ArrayList<>();
        Collections.addAll(timeSlotList,time);
    }

    public List<String> getTimeSlotList(List<String> alreadyBooked) {
        for (String s : alreadyBooked){
            timeSlotList.remove(new String(s));
        }
        return timeSlotList;
    }
}
