package com.example.or_maayan.instabum.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {

    public String id;
    public String uid;
    public String author;
    public String title;
    public String photoUrl;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String id, String uid, String author, String title, String photoUrl) {
        this.id = id;
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.photoUrl = photoUrl;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("photoUrl", photoUrl);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("creation", ServerValue.TIMESTAMP);

        return result;
    }

}