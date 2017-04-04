package com.example.or_maayan.instabum.skeleton;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.or_maayan.instabum.MainActivity;
import com.example.or_maayan.instabum.R;
import com.example.or_maayan.instabum.services.AuthService;
import com.example.or_maayan.instabum.services.UIService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AcountFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AcountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AcountFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private TextView textViewAccountName;


    public AcountFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AcountFragment.
     */
    public static AcountFragment newInstance() {
        AcountFragment fragment = new AcountFragment();
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
        View inf = inflater.inflate(R.layout.fragment_acount, container, false);
        this.handleUIBindings(inf);
        return inf;
    }

    private void handleUIBindings(View v){
        this.assignAccountName(v);
        assignClickHandlers(v);
    }

    private void assignAccountName(View v){
        this.textViewAccountName = (TextView) v.findViewById(R.id.accountFragment_textviewAccountName);
        this.textViewAccountName.setText(AuthService.getInstance().getCurrentUser().getEmail());
    }

    private void assignClickHandlers(View v){
        Button deleteButton = (Button) v.findViewById(R.id.accountFragment_buttonDeleteUser);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteAccountPressed();
            }
        });

        Button signOutButton = (Button) v.findViewById(R.id.accountFragment_buttonSignOut);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignOutPressed();
            }
        });
    }

    public void onDeleteAccountPressed(){
        AuthService.getInstance().deleteUser(getContext());
    }

    public void onSignOutPressed(){
        AuthService.getInstance().SignOut();
        UIService.getInstance().changeActivity(this.getContext(), MainActivity.class);
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
