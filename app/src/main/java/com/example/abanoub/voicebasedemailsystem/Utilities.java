package com.example.abanoub.voicebasedemailsystem;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Abanoub on 2017-12-03.
 */

public class Utilities {

    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public static FirebaseUser getCurrentUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
            return null;

        return user;
    }

    public static String getModifiedCurrentEmail() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
            return null;
        String currentUserEmail = user.getEmail().replace(".", "_");

        return currentUserEmail;
    }


    public static ArrayList<NewEmail> getAllEmails(DataSnapshot dataSnapshot) {

        Map<String, Object> dataSnapShot = (Map<String, Object>) dataSnapshot.getValue();
        ArrayList<NewEmail> list = new ArrayList<>();

        //iterate through each email, ignoring their UID
        if (dataSnapShot != null) {
            for (Map.Entry<String, Object> entry : dataSnapShot.entrySet()) {

                //Get email map
                Map singleEmail = (Map) entry.getValue();
                NewEmail emailObj = new NewEmail();
                emailObj.sender = (String) singleEmail.get("sender");
                emailObj.receiver = (String) singleEmail.get("receiver");
                emailObj.title = (String) singleEmail.get("title");
                emailObj.body = (String) singleEmail.get("body");
                emailObj.date = (String) singleEmail.get("date");
                emailObj.isFavorite = (String) singleEmail.get("isFavorite");
                emailObj.pushID = (String) singleEmail.get("pushID");
                list.add(emailObj);
            }
        }
        return list;
    }

    public static ArrayList<NewEmail> getFavoriteEmails(DataSnapshot dataSnapshot) {

        Map<String, Object> dataSnapShot = (Map<String, Object>) dataSnapshot.getValue();
        ArrayList<NewEmail> list = new ArrayList<>();

        //iterate through each email, ignoring their UID
        if (dataSnapShot != null) {
            for (Map.Entry<String, Object> entry : dataSnapShot.entrySet()) {

                //Get email map
                Map singleEmail = (Map) entry.getValue();

                if (((String) singleEmail.get("isFavorite")).equals("yes")) {
                    NewEmail emailObj = new NewEmail();
                    emailObj.sender = (String) singleEmail.get("sender");
                    emailObj.receiver = (String) singleEmail.get("receiver");
                    emailObj.title = (String) singleEmail.get("title");
                    emailObj.body = (String) singleEmail.get("body");
                    emailObj.date = (String) singleEmail.get("date");
                    emailObj.isFavorite = (String) singleEmail.get("isFavorite");
                    emailObj.pushID = (String) singleEmail.get("pushID");
                    list.add(emailObj);
                }
            }
        }
        return list;
    }

    public static ArrayList<UserEmail> getAllUsersEmails(DataSnapshot dataSnapshot) {

        Map<String, Object> dataSnapShot = (Map<String, Object>) dataSnapshot.getValue();
        ArrayList<UserEmail> list = new ArrayList<>();

        //iterate through each user, ignoring their UID
        if (dataSnapShot != null) {
            for (Map.Entry<String, Object> entry : dataSnapShot.entrySet()) {

                //Get user map
                Map singleEmail = (Map) entry.getValue();
                UserEmail userEmailObj = new UserEmail();
                userEmailObj.email = (String) singleEmail.get("email");
                userEmailObj.pushID = (String) singleEmail.get("pushID");
                list.add(userEmailObj);
            }
        }
        return list;
    }

    public static NewUser getPersonalData(DataSnapshot dataSnapshot) {

        Map<String, Object> dataSnapShot = (Map<String, Object>) dataSnapshot.getValue();

        NewUser userObj = new NewUser();

        if (dataSnapShot != null) {
            for (Map.Entry<String, Object> entry : dataSnapShot.entrySet()) {
                //Get user map
                Map singleUser = (Map) entry.getValue();

                userObj.fullname = (String) singleUser.get("fullname");
                userObj.email = (String) singleUser.get("email");
                userObj.password = (String) singleUser.get("password");
                userObj.birthdate = (String) singleUser.get("birthdate");
                userObj.gender = (String) singleUser.get("gender");
                userObj.phoneNumber = (String) singleUser.get("phoneNumber");
                userObj.secretQuestion = (String) singleUser.get("secretQuestion");
                userObj.secretAnswer = (String) singleUser.get("secretAnswer");
                userObj.country = (String) singleUser.get("country");
                userObj.pushID = (String) singleUser.get("pushID");
            }
        }
        return userObj;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
