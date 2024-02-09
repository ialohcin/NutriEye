package com.example.nutrieye;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.uncopt.android.widget.text.justify.JustifiedTextView;


public class ResourcesFragment extends Fragment {
    NavigationScreen navigationScreen;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resources, container, false);

        navigationScreen = (NavigationScreen) getActivity();

        JustifiedTextView philfctText = view.findViewById(R.id.philFCTdesc);
        JustifiedTextView pdriText = view.findViewById(R.id.pdriDesc);
        TextView linkPhilFCT = view.findViewById(R.id.linkPhilFCT);
        TextView linkPDRI = view.findViewById(R.id.linkPDRI);

        philfctText.setText(getString(R.string.philfct_description));
        pdriText.setText(getString(R.string.pdri_description));

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

    public void refreshContent() {

    }
}