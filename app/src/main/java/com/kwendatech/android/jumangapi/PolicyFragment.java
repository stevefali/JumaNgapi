package com.kwendatech.android.jumangapi;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



public class PolicyFragment extends Fragment {


    private TextView explainer;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public PolicyFragment() {
        // Required empty public constructor
    }


    public static PolicyFragment newInstance(String param1, String param2) {
        PolicyFragment fragment = new PolicyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_policy, container, false);

        explainer = (TextView) view.findViewById(R.id.explanation_textview);
        explainer.setMovementMethod(LinkMovementMethod.getInstance()); // Make the link clickable.


        // Inflate the layout for this fragment
        return view;
    }

}
