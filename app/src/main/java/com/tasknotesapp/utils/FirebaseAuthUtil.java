package com.tasknotesapp.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthUtil {
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static boolean isUserLoggedIn() {
        return mAuth.getCurrentUser() != null;
    }

    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    public static String getCurrentUserEmail() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }

    public static void signOut() {
        mAuth.signOut();
    }
}