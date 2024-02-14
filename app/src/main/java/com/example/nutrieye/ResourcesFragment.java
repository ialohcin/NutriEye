package com.example.nutrieye;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.uncopt.android.widget.text.justify.JustifiedTextView;


public class ResourcesFragment extends Fragment {
    View rootView;
    SwipeRefreshLayout swipeRefreshLayout;
    ResourcesFragment.FragmentInteractionListener interactionListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_resources, container, false);

        swipeRefreshLayout = rootView.findViewById(R.id.resources_refresh);

        if (savedInstanceState == null) {
            bindViews(rootView);
            refreshContent();
        }

        return rootView;
    }

    private void bindViews(View view) {
        JustifiedTextView philfctText = view.findViewById(R.id.philFCTdesc);
        JustifiedTextView pdriText = view.findViewById(R.id.pdriDesc);
        TextView linkPhilFCT = view.findViewById(R.id.linkPhilFCT);
        TextView linkPDRI = view.findViewById(R.id.linkPDRI);

        philfctText.setText(getString(R.string.philfct_description));
        pdriText.setText(getString(R.string.pdri_description));

        ScrollView scrollView = view.findViewById(R.id.scrollView3);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // Check if the ScrollView is at the top
                if (scrollView.getScrollY() == 0) {
                    // Enable SwipeRefreshLayout
                    swipeRefreshLayout.setEnabled(true);
                } else {
                    // Disable SwipeRefreshLayout
                    swipeRefreshLayout.setEnabled(false);
                }
            }
        });

        linkPhilFCT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink("https://i.fnri.dost.gov.ph/fct/library");
            }
        });

        linkPDRI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLink("https://www.fnri.dost.gov.ph/index.php/tools-and-standard/philippine-dietary-reference-intakes-pdri");
            }
        });
    }

    public void refreshContent() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (interactionListener != null) {
                            LayoutInflater inflater = requireActivity().getLayoutInflater();
                            View rootView = inflater.inflate(R.layout.fragment_resources, null);
                            bindViews(rootView);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public interface FragmentInteractionListener {
        void replaceOrPopFragment(Fragment fragment, boolean triggeredBySwipeRefresh);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ResourcesFragment.FragmentInteractionListener) {
            interactionListener = (ResourcesFragment.FragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentInteractionListener");
        }
    }

    private void openLink(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
