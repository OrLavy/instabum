package com.example.or_maayan.instabum.skeleton;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.or_maayan.instabum.R;
import com.example.or_maayan.instabum.models.Post;
import com.example.or_maayan.instabum.services.AuthService;
import com.example.or_maayan.instabum.services.DataBaseService;
import com.example.or_maayan.instabum.services.UIService;
import com.example.or_maayan.instabum.util.GenericCallBack;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SelfFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SelfFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelfFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private TextView totalPostsTextView;
    private TextView latestPostStarsTextView;
    private TextView bestPostStarsTextView;

    private long bestStarCount = 0;

    private List<String> idsList;
    private String latestPostId = null;
    private ValueEventListener latestPostListener = null;

    public SelfFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SelfFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SelfFragment newInstance() {
        SelfFragment fragment = new SelfFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inf = inflater.inflate(R.layout.fragment_self, container, false);
        initializeUi(inf);


        UIService.getInstance().ShowProgressDialog(inf.getContext());

        DataBaseService.getInstance().getAllPostsBySelf(new GenericCallBack<List<String>>() {
            @Override
            public void CallBack(List<String> idsList) {
                updateIdsList(idsList);
                UIService.getInstance().hideProgressDialog(inf.getContext());
            }
        });

        DataBaseService.getInstance().getBestPostBySelf(new GenericCallBack<Long>() {
            @Override
            public void CallBack(Long value) {
                bestStarCount = value;
                bestPostStarsTextView.setText(String.valueOf(value));
            }
        });

        return inf;
    }

    private void initializeUi(View v){
        totalPostsTextView = (TextView)  v.findViewById(R.id.selfFragment_totalposts);
        latestPostStarsTextView = (TextView)  v.findViewById(R.id.selfFragment_latestPostStars);
        bestPostStarsTextView = (TextView)  v.findViewById(R.id.selfFragment_bestPostStarsTextView);
    }


    private void updateIdsList(List<String> newIdsList){
        this.idsList = newIdsList;
        updateTotalPosts(idsList.size());
        updateLatestPost();
    }

    private void updateTotalPosts(int sum){
        this.totalPostsTextView.setText(String.valueOf(sum));
    }

    private void updateLatestPost(){
        // Removes old listener
        if (this.latestPostListener != null){
            DataBaseService.getInstance().removeListenerOnPost(latestPostId,latestPostListener);
            latestPostListener = null;
        }

        if (this.idsList.isEmpty()){
            this.latestPostId = null;
        } else {
            latestPostId = this.idsList.get(this.idsList.size() - 1);
            latestPostListener = DataBaseService.getInstance().addListenerToPost(latestPostId, new GenericCallBack<Post>() {
                @Override
                public void CallBack(Post post) {

                    if (post != null){
                        int starCount = post.starCount;
                        latestPostStarsTextView.setText(String.valueOf(starCount));
                        updateBestStarCount(starCount);
                    }

                }
            });
        }

    }

    private void updateBestStarCount(int currentStarCount){
        if (currentStarCount > bestStarCount){
            DataBaseService.getInstance().setBestPostBySelf(currentStarCount);
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
