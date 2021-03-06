package com.example.or_maayan.instabum.services;

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.or_maayan.instabum.auth.Credentials;
import com.example.or_maayan.instabum.auth.EmailPasswordActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by orlavy on 3/8/17.
 */

public class AuthService {
    private static final AuthService ourInstance = new AuthService();
    public static AuthService getInstance() {
        return ourInstance;
    }

    private String TAG = "UIService";
    private FirebaseAuth auth;

    private FirebaseUser user;

    private AuthService() {
        this.auth = FirebaseAuth.getInstance();

        this.user = this.auth.getCurrentUser();

        this.auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null){
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG,"onAuthStateChanged:signed_out:" + user);
                }
            }
        });
    }

    public FirebaseUser getCurrentUser(){
        return this.user;
    }

    public void SignUp_EmailPassword(Credentials credentials, OnCompleteListener onCompleteListener){
        this.auth.createUserWithEmailAndPassword(credentials.email, credentials.password).
                addOnCompleteListener(onCompleteListener);
    }
    public void SignIn_EmailPassword(Credentials credentials, OnCompleteListener onCompleteListener){
        this.auth.signInWithEmailAndPassword(credentials.email, credentials.password).
                addOnCompleteListener(onCompleteListener);
    }

    public void SignOut(){
        this.auth.signOut();
    }


}
