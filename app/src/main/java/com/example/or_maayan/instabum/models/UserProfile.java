package com.example.or_maayan.instabum.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by orlavy on 3/30/17.
 */

@IgnoreExtraProperties
public class UserProfile {

    public String username;
    public String email;

    public UserProfile() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public UserProfile(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
