package com.example.nutrieye;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class ResourcesFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        TextView pdriDesc = view.findViewById(R.id.pdriDesc);
        TextView philfctDesc = view.findViewById(R.id.philFCTdesc);
        TextView linkPhilFCT = view.findViewById(R.id.linkPhilFCT);
        TextView linkPDRI = view.findViewById(R.id.linkPDRI);

        String pdriText = getString(R.string.pdri_description);
        SpannableStringBuilder pdriBuilder = new SpannableStringBuilder(pdriText);
        pdriBuilder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL),
                0, pdriText.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        pdriDesc.setText(pdriBuilder);

        String philfctText = getString(R.string.philfct_description);
        SpannableStringBuilder philfctBuilder = new SpannableStringBuilder(philfctText);
        philfctBuilder.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL),
                0, philfctText.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        philfctDesc.setText(philfctBuilder);

        linkPhilFCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://i.fnri.dost.gov.ph/fct/library");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        linkPDRI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.fnri.dost.gov.ph/index.php/tools-and-standard/philippine-dietary-reference-intakes-pdri");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        return view;
    }
}