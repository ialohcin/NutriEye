package com.example.nutrieye;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static androidx.core.app.ActivityCompat.finishAffinity;


public class ProfileFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        AppCompatButton logout = view.findViewById(R.id.logoutButton);
        AppCompatButton editProfie = view.findViewById(R.id.editProfileButton);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Logging Out");
                alertDialog.setMessage("Are you sure you want to logout?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int option) {
                        Intent intent = new Intent(getActivity(), LoginScreen.class);
                        startActivity(intent);
                        if (getActivity() != null) {
                            getActivity().finishAffinity();
                        }
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int option) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();
            }
        });

        editProfie.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), EditProfileScreen.class);
            startActivity(intent);
            //To be modified
            if (getActivity() != null) {
                getActivity().finishAffinity();
            }
        });

        return view;

    }
}