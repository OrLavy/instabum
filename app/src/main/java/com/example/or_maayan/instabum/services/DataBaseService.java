package com.example.or_maayan.instabum.services;

import android.provider.ContactsContract;
import android.util.Log;

import com.example.or_maayan.instabum.models.Post;
import com.example.or_maayan.instabum.models.UserProfile;
import com.example.or_maayan.instabum.util.GenericCallBack;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private static final String TAG = "DataBaseService";

    public static final String POSTS_URL = "/posts";
    public static final String USERS_URL = "/users";
    public static final String USER_POSTS = "self_posts";
    public static final String USER_HIGHEST_STARS = "highest_stars";

    FirebaseDatabase database;
    DatabaseReference baseRef;
    DatabaseReference feedRef;
    DatabaseReference usersRef;

    private DataBaseService() {
        database = FirebaseDatabase.getInstance();
        this.baseRef = database.getReference("");
        this.feedRef = database.getReference(POSTS_URL);
        this.usersRef = database.getReference(USERS_URL);
    }

    public void writeNewUser(String userId, String name, String email) {
        UserProfile user = new UserProfile(name, email);
        this.baseRef.child("users").child(userId).setValue(user);
    }

    public Task<Void> createPost(String caption, String photoDownloadUri){
        String key = this.feedRef.push().getKey();
        Post post = createNewPostInstance(key, caption,photoDownloadUri);
        Map<String, Object> postValues = post.toMap();


        Map<String,Object> childUpdates = createNewPostUpdateMap(post, key);

        return baseRef.updateChildren(childUpdates);
    }

    /**
     *
     */
    public void getAllPostsBySelf(final GenericCallBack<List<String>> postIdsListCallback){
        DatabaseReference postsRef = usersRef.child(AuthService.getInstance().getCurrentUser().getUid()).child(USER_POSTS);
        postsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> postsIds = new ArrayList<String>();
                for (DataSnapshot postIdSnapshot: dataSnapshot.getChildren()) {
                    // each single snapshot holds the id of a post
                    postsIds.add(postIdSnapshot.getKey().toString());
                }
                postIdsListCallback.CallBack(postsIds);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getBestPostBySelf(final GenericCallBack<Long> bestPostStartCallback){
        getHighestStartsRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long highestPost = Long.valueOf(0);
                        Object value = dataSnapshot.getValue();
                        if (value != null){
                            highestPost = (Long) dataSnapshot.getValue();
                        }

                        bestPostStartCallback.CallBack(highestPost);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public void setBestPostBySelf(int newValue){
        getHighestStartsRef().setValue(newValue);
    }

    public void removeListenerOnPost(String postId, ValueEventListener oldListener){
        this.feedRef.child(postId).removeEventListener(oldListener);
    }

    public ValueEventListener addListenerToPost(String postId, final GenericCallBack<Post> postValueCallback){

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postValueCallback.CallBack(dataSnapshot.getValue(Post.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        this.feedRef.child(postId).addValueEventListener(valueEventListener);

        return valueEventListener;
    }

    public void toggleStartOnPost(Post post, final GenericCallBack<Boolean> isComitted){
        // Get reference to the post node
        DatabaseReference postRef = feedRef.child(post.id);
        final FirebaseUser user = AuthService.getInstance().getCurrentUser();
        final String userId = user.getUid();
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Post p = mutableData.getValue(Post.class);
                if (p == null) {
                    return Transaction.success(mutableData);
                }

                if (p.stars.containsKey(userId)) {
                    // Unstar the post and remove self from stars
                    p.starCount = p.starCount - 1;
                    p.stars.remove(userId);
                } else {
                    // Star the post and add self to stars
                    p.starCount = p.starCount + 1;
                    p.stars.put(userId, true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                isComitted.CallBack(b);
            }
        });

    }


    ///
    /// Helper methods
    ///

    private Post createNewPostInstance(String key, String caption, String photoDownloadUri){
        String uid = AuthService.getInstance().getCurrentUser().getUid();
        String author = AuthService.getInstance().getCurrentUser().getEmail();
        Post post = new Post(key, uid,author, caption, photoDownloadUri);
        return post;
    }

    private Map<String,Object> createNewPostUpdateMap(Post post, String postKey){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(POSTS_URL + "/" + postKey, post.toMap());
        childUpdates.put(USERS_URL + "/" + post.uid + "/" + USER_POSTS + "/" + postKey, true);
        return childUpdates;
    }

    ///
    /// Refrence building methods
    ///
    private DatabaseReference getHighestStartsRef(){
        return usersRef.child(AuthService.getInstance().getCurrentUser().getUid()).child(USER_HIGHEST_STARS);
    }
}
