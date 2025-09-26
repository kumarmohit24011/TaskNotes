package com.tasknotesapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static final String DATE_FORMAT = "MMM dd, yyyy";
    public static final String DATE_TIME_FORMAT = "MMM dd, yyyy HH:mm";
    public static final String TIME_FORMAT = "HH:mm";

    public static String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return format.format(date);
    }

    public static String formatDateTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        return format.format(date);
    }

    public static String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat format = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
        return format.format(date);
    }

    public static String getRelativeTime(Date date) {
        if (date == null) return "";

        long diff = System.currentTimeMillis() - date.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (minutes > 0) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else {
            return "Just now";
        }
    }
}