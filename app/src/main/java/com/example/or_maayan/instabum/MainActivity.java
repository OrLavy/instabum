package com.example.or_maayan.instabum;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.or_maayan.instabum.auth.EmailPasswordActivity;
import com.example.or_maayan.instabum.services.AuthService;
import com.example.or_maayan.instabum.services.DataBaseService;
import com.example.or_maayan.instabum.services.UIService;
import com.example.or_maayan.instabum.skeleton.FeedTabs;

import java.sql.DatabaseMetaData;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main)

        if(AuthService.getInstance().getCurrentUser() != null){
            Log.d(TAG, "User is already logged");
            this.goToTabs();
        } else {
            Log.d(TAG, "User is not logged");
            this.goToSignInPage();
        }
    }

    private void goToTabs(){
        UIService.getInstance().changeActivity(this, FeedTabs.class);
    }

    private void goToSignInPage(){
        UIService.getInstance().changeActivity(this,EmailPasswordActivity.class);
    }
}
