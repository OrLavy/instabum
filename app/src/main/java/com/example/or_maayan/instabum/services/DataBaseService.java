package com.example.or_maayan.instabum.services;

import android.provider.ContactsContract;

import com.example.or_maayan.instabum.models.UserProfile;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by orlavy on 3/30/17.
 */

public class DataBaseService {
    private static final DataBaseService ourInstance = new DataBaseService();
    public static DataBaseService getInstance() {
        return ourInstance;
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference baseRef;
    DatabaseReference feedRef;


    private DataBaseService() {
        this.baseRef = database.getReference("");
        this.feedRef = database.getReference("feeds");
    }

    public void initializeForUser(FirebaseUser user){

    }

    public void writeNewUser(String userId, String name, String email) {
        UserProfile user = new UserProfile(name, email);
        this.baseRef.child("users").child(userId).setValue(user);
    }

    public void getAllPostsBySelf(FirebaseUser user){

    }
}
