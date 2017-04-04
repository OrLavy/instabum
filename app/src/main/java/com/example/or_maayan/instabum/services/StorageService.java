package com.example.or_maayan.instabum.services;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;

import com.example.or_maayan.instabum.util.GenericCallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.database.Exclude;
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

    FirebaseStorage storageReference;
    StorageReference imagesRef;

    private StorageService() {
        this.storageReference = FirebaseStorage.getInstance();
        this.imagesRef = storageReference.getReference("images");
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

}
