package com.example.nutrieye;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.nutrieye.databinding.ActivityNavigationScreenBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NavigationScreen extends AppCompatActivity {
    private String userUID;
    ActivityNavigationScreenBinding binding;
    public static final String USER_UID_KEY = "userUID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNavigationScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check for the activeFragment extra and set it if present
        String activeFragment = getIntent().getStringExtra("activeFragment");

        // Retrieve userUID from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userUID = preferences.getString(USER_UID_KEY, null);

        if (userUID == null) {
            // If userUID is not available in SharedPreferences, set a default value or handle it as needed
            //userUID = getIntent().getStringExtra("USER_UID");
            userUID = "6IAG8FBcGIRL4xg89X5DmsSVE0H2";
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

//        new Handler(this.getMainLooper()).postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                BadgeDrawable badgeDrawable = binding.bottomNavigationView.getOrCreateBadge(R.id.home);
//                badgeDrawable.setVisible(true);
//                badgeDrawable.setVerticalOffset(dpToPx(NavigationScreen.this, 3));
//                badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.red));
//
//            }
//        }, 1000);
    }

    public static int dpToPx(Context context, int dp){
        Resources resources = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }

//    public void replaceOrPopFragment(Fragment frag) {
//        FragmentManager fragManager = getSupportFragmentManager();
//        Fragment existingFragment = fragManager.findFragmentById(R.id.frame_layout);
//
//        if (existingFragment != null && existingFragment.getClass().equals(frag.getClass())) {
//            // Fragment already exists, no need to replace
//            return;
//        }
//
//        FragmentTransaction fragTransaction = fragManager.beginTransaction();
//
//        if (existingFragment != null) {
//            // Hide the existing fragment instead of replacing it
//            fragTransaction.hide(existingFragment);
//        }
//
//        boolean fragmentPopped = fragManager.popBackStackImmediate(frag.getClass().getName(), 0);
//
//        if (!fragmentPopped) {
//            // Add the new fragment to the container
//            fragTransaction.add(R.id.frame_layout, frag, frag.getClass().getName());
//        } else {
//            // Show the fragment if it's already in the back stack
//            fragTransaction.show(frag);
//        }
//
//        fragTransaction.addToBackStack(frag.getClass().getName());
//        fragTransaction.commit();
//    }

    public void replaceOrPopFragment(Fragment frag, boolean triggeredBySwipeRefresh) {
        FragmentManager fragManager = getSupportFragmentManager();
        Fragment existingFragment = fragManager.findFragmentById(R.id.frame_layout);

        if (existingFragment != null && existingFragment.getClass().equals(frag.getClass())) {
            // Fragment already exists
            if (triggeredBySwipeRefresh) {
                /// If the refresh is triggered by swipe, call the refreshContent method
                if (existingFragment instanceof HomeFragment) {
                    ((HomeFragment) existingFragment).refreshContent();
                } else if(existingFragment instanceof MealPlanFragment) {
                    ((MealPlanFragment) existingFragment).refreshContent();
                }else if(existingFragment instanceof ResourcesFragment) {
                    ((ResourcesFragment) existingFragment).refreshContent();
                }else if(existingFragment instanceof ProfileFragment) {
                    ((ProfileFragment) existingFragment).refreshContent();
                }
            }
            return;
        }

        FragmentTransaction fragTransaction = fragManager.beginTransaction();

        if (existingFragment != null) {
            // Hide the existing fragment instead of replacing it
            fragTransaction.hide(existingFragment);
        }

        boolean fragmentPopped = fragManager.popBackStackImmediate(frag.getClass().getName(), 0);

        if (!fragmentPopped) {
            // Add the new fragment to the container
            fragTransaction.add(R.id.frame_layout, frag, frag.getClass().getName());
        } else {
            // Show the fragment if it's already in the back stack
            fragTransaction.show(frag);
        }

        fragTransaction.addToBackStack(frag.getClass().getName());
        fragTransaction.commit();
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
        super.onDestroy();
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        preferences.edit().remove(USER_UID_KEY).apply(); // Clear the userUID from SharedPreferences
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackEntryCount = fragmentManager.getBackStackEntryCount();

        if (backStackEntryCount > 1) {
            // If there are fragments in the backstack (more than one), pop the top fragment
            fragmentManager.popBackStack();
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
    }


    private void logExitAppActivity() {
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
    }

    private void clearUserUIDfromSharedPreferences() {
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
    }
}