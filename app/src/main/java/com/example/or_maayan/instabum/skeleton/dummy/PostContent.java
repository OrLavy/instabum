package com.example.or_maayan.instabum.skeleton.dummy;

import com.example.or_maayan.instabum.models.Post;
import com.example.or_maayan.instabum.services.AuthService;
import com.example.or_maayan.instabum.services.DataBaseService;
import com.example.or_maayan.instabum.util.Constants;
import com.example.or_maayan.instabum.util.GenericCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PostContent {

    public static final List<GenericCallBack<Void>> DATA_UPDATED_LIST = new ArrayList<>();

    /**
     * An array of sample (dummy) items.
     */
    public static final List<PostItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, PostItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = Constants.FEED_SIZE;

    static {
        DataBaseService.getInstance().addFeedListener(new GenericCallBack<List<Post>>() {
            @Override
            public void CallBack(List<Post> feedPosts) {
                ITEMS.clear();
                String userId = AuthService.getInstance().getCurrentUser().getUid();
                for (Post post : feedPosts){
                    boolean isStarred = post.stars.containsKey(userId);
                    addItem(new PostItem(post.id,post.title,post.photoUrl, isStarred, post));
                }

                for (GenericCallBack<Void> callBack : DATA_UPDATED_LIST){
                    callBack.CallBack(null);
                }
            }
        });
    }

    public static void addChangeListener(GenericCallBack<Void> changeListener){
        DATA_UPDATED_LIST.add(changeListener);
    }

    private static void addItem(PostItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */

    public static class PostItem {
        public final String id;
        public final String caption;
        public final String PhotoUrl;
        public final boolean isStarred;
        public final Post originalPost;

        public PostItem(String id, String caption, String PhotoUrl, boolean isStarred, Post originalPost) {
            this.id = id;
            this.caption = caption;
            this.PhotoUrl = PhotoUrl;
            this.isStarred = isStarred;
            this.originalPost = originalPost;
        }

        @Override
        public boolean equals(Object o) {

            if (o == this) return true;
            if (!(o instanceof PostItem)) {
                return false;
            }

            PostItem postItem = (PostItem) o;

            return postItem.id == this.id;
        }

        //Idea from effective Java : Item 9
        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + id.hashCode();
            return result;
        }
    }
}
