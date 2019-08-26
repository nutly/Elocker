package com.feiyang.elocker.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.feiyang.elocker.R;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;

public class FragmentHelp extends Fragment {


    public FragmentHelp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_help, container, false);
        PDFView pdfView = view.findViewById(R.id.help_pdf_view);
        pdfView.fromAsset("specification.pdf")
                .defaultPage(1)
                .swipeVertical(true)
                .enableSwipe(true)
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        Toast.makeText(view.getContext(), page + "/" + pageCount, Toast.LENGTH_LONG).show();
                    }
                })
                .load();
        return view;
    }

}
