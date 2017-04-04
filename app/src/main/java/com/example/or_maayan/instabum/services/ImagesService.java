package com.example.or_maayan.instabum.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by orlavy on 4/4/17.
 */

public class ImagesService {
    private static final ImagesService ourInstance = new ImagesService();
    public static ImagesService getInstance() {
        return ourInstance;
    }

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImagesService() {
    }

    public void takePicture(Activity activity){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
}
