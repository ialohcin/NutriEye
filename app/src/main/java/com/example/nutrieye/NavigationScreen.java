package com.example.nutrieye;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nutrieye.databinding.ActivityNavigationScreenBinding;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NavigationScreen extends AppCompatActivity implements HomeFragment.FragmentInteractionListener,
        ProfileFragment.FragmentInteractionListener, MealPlanFragment.FragmentInteractionListener,
        ResourcesFragment.FragmentInteractionListener {
    private String userUID;
    static ActivityNavigationScreenBinding binding;
    public static final String USER_UID_KEY = "userUID";

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Check for the activeFragment extra and set it if present
        String activeFragment = getIntent().getStringExtra("activeFragment");

        // Retrieve userUID from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);

        if (userUID == null) {
            // If userUID is not available in SharedPreferences, set a default value or handle it as needed
            userUID = getIntent().getStringExtra("USER_UID");
            preferences.edit().putString(USER_UID_KEY, userUID).apply(); // Store the default userUID
        }

        if (activeFragment != null) {
            switch (activeFragment) {
                case "ProfileFragment":
                    setBottomNavigationItemChecked(R.id.profile);
                    replaceOrPopFragment(new ProfileFragment(), false);

                    break;
                case "MealPlanFragment":
                    setBottomNavigationItemChecked(R.id.mealplan);
                    replaceOrPopFragment(new MealPlanFragment(), false);
                    break;
                case "ResourcesFragment":
                    setBottomNavigationItemChecked(R.id.resources);
                    replaceOrPopFragment(new ResourcesFragment(), false);
                    break;
                default:
                    // Default to HomeFragment if no or unrecognized activeFragment specified
                    setBottomNavigationItemChecked(R.id.home);
                    replaceOrPopFragment(new HomeFragment(), false);
                    break;
            }
        } else {
            setBottomNavigationItemChecked(R.id.home);
            replaceOrPopFragment(new HomeFragment(), false);
        }

        if (savedInstanceState != null) {
            return;
        }

        binding.bottomNavigationView.setBackground(null);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home) {
                replaceOrPopFragment(new HomeFragment(), false);
            } else if (item.getItemId() == R.id.mealplan) {
                replaceOrPopFragment(new MealPlanFragment(), false);
            } else if (item.getItemId() == R.id.resources) {
                replaceOrPopFragment(new ResourcesFragment(), false);
            } else if (item.getItemId() == R.id.profile) {
                replaceOrPopFragment(new ProfileFragment(), false);
            }
            return true;
        });

        // Add listener to handle back stack changes
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                updateBottomNavigationItem();
            }
        });

        binding.cameraFab.setOnClickListener(view -> {
            Intent intent = new Intent(NavigationScreen.this, CameraScreen.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save any relevant data or state into the outState bundle
        outState.putString("userUID", userUID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore any relevant data or state from the savedInstanceState bundle
        userUID = savedInstanceState.getString("userUID");
    }

    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }

    public void updateBadgeVisibility(boolean isVisible, String fragmentTag) {
        int menuItemId = 0; // Default value, change based on fragment
        switch (fragmentTag) {
            case "HomeFragment":
                menuItemId = R.id.home;
                break;
            case "MealPlanFragment":
                menuItemId = R.id.mealplan;
                break;
            case "ResourcesFragment":
                menuItemId = R.id.resources;
                break;
            case "ProfileFragment":
                menuItemId = R.id.profile;
                break;
        }

        BadgeDrawable badgeDrawable = binding.bottomNavigationView.getOrCreateBadge(menuItemId);
        badgeDrawable.setVisible(isVisible);
        badgeDrawable.setVerticalOffset(dpToPx(this, 3));
        badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.red));
    }

    public void replaceOrPopFragment(Fragment frag, boolean triggeredBySwipeRefresh) {
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragTransaction = fragManager.beginTransaction();

        if (triggeredBySwipeRefresh) {
            // Refresh the content of the current fragment without popping the back stack
            refreshFragmentContent(frag);
        } else {
            // Check if the fragment already exists in the FragmentManager
            Fragment existingFragment = fragManager.findFragmentByTag(frag.getClass().getName());

            if (existingFragment == null) {
                // Fragment is not in the FragmentManager, add it
                fragTransaction.add(R.id.frame_layout, frag, frag.getClass().getName());
            } else {
                // Fragment already exists, show it
                fragTransaction.show(existingFragment);
            }

            // Hide all other fragments
            for (Fragment fragment : fragManager.getFragments()) {
                if (fragment != existingFragment) {
                    fragTransaction.hide(fragment);
                }
            }

            // Add transaction to back stack if it's a new fragment
            if (existingFragment == null) {
                fragTransaction.addToBackStack(frag.getClass().getName());
            }

            fragTransaction.commit();

            // Update bottom navigation item based on the current fragment
            updateBottomNavigationItem();
        }
    }

    public void refreshFragmentContent(Fragment frag) {
        if (frag instanceof HomeFragment) {
            ((HomeFragment) frag).refreshContent();
        } else if (frag instanceof MealPlanFragment) {
            ((MealPlanFragment) frag).refreshContent();
        } else if (frag instanceof ResourcesFragment) {
            ((ResourcesFragment) frag).refreshContent();
        } else if (frag instanceof ProfileFragment) {
            ((ProfileFragment) frag).refreshContent();
        }
    }

    private void updateBottomNavigationItem() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);

        if (currentFragment instanceof HomeFragment) {
            setBottomNavigationItemChecked(R.id.home);
        } else if (currentFragment instanceof MealPlanFragment) {
            setBottomNavigationItemChecked(R.id.mealplan);
        } else if (currentFragment instanceof ResourcesFragment) {
            setBottomNavigationItemChecked(R.id.resources);
        } else if (currentFragment instanceof ProfileFragment) {
            setBottomNavigationItemChecked(R.id.profile);
        }
    }

    private void setBottomNavigationItemChecked(int itemId) {
        BottomNavigationView bottomNavigationView = binding.bottomNavigationView;
        bottomNavigationView.getMenu().findItem(itemId).setChecked(true);
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            preferences.edit().remove(USER_UID_KEY).apply(); // Clear the userUID from SharedPreferences
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error onDestroy operation: ", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();

            if (backStackEntryCount > 1) {
                // If there are fragments in the backstack (more than one), pop the top fragment
                fragmentManager.popBackStack();

                // Get the topmost fragment from the back stack
                FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(backStackEntryCount - 2);
                String topFragmentTag = backStackEntry.getName();
                Fragment topFragment = fragmentManager.findFragmentByTag(topFragmentTag);

                // Show the topmost fragment and hide all others
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                for (Fragment fragment : fragmentManager.getFragments()) {
                    if (fragment == topFragment) {
                        transaction.show(fragment);
                    } else {
                        transaction.hide(fragment);
                    }
                }
                transaction.commit();

                // Update bottom navigation item based on the current fragment
                updateBottomNavigationItem();
            } else if (backStackEntryCount == 1) {
                // If there is only one fragment left in the backstack, display the exit app dialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Exit App");
                alertDialog.setMessage("Are you sure you want to exit the app?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int option) {
                        // Log the activity here if needed
                        logExitAppActivity();

                        // Clear userUID from SharedPreferences if needed
                        clearUserUIDfromSharedPreferences();

                        // Close the app
                        finishAffinity();
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
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error onBackStack operation: " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    private void logExitAppActivity() {
        try {
            // Create ActivityLogs structure
            DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
            DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

            // Get current time
            String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());
            String currentDay = new SimpleDateFormat("MMM dd yyyy", Locale.US).format(Calendar.getInstance().getTime());

            // Generate a unique ID for the log entry
            String logID = "LogID_" + System.currentTimeMillis();

            // Create the log entry structure
            DatabaseReference logEntryRef = activityLogsRef.child(currentDay).child(logID);

            // Modify the description to indicate exiting the app
            logEntryRef.child("action").setValue("Exited App");
            logEntryRef.child("category").setValue("Application");
            logEntryRef.child("timestamp").setValue(currentTime);
        } catch (Exception e) {
            throw new RuntimeException("Error logging exit activity: " + e.getMessage(), e);
        }
    }

    private void clearUserUIDfromSharedPreferences() {
        try {
            // Clear userUID from SharedPreferences
            SharedPreferences preferences = NavigationScreen.this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(USER_UID_KEY); // USER_UID_KEY is the key used to store userUID
            editor.apply();

            // Clear "Remember Me" preferences
            editor.remove("isUserRemembered");
            editor.remove("savedEmail");
            editor.remove("savedPassword");
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error clearing SharedPreferences: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}