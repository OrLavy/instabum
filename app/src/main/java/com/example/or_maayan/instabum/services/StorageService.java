package com.example.or_maayan.instabum.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.or_maayan.instabum.util.GenericCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by orlavy on 4/4/17.
 */

public class StorageService {
    private static final StorageService ourInstance = new StorageService();
    public static StorageService getInstance() {
        return ourInstance;
    }

    FirebaseStorage firebaseStorage;
    StorageReference imagesRef;

    private StorageService() {
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.imagesRef = firebaseStorage.getReference("images");
    }

    public void uploadImage(Bitmap photo, final GenericCallBack<String> successCallback, final GenericCallBack<Exception> failureCallBack){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference currentUploadReference = imagesRef.child(AuthService.getInstance().getCurrentUser().getUid()).child(new Date().toString());


        UploadTask uploadTask = currentUploadReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                failureCallBack.CallBack(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                successCallback.CallBack(downloadUrl.toString());
            }
        });
    }

    public void downloadImage(String imageUri){
        StorageReference islandRef = imagesRef.child("images/island.jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }
}
