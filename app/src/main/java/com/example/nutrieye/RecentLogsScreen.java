package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RecentLogsScreen extends AppCompatActivity {
    RecyclerView recyclerView;
    String userUID;
    List<RecentLogs> logsList;
    RecentLogsAdapter logsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_logs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        initComponents();

        loadUserData();

    }

    public void initComponents() {
        recyclerView = findViewById(R.id.recyclerViewRecentLogs);
        recyclerView.setHasFixedSize(true);
        BottomMarginPercentageDecoration marginDecoration = new BottomMarginPercentageDecoration(this, 0.06f);
        recyclerView.addItemDecoration(marginDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        logsList = new ArrayList<>();
        logsAdapter = new RecentLogsAdapter(logsList);
        recyclerView.setAdapter(logsAdapter);
    }

    private void loadUserData() {
        try {
            SharedPreferences preferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            userUID = preferences.getString(USER_UID_KEY, null);
            if (userUID != null) {
                fetchActivityLogsDataFromFirebase();
            } else {
                // Handle case where userUID is not available
                Toast.makeText(RecentLogsScreen.this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle any exception that occurs during data loading
            e.printStackTrace();
            Toast.makeText(RecentLogsScreen.this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate back to NavigationScreen with ProfileFragment active
            navigateToProfileFragment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToProfileFragment();
    }

    private void navigateToProfileFragment() {
        Intent intent = new Intent(this, NavigationScreen.class);
        intent.putExtra("activeFragment", "ProfileFragment");
        startActivity(intent);
        finish(); // Finish the RecentLogsScreen activity
    }

    private void fetchActivityLogsDataFromFirebase() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
        String currentDate = dateFormat.format(calendar.getTime());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
        DatabaseReference activityLogsRef = userRef.child("ActivityLogs").child(currentDate);

        final String[] userProfilePhoto = new String[1];
        DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID).child("Profile");

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot profileSnapshot) {
                try {
                    if (profileSnapshot.exists()) {
                        userProfilePhoto[0] = profileSnapshot.child("profilePhoto").getValue(String.class);
                        // Assuming 'sex' is a property in the profile data
                        String sex = profileSnapshot.child("sex").getValue(String.class);

                        if (userProfilePhoto[0] == null || userProfilePhoto[0].isEmpty() || userProfilePhoto[0].equals("null")) {
                            // If the profile photo URL is empty or null, assign the default image based on gender
                            int defaultImageResource = "male".equalsIgnoreCase(sex) ? R.drawable.malepic : R.drawable.femalepic;
                            userProfilePhoto[0] = Integer.toString(defaultImageResource);
                        }

                        // Update existing items in logsList with the new userProfilePhoto
                        for (RecentLogs recentLog : logsList) {
                            recentLog.setLogsIcon(userProfilePhoto[0]);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                activityLogsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            logsList.clear(); // Clear the existing list

                            for (DataSnapshot logSnapshot : dataSnapshot.getChildren()) {
                                // Extract data and create RecentLogs objects for each log entry
                                String action = logSnapshot.child("action").getValue(String.class);
                                String category = logSnapshot.child("category").getValue(String.class);
                                String timestamp = logSnapshot.child("timestamp").getValue(String.class);

                                // Create the RecentLogs object
                                RecentLogs recentLog = new RecentLogs(userProfilePhoto[0], action, category, timestamp);
                                logsList.add(recentLog);
                            }

                            // Update the RecyclerView with the fetched data
                            updateRecyclerView(logsList);
                        } catch (Exception e) {
                           e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors in fetching activity logs
                        Toast.makeText(RecentLogsScreen.this, "Error fetching activity logs: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors in fetching profile data
                Toast.makeText(RecentLogsScreen.this, "Error fetching profile data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerView(List<RecentLogs> logsList) {
        logsAdapter.setData(logsList);
        logsAdapter.notifyDataSetChanged();
    }

}