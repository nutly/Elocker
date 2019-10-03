package com.feiyang.elocker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;

public class FragmentAbout extends Fragment {


    public FragmentAbout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        TextView versionTv = view.findViewById(R.id.version);
        versionTv.setText(Constant.APPVERSION);
        return view;
    }

}
