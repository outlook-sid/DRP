package com.example.drp.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class QRCodeHelper {

    public static String encodeQRCode(String accID, String date, String key, String paymentMethod) {
        String encodeURL = encode(date, accID, paymentMethod, key);
        return encodeURL;
    }

    public static String decodeQRCode(String encodedString) {

        return null;
    }


    private static String encode(String date, String accID, String paymentMethod , String key)
    {
        int min1 = Integer.parseInt(date.split("_")[0]);
        int min2 = Integer.parseInt(date.split("_")[1]);
        int max1 = Integer.parseInt(accID.substring(1,6));
        int max2 = Integer.parseInt(accID.substring(3,8));

        StringBuilder input1 = new StringBuilder();
        input1.append(paymentMethod);
        input1.reverse();

        int b1 = (int)(Math.random()*(max1-min1+1)+min1);
        int b2 = (int)(Math.random()*(max2-min2+1)+min2);

        String res = "DRP__" +b2 + "-" + b1 + "%"+ key.length() + "%%" + input1.toString() +"%%%" + b1 + "-" + b2 +"__END";
        return res;
    }

}
