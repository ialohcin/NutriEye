package com.example.nutrieye;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.nutrieye.databinding.ActivityNavigationScreenBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationScreen extends AppCompatActivity {

    ActivityNavigationScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check for the activeFragment extra and set it if present
        String activeFragment = getIntent().getStringExtra("activeFragment");
        if (activeFragment != null) {
            switch (activeFragment) {
                case "ProfileFragment":
                    setBottomNavigationItemChecked(R.id.profile);
                    replaceFragment(new ProfileFragment());
                    break;
                case "SearchFragment":
                    setBottomNavigationItemChecked(R.id.mealplan);
                    replaceFragment(new MealPlanFragment());
                    break;
                case "ResourcesFragment":
                    setBottomNavigationItemChecked(R.id.resources);
                    replaceFragment(new ResourcesFragment());
                    break;
                default:
                    // Default to HomeFragment if no or unrecognized activeFragment specified
                    setBottomNavigationItemChecked(R.id.home);
                    replaceFragment(new HomeFragment());
                    break;
            }
        } else {
            // Default to HomeFragment if no activeFragment specified
            setBottomNavigationItemChecked(R.id.home);
            replaceFragment(new HomeFragment());
        }

        binding.bottomNavigationView.setBackground(null);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.mealplan) {
                replaceFragment(new MealPlanFragment());
            } else if (item.getItemId() == R.id.resources) {
                replaceFragment(new ResourcesFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });

        binding.cameraFab.setOnClickListener(view -> {
            Intent intent = new Intent(NavigationScreen.this, CameraScreen.class);
            startActivity(intent);
        });
    }

    private void replaceFragment(Fragment frag){
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();
        fragTransaction.replace(R.id.frame_layout, frag);
        fragTransaction.commit();
    }

    private void setBottomNavigationItemChecked(int itemId) {
        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.getMenu().findItem(itemId).setChecked(true);
    }

}