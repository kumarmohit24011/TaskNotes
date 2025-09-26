package com.tasknotesapp.utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    public static boolean isEmpty(String text) {
        return TextUtils.isEmpty(text) || text.trim().isEmpty();
    }

    public static boolean isValidTaskTitle(String title) {
        return !isEmpty(title) && title.trim().length() >= 3;
    }

    public static boolean isValidNoteTitle(String title) {
        return !isEmpty(title) && title.trim().length() >= 2;
    }

    public static boolean isValidTaskDescription(String description) {
        return !isEmpty(description) && description.trim().length() >= 5;
    }

    public static boolean isValidNoteContent(String content) {
        return !isEmpty(content) && content.trim().length() >= 5;
    }
}