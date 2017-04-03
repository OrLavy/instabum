package com.example.or_maayan.instabum.services;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.example.or_maayan.instabum.R;
import com.example.or_maayan.instabum.auth.EmailPasswordActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by orlavy on 3/8/17.
 */

public class UIService {
    private static final UIService ourInstance = new UIService();

    public static UIService getInstance() {
        return ourInstance;
    }

    private Map<Context,ProgressDialog> contextDialogs;

    private UIService() {
        this.contextDialogs = new HashMap<>();
    }

    public void ShowProgressDialog(Context context){
        ProgressDialog progressDialog;

        if (this.contextDialogs.get(context) == null){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.ProgressBarMessage));
            progressDialog.setIndeterminate(true);

            this.contextDialogs.put(context,progressDialog);
        } else {
            progressDialog = this.contextDialogs.get(context);
        }

        progressDialog.show();
    }

    public void hideProgressDialog(Context context) {
        ProgressDialog progressDialog = this.contextDialogs.get(context);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void changeActivity(Context context, Class targetClass){
        Intent intent = new Intent(context, targetClass);
        context.startActivity(intent);
    }
}
