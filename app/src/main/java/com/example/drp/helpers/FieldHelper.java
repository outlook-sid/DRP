package com.example.drp.helpers;

import com.google.android.material.textfield.TextInputLayout;

public class FieldHelper {


    public static boolean validateField(TextInputLayout field) {
        String val = field.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            field.setError("Field can not be empty");
            return false;
        } else if (val.contains(" ")) {
            field.setError("Invalid Field");
            return false;
        } else {
            field.setError(null);
            field.setErrorEnabled(false);
            return true;
        }
    }

    public static boolean validateFullName(TextInputLayout fullName) {
        String val = fullName.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            fullName.setError("Field can not be empty");
            return false;
        } else {
            fullName.setError(null);
            fullName.setErrorEnabled(false);
            return true;
        }
    }
    public static boolean validateFullName(String fullName) {
        if (fullName.isEmpty()) {
            return false;
        }else if (!fullName.contains(" ")){
            return false;
        }else {
            return true;
        }
    }

    public static boolean validateUserCardID(TextInputLayout userID) {
        String val = userID.getEditText().getText().toString().trim();
        String checkSpaces = "\\A\\w{1,20}\\z";

        if (val.isEmpty()) {
            userID.setError("Field can not be empty");
            return false;
        } else if (val.length() != 13) {
            userID.setError("Invalid Card no!");
            return false;
        } else if (!val.matches(checkSpaces)) {
            userID.setError("No White spaces are allowed!");
            return false;
        } else {
            userID.setError(null);
            userID.setErrorEnabled(false);
            return true;
        }
    }

    public static boolean validateEmail(TextInputLayout email) {
        String val = email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            email.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkEmail)) {
            email.setError("Invalid Email!");
            return false;
        } else {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }

    public static boolean validatePassword(TextInputLayout password) {
        String val = password.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (!val.matches(checkPassword)) {
            password.setError("Password should contain 4 characters!");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    public static Boolean validatePhoneNo(TextInputLayout regPhoneNo) {
        String val = regPhoneNo.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            regPhoneNo.setError("Field cannot be empty");
            return false;
        } else if (val.contains(" ") || val.contains(".")) {
            regPhoneNo.setError("Invalid phone number");
            return false;
        } else if (val.length() < 10 || val.length() == 12 || (val.length() == 13 && !val.substring(0, 3).equals("+91"))) {
            regPhoneNo.setError("Invalid phone number");
            return false;
        } else {
            regPhoneNo.setError(null);
            regPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    public static Boolean validatePhoneNo(String phoneNo) {
        if (phoneNo.isEmpty()) {
            return false;
        } else if (phoneNo.contains(" ") || phoneNo.contains(".")) {
            return false;
        } else if (phoneNo.length() < 10 || phoneNo.length() == 12 || (phoneNo.length() == 13 && !phoneNo.substring(0, 3).equals("+91"))) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean validatePassword(TextInputLayout password1, TextInputLayout password2) {
        String val1 = password1.getEditText().getText().toString();
        String val2 = password2.getEditText().getText().toString();

        if (val2.isEmpty()) {
            password2.setError("Field cannot be empty");
            return false;
        } else {
            if (!val1.equals(val2)) {
                password2.setError("Password does not match");
                return false;
            } else {
                password2.setError(null);
                password2.setErrorEnabled(false);
                return true;
            }
        }
    }

    public static boolean isRepeatingTextInputEditText(TextInputLayout[] textLayout, int index) {
        String _data = textLayout[index].getEditText().getText().toString();
        for (int i = 0; i < index; i++) {
            String _s = textLayout[i].getEditText().getText().toString();
            if (_s.equals(_data)) {
                textLayout[index].setError("already added");
                return false;
            }
        }
        textLayout[index].setError(null);
        textLayout[index].setErrorEnabled(false);
        return true;
    }


}
