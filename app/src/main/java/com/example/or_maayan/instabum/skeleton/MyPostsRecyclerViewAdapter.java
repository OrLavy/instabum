package com.example.or_maayan.instabum.skeleton;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.or_maayan.instabum.R;
import com.example.or_maayan.instabum.services.DataBaseService;
import com.example.or_maayan.instabum.services.ImagesService;
import com.example.or_maayan.instabum.services.StorageService;
import com.example.or_maayan.instabum.skeleton.PostsFragment.OnListFragmentInteractionListener;
import com.example.or_maayan.instabum.skeleton.dummy.PostContent;
import com.example.or_maayan.instabum.util.GenericCallBack;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PostContent.PostItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyPostsRecyclerViewAdapter extends RecyclerView.Adapter<MyPostsRecyclerViewAdapter.ViewHolder> {

    private final List<PostContent.PostItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyPostsRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mValues = PostContent.ITEMS;
        mListener = listener;

        PostContent.addChangeListener(new GenericCallBack<Void>() {
            @Override
            public void CallBack(Void value) {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mValues.get(position));
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements ImagesService.LoadImageTask.Listener {
        public final View mView;
        public final TextView mCaptionView;
        public final ImageView mImageView;
        public final Button mStarredButton;
        public PostContent.PostItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCaptionView = (TextView) view.findViewById(R.id.postItem_Caption);
            mImageView = (ImageView) view.findViewById(R.id.postItem_imageView);
            mStarredButton = (Button) view.findViewById(R.id.postItem_starButton);
        }

        public void setItem(final PostContent.PostItem postItem){
            this.mItem = postItem;
            this.mCaptionView.setText(mItem.caption);
            StorageService.getInstance().downloadImage(mItem.PhotoUrl);
            new ImagesService.LoadImageTask(this).execute(mItem.PhotoUrl);
            mStarredButton.setText(postItem.isStarred ? R.string.postItem_liked : R.string.postItem_notLiked);

            mStarredButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataBaseService.getInstance().toggleStartOnPost(postItem.originalPost);
                }
            });
        }

        @Override
        public void onImageLoaded(Bitmap bitmap) {
            mImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onError() {

        }
    }
}
