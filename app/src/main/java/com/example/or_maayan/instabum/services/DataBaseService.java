package com.example.or_maayan.instabum.services;

import android.provider.ContactsContract;

import com.example.or_maayan.instabum.models.Post;
import com.example.or_maayan.instabum.models.UserProfile;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by orlavy on 3/30/17.
 */

public class DataBaseService {
    private static final DataBaseService ourInstance = new DataBaseService();
    public static DataBaseService getInstance() {
        return ourInstance;
    }

    public static final String POSTS_URL = "/posts";
    public static final String USERS_URL = "/users";
    public static final String USER_POSTS = "self_posts";

    FirebaseDatabase database;
    DatabaseReference baseRef;
    DatabaseReference feedRef;


    private DataBaseService() {
        database = FirebaseDatabase.getInstance();
        this.baseRef = database.getReference("");
        this.feedRef = database.getReference("feeds");
    }

    public void writeNewUser(String userId, String name, String email) {
        UserProfile user = new UserProfile(name, email);
        this.baseRef.child("users").child(userId).setValue(user);
    }

    public Task<Void> createPost(String caption, String photoDownloadUri){
        String key = this.feedRef.push().getKey();
        Post post = createNewPostInstance(caption,photoDownloadUri);
        Map<String, Object> postValues = post.toMap();


        Map<String,Object> childUpdates = createNewPostUpdateMap(post, key);

        return baseRef.updateChildren(childUpdates);
    }

    public void getAllPostsBySelf(FirebaseUser user){

    }


    ///
    /// Helper methods
    ///

    private Post createNewPostInstance(String caption, String photoDownloadUri){
        String uid = AuthService.getInstance().getCurrentUser().getUid();
        String author = AuthService.getInstance().getCurrentUser().getEmail();
        Post post = new Post(uid,author, caption, photoDownloadUri);
        return post;
    }

    private Map<String,Object> createNewPostUpdateMap(Post post, String postKey){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(POSTS_URL + "/" + postKey, post.toMap());
        childUpdates.put(USERS_URL + "/" + post.uid + "/" + USER_POSTS + "/" + postKey, postKey);
        return childUpdates;
    }
}
