package com.android.life.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Pattern;

/**
 * Created by Nag on 7/14/15.
 */
public class ValidationUtil {
    public static enum Type {
        PHONE, EMAIL, USER_NAME, FULL_NAME, PASSWORD
    }

    public static String getValidationStatus(Type type, String value) {
        switch (type) {
            case PHONE:
                return getPhoneValidationStatus(value);
            case EMAIL:
                return null;
            case USER_NAME:
                return null;
            case FULL_NAME:
                return getFullNameValidationStatus(value);
            case PASSWORD:
                return getPasswordValidationStatus(value);
        }
        return null;
    }

    public static boolean isValid(Type type, String value) {
        switch (type) {
            case PHONE:
                return isPhoneValid(value);
            case EMAIL:
                return isEmailValid(value);
            case USER_NAME:
                return false;
            case FULL_NAME:
                return false;
            case PASSWORD:
                return false;
        }
        return true;
    }

    public static String getPhoneValidationStatus(String value) {
        return null;
    }

    public static String getFullNameValidationStatus(String value) {
        if (value.split(" ").length < 2) {
            return "You must include your first and last name.";
        } else if (getCharCount('-', value) > 1) {
            return "Only one \'-\' allowed.";
        } else {
            return null;
        }
    }

    public static String getPasswordValidationStatus(String value) {
        if (!hasEnoughDiversity(value)) {
            return "Password is not valid. Use at least 1 letter and 1 number or symbol.";
        }
        return null;
    }

    public static boolean isPhoneValid(String phone) {
        return getOnlyNumerics(phone).length() == 10;
    }

    public static String getOnlyNumerics(String value) {
        return value.replaceAll("\\D+", "");
    }

    public static boolean isEmailValid(String email) {
        return email.trim().length() > 0 && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static int getCharCount(char c, String s) {
        int counter = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                counter++;
            }
        }
        return counter;
    }

    private static boolean hasEnoughDiversity(String value) {
        int n = 0;
        //Numbers
        if (value.matches(".*\\d+.*")) {
            n++;
        }
        //Alphabets
        if (value.matches(".*[a-zA-Z]+.*")) {
            n++;
        }
        //Symbols
        Pattern p = Pattern.compile("[^a-zA-Z0-9]");
        if (p.matcher(value).find()) {
            n++;
        }
        return n >= 2;
    }

    public static InputFilter fullNameFilter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!Character.isLetter(source.charAt(i)) && !Character.toString(source.charAt(i)).equals(" ") && !Character.toString(source.charAt(i)).equals("-")) {
                    return "";
                }
            }
            return null;
        }
    };
}