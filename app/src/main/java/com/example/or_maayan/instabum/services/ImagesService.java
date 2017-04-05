package com.example.or_maayan.instabum.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

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

    public static boolean isImageInCache(String imageUri){
        return LocalDBService.getInstance().hasCacheForImage(imageUri);
    }



    public static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        public LoadImageTask(Listener listener) {
            mListener = listener;
        }

        public interface Listener{

            void onImageLoaded(Bitmap bitmap);
            void onError();
        }

        private Listener mListener;
        @Override
        protected Bitmap doInBackground(String... args) {
            try {
                String imageUri = args[0];
                if (isImageInCache(imageUri)){
                    return LocalDBService.getInstance().loadImageFromFile(imageUri);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUri).getContent());
                    LocalDBService.getInstance().saveImageToFile(bitmap, imageUri);
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                mListener.onImageLoaded(bitmap);
            } else {
                mListener.onError();
            }
        }
    }
}
