package com.feiyang.elocker.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.feiyang.elocker.Constant;
import com.feiyang.elocker.R;

public class FragmentFeedback extends Fragment {

    private EditText mFeedbackContent;

    public FragmentFeedback() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_feedback, container, false);
        mFeedbackContent = view.findViewById(R.id.feedback_content);
        Button submitBtn = view.findViewById(R.id.feedback_submit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailContent = mFeedbackContent.getText().toString();
                if (emailContent != null) {
                    Context context = view.getContext();
                    SharedPreferences sp = context.getSharedPreferences(Constant.PROPERTY_FILE_NAME, Context.MODE_PRIVATE);
                    String phoneNum = sp.getString("phoneNum", "Unknow");
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setData(Uri.parse("mailto:944867649@qq.com"));
                    email.putExtra(Intent.EXTRA_SUBJECT, "Feedback From " + phoneNum);
                    email.putExtra(Intent.EXTRA_TEXT, emailContent);
                    startActivity(email);
                }
            }
        });
        return view;
    }

}
