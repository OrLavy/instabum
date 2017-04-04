package com.example.or_maayan.instabum.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.or_maayan.instabum.R;
import com.example.or_maayan.instabum.services.AuthService;
import com.example.or_maayan.instabum.services.UIService;
import com.example.or_maayan.instabum.skeleton.FeedTabs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class EmailPasswordActivity extends AppCompatActivity {

    String TAG = "EmailPasswordActivity";
    private TextView statusTextView;
    private TextView detailTextView;
    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);
        this.initializeMembers();
        this.assignListeners();
    }

    private void initializeMembers(){
        this.emailField = (EditText) findViewById(R.id.editTextEmail);
        this.passwordField = (EditText) findViewById(R.id.editTextPassword);
    }

    private void assignListeners(){
        findViewById(R.id.Login_buttonSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp(getCredentials());
            }
        });
        findViewById(R.id.Login_buttonSignIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(getCredentials());
            }
        });
        findViewById(R.id.Login_ForgotPassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword(getCredentials());
            }
        });
    }

    private void SignUp(Credentials credentials){
        if (!validateForm()){
            return;
        } else {
            UIService.getInstance().ShowProgressDialog(this);

            AuthService.getInstance().SignUp_EmailPassword(credentials, OnSignUpAttempt);
        }
    }

    private void signIn(Credentials credentials){
        if (!validateForm()){
            return;
        } else {
            UIService.getInstance().ShowProgressDialog(this);

            AuthService.getInstance().SignIn_EmailPassword(credentials, OnSignInAttempt);
        }
    }

    private void forgotPassword(Credentials credentials){

    }

    private OnCompleteListener OnSignInAttempt = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            handleAuthenticationAttempt(task.isSuccessful(), FeedTabs.class);
        }
    };

    private OnCompleteListener OnSignUpAttempt = new OnCompleteListener() {
        @Override
        public void onComplete(@NonNull Task task) {
            handleAuthenticationAttempt(task.isSuccessful(), FeedTabs.class);
        }
    };

    private void handleAuthenticationAttempt(boolean isSuccessful, Class newActivity){
        if (! isSuccessful){
            Toast.makeText(EmailPasswordActivity.this, R.string.AuthFailed,Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(EmailPasswordActivity.this, newActivity);
            EmailPasswordActivity.this.startActivity(intent);
        }

        UIService.getInstance().hideProgressDialog(EmailPasswordActivity.this);
    }

    private boolean validateForm() {
        boolean valid = true;

        valid &= validateField(emailField);
        valid &= validateField(passwordField);

        return valid;
    }

    private boolean validateField(EditText field){

        if (TextUtils.isEmpty(field.getText().toString())) {
            field.setError("Required.");
            return false;
        } else {
            field.setError(null);
        }
        return true;
    }

    private Credentials getCredentials(){
        return new Credentials(emailField.getText().toString(),passwordField.getText().toString());
    }

}
