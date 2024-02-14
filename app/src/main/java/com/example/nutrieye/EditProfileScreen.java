package com.example.nutrieye;

import static com.example.nutrieye.NavigationScreen.USER_UID_KEY;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.nutrieye.databinding.ActivityEditProfileScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EditProfileScreen extends AppCompatActivity {
    public String userUID;
    private DatePickerDialog.OnDateSetListener dobSetListener;
    ActivityEditProfileScreenBinding binding;
    private boolean[] foodAllergenCheckedItems;
    private boolean[] healthConditionCheckedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.dobProfile.setEnabled(false);
        binding.emailAddProfile.setEnabled(false);
        binding.selectSexProfile.setEnabled(false);

        binding.emailAddProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateEmail(Objects.requireNonNull(binding.emailAddProfile.getText().toString().trim()));
            }
        });

        binding.passwordProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validatePassword(Objects.requireNonNull(binding.passwordProfile.getText()).toString().trim());
            }
        });

        binding.confirmPassProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateConfirmPassword(Objects.requireNonNull(binding.confirmPassProfile.getText()).toString().trim(), Objects.requireNonNull(binding.passwordProfile.getText()).toString().trim());
            }
        });

        binding.firstNameProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateFirstName(Objects.requireNonNull(binding.firstNameProfile.getText()).toString().trim());
            }
        });

        binding.lastNameProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateLastName(Objects.requireNonNull(binding.lastNameProfile.getText()).toString().trim());
            }
        });

        binding.dobProfile.setFocusable(false);
        binding.dobProfile.setOnClickListener(view -> {
            binding.dobProfile.setFocusableInTouchMode(true);
            binding.dobProfile.requestFocus();

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(binding.dobProfile.getWindowToken(), 0);

            // Create a new DatePickerDialog
            DatePickerDialog dobPickerDialog = new DatePickerDialog(
                    EditProfileScreen.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    dobSetListener,
                    year, month, day);

            // Show the DatePickerDialog
            dobPickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dobPickerDialog.show();
        });

        dobSetListener = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.YEAR, year1);
            calendar1.set(Calendar.MONTH, month1 - 1);
            calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.US);
            String date = dateFormat.format(calendar1.getTime());

            // Set the selected date to the TextView
            binding.dobProfile.setText(date);
        };

        binding.dobProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateDOB(Objects.requireNonNull(binding.dobProfile.getText()).toString().trim());
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Male", "Female"});

        binding.selectSexProfile.setAdapter(adapter);

        binding.selectSexProfile.setOnClickListener(view -> hideKeyboard());

        binding.selectSexProfile.setOnItemClickListener((parent, view, position, l) -> {
            String selectedSex = parent.getItemAtPosition(position).toString();
            validateSex(selectedSex);
        });

        binding.contactNumProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateContactNumber(Objects.requireNonNull(binding.contactNumProfile.getText()).toString().trim());
            }
        });

        binding.heightProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            public void afterTextChanged(Editable editable) {
                validateHeight(Objects.requireNonNull(binding.heightProfile.getText()).toString().trim());
            }
        });

        binding.weightProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            public void afterTextChanged(Editable editable) {
                validateWeight(Objects.requireNonNull(binding.weightProfile.getText()).toString().trim());
            }
        });

        // Define arrays for main options and descriptors
        String[] mainOptions = new String[]{"Athlete", "Very Active", "Active", "Low Active", "Inactive", "Sedentary"};
        String[] descriptors = new String[]{"Professional athlete", "Exercise 6-7 times a week", "Exercise 3-5 times a week", "Exercise 2-3 times a week", "Exercise 1-2 times a week", "Little to no exercise"};

        ArrayAdapter<String> phyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mainOptions);

        binding.phyActivityLvlProfile.setAdapter(phyAdapter);

        binding.phyActivityLvlProfile.setOnClickListener(view -> hideKeyboard());

        binding.phyActivityLvlProfile.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = mainOptions[position]; // The main option
            String descriptor = descriptors[position]; // The descriptor

            // Set helper text to the descriptor regardless of the error state
            binding.profilePhyActivityTextInput.setHelperTextEnabled(true);
            binding.profilePhyActivityTextInput.setHelperText(descriptor);

            // Get the green color from colors.xml
            int greenColor = ContextCompat.getColor(this, R.color.green);

            // Set helper text color to green
            binding.profilePhyActivityTextInput.setHelperTextColor(ColorStateList.valueOf(greenColor));

            // Validate the selected option (you can still validate the main option if needed)
            validatePhysicalActivityLevel(selectedOption);
        });

        foodAllergenCheckedItems = new boolean[]{false, false, false, false};
        healthConditionCheckedItems = new boolean[]{false, false, false, false};

        binding.foodAllergensProfile.setOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Food Allergens", foodAllergenCheckedItems);
        });
        binding.profileFoodAllergensTextInput.setEndIconOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Food Allergens", foodAllergenCheckedItems);
        });

        binding.healthConditionsProfile.setOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Health Conditions", healthConditionCheckedItems);
        });
        binding.profileHealthConditionsTextInput.setEndIconOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Health Conditions", healthConditionCheckedItems);
        });

        binding.saveChangesButton.setOnClickListener(view -> {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }

            try {
                String emailStr = Objects.requireNonNull(binding.emailAddProfile.getText()).toString().trim();
                String passwordStr = Objects.requireNonNull(binding.passwordProfile.getText()).toString().trim();
                String confirmPasswordStr = Objects.requireNonNull(binding.confirmPassProfile.getText()).toString().trim();
                String firstNameStr = Objects.requireNonNull(binding.firstNameProfile.getText()).toString().trim();
                String lastNameStr = Objects.requireNonNull(binding.lastNameProfile.getText()).toString().trim();
                String dobStr = Objects.requireNonNull(binding.dobProfile.getText()).toString().trim();
                String selectedSex = binding.selectSexProfile.getText().toString().trim();
                String prefix = binding.profileContactNumTextInput.getPrefixText().toString().trim();
                String contactNumberStr = Objects.requireNonNull(binding.contactNumProfile.getText()).toString().trim();
                String selectedPhysicalActivityLevel = binding.phyActivityLvlProfile.getText().toString().trim();
                String healthConditionStr = binding.healthConditionsProfile.getText().toString().trim();
                String foodAllergensStr = binding.foodAllergensProfile.getText().toString().trim();
                String heightStr = Objects.requireNonNull(binding.heightProfile.getText()).toString().trim();
                String weightStr = Objects.requireNonNull(binding.weightProfile.getText()).toString().trim();

                // Set default values if the strings are empty
                if (healthConditionStr.isEmpty()) {
                    healthConditionStr = ""; // Setting default value for health conditions
                }

                if (foodAllergensStr.isEmpty()) {
                    foodAllergensStr = ""; // Setting default value for food allergens
                }

                boolean hasError = false;

                if (!validateEmail(emailStr)) hasError = true;
                if (!validatePassword(passwordStr)) hasError = true;
                if (!validateConfirmPassword(confirmPasswordStr, passwordStr)) hasError = true;
                if (!validateFirstName(firstNameStr)) hasError = true;
                if (!validateLastName(lastNameStr)) hasError = true;
                if (!validateDOB(dobStr)) hasError = true;
                if (!validateSex(selectedSex)) hasError = true;
                if (!validateContactNumber(contactNumberStr)) hasError = true;
                if (!validatePhysicalActivityLevel(selectedPhysicalActivityLevel)) hasError = true;
                if (!validateHeight(heightStr)) hasError = true;
                if (!validateWeight(weightStr)) hasError = true;

                if (hasError) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EditProfileScreen.this);
                    alertDialog.setTitle("Validation Error");
                    alertDialog.setMessage("Please check the form for errors and complete all fields.");
                    alertDialog.setPositiveButton("OK", (dialogInterface, option) -> dialogInterface.dismiss());
                    alertDialog.show();
                } else {
                    // Convert to Double only if not empty
                    Double height = Double.valueOf(heightStr);
                    Double weight = Double.valueOf(weightStr);
                    String finalFoodAllergensStr = foodAllergensStr;
                    String finalHealthConditionStr = healthConditionStr;
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Firebase integration for user details in Realtime Database
                        FirebaseDatabase db = FirebaseDatabase.getInstance();
                        DatabaseReference reference = db.getReference("Users").child(user.getUid()).child("Profile");

                        // Only modify the fields that are allowed to be updated
                        HashMap<String, Object> updatedUserData = new HashMap<>();
                        updatedUserData.put("firstName", firstNameStr);
                        updatedUserData.put("lastName", lastNameStr);
                        updatedUserData.put("password", passwordStr);
                        updatedUserData.put("contactNum", prefix + contactNumberStr);
                        updatedUserData.put("confirmPass", confirmPasswordStr);
                        updatedUserData.put("foodAllergens", finalFoodAllergensStr);
                        updatedUserData.put("healthConditions", finalHealthConditionStr);
                        updatedUserData.put("height", calculateWeightHeightValue(height));
                        updatedUserData.put("weight", calculateWeightHeightValue(weight));
                        updatedUserData.put("phyActivity", selectedPhysicalActivityLevel);

                        // If password is changed, update it
                        if (passwordStr.equals(confirmPasswordStr)) {
                            updateFirebaseAuthenticationPassword(passwordStr, user.getUid());
                        }

                        reference.updateChildren(updatedUserData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    handleProfileUpdateSuccess();
                                } else {
                                    if (task.getException() != null) {
                                        throw new RuntimeException("Error updating profile: " + task.getException().getMessage(), task.getException());
                                    } else {
                                        throw new RuntimeException("Unknown error occurred while updating profile.");
                                    }
                                }
                            }
                        });
                    }
                }
            } catch (Exception e) {
                // Handle any exception that occurs during profile update
                e.printStackTrace();
                Toast.makeText(EditProfileScreen.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        loadUserData();

    }

    private void loadUserData() {
        try {
            SharedPreferences preferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            userUID = preferences.getString(USER_UID_KEY, null);
            if (userUID != null) {
                fetchUserDataFromFirebase();
            } else {
                // Handle case where userUID is not available
                Toast.makeText(EditProfileScreen.this, "User data not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Handle any exception that occurs during data loading
            e.printStackTrace();
            Toast.makeText(EditProfileScreen.this, "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void updateFirebaseAuthenticationPassword(String newPassword, String userUID) {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Authentication password update successful, now update Realtime Database
                            updateFirebaseRealtimeDatabasePassword(userUID, newPassword);

                            // Clear Remember Me
                            clearUserUIDfromSharedPreferences();

                        } else {
                            // Handle failure
                            Toast.makeText(EditProfileScreen.this, "Failed to update password in Authentication", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            // Handle any exception that occurs during password update
            e.printStackTrace();
            Toast.makeText(EditProfileScreen.this, "Error updating authentication password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFirebaseRealtimeDatabasePassword(String userUID, String newPassword) {
        try {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userUID).child("Profile");
            userRef.child("password").setValue(newPassword);
            userRef.child("confirmPass").setValue(newPassword);
        } catch (Exception e) {
            // Handle any exception that occurs during database update
            e.printStackTrace();
            Toast.makeText(EditProfileScreen.this, "Error updating password in Realtime Database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchUserDataFromFirebase() {
        try {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(userUID).child("Profile");

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String sex = snapshot.child("sex").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);
                        String confirmPassword = snapshot.child("confirmPass").getValue(String.class);
                        String dob = snapshot.child("dob").getValue(String.class);
                        String contactNumber = snapshot.child("contactNum").getValue(String.class);
                        Double weight = snapshot.child("weight").getValue(Double.class);
                        Double height = snapshot.child("height").getValue(Double.class);
                        String foodAllergens = snapshot.child("foodAllergens").getValue(String.class);
                        String healthConditions = snapshot.child("healthConditions").getValue(String.class);
                        String phyActivity = snapshot.child("phyActivity").getValue(String.class);

                        // Update UI with fetched data
                        updateUIWithData(firstName, lastName, sex, email, password, confirmPassword, dob,
                                contactNumber, weight, height, foodAllergens, healthConditions, phyActivity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database cancellation error
                    Toast.makeText(EditProfileScreen.this, "Database operation cancelled", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            Toast.makeText(EditProfileScreen.this, "Error fetching user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUIWithData(String firstName, String lastName, String sex, String email, String password,
                                  String confirmPassword, String dob, String contactNumber, Double weight, Double height,
                                  String foodAllergens, String healthConditions, String phyActivity) {
        // Update UI with fetched data
        binding.emailAddProfile.setText(email);
        binding.passwordProfile.setText(password);
        binding.confirmPassProfile.setText(confirmPassword);

        binding.firstNameProfile.setText(firstName);
        binding.lastNameProfile.setText(lastName);
        binding.dobProfile.setText(dob);
        binding.selectSexProfile.setText(sex);
        if (contactNumber != null && contactNumber.startsWith("+63")) {
            contactNumber = contactNumber.substring(3); // Remove "+63"
        }
        binding.contactNumProfile.setText(contactNumber);
        binding.weightProfile.setText(String.valueOf(weight));
        binding.heightProfile.setText(String.valueOf(height));

        binding.phyActivityLvlProfile.setText(phyActivity);
        // Define arrays for main options and descriptors
        String[] mainOptions = new String[]{"Athlete", "Very Active", "Active", "Low Active", "Inactive", "Sedentary"};

        // Set the helper text for the initial value
        setHelperTextForPhyActivity(phyActivity);

        ArrayAdapter<String> phyAdapter = new ArrayAdapter<>(EditProfileScreen.this, android.R.layout.simple_spinner_dropdown_item, mainOptions);

        binding.phyActivityLvlProfile.setAdapter(phyAdapter);

        binding.phyActivityLvlProfile.setOnClickListener(view -> hideKeyboard());

        binding.phyActivityLvlProfile.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = mainOptions[position]; // The main option

            // Set the helper text for the selected option
            setHelperTextForPhyActivity(selectedOption);

            // Validate the selected option
            validatePhysicalActivityLevel(selectedOption);
        });

        binding.foodAllergensProfile.setText(foodAllergens);
        binding.healthConditionsProfile.setText(healthConditions);
    }

    private void setHelperTextForPhyActivity(String selectedOption) {
        // Define arrays for main options and descriptors
        String[] mainOptions = new String[]{"Athlete", "Very Active", "Active", "Low Active", "Inactive", "Sedentary"};
        String[] descriptors = new String[]{"Professional athlete", "Exercise 6-7 times a week", "Exercise 3-5 times a week", "Exercise 2-3 times a week", "Exercise 1-2 times a week", "Little to no exercise"};

        // Find the position of the selected option
        int position = Arrays.asList(mainOptions).indexOf(selectedOption);

        // Set the descriptor text
        if (position >= 0 && position < descriptors.length) {
            String descriptor = descriptors[position];
            // Set helper text to the descriptor regardless of the error state
            binding.profilePhyActivityTextInput.setHelperTextEnabled(true);
            binding.profilePhyActivityTextInput.setHelperText(descriptor);

            // Get the green color from colors.xml
            int greenColor = ContextCompat.getColor(EditProfileScreen.this, R.color.green);

            // Set helper text color to green
            binding.profilePhyActivityTextInput.setHelperTextColor(ColorStateList.valueOf(greenColor));
        }
    }

    private void clearUserUIDfromSharedPreferences() {
        try {
            // Clear userUID from SharedPreferences
            SharedPreferences preferences = EditProfileScreen.this.getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
//            editor.remove(USER_UID_KEY); // USER_UID_KEY is the key used to store userUID
//            editor.apply();

            // Clear "Remember Me" preferences
            editor.remove("isUserRemembered");
            editor.remove("savedEmail");
            editor.remove("savedPassword");
            editor.apply();
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            Toast.makeText(EditProfileScreen.this, "Error clearing SharedPreferences: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private double calculateWeightHeightValue(double value) {
        // Rounds to one decimal place
        return Math.round(value * 10.0) / 10.0;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showDiscardChangesDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void handleProfileUpdateSuccess() {
        try {
            Toast.makeText(EditProfileScreen.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy", Locale.US);
            String currentDate = dateFormat.format(calendar.getTime());

            // Create ActivityLogs structure
            DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
            DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

            // Get current time
            String currentTime = new SimpleDateFormat("hh:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());

            // Generate a unique ID for the log entry
            String logID = "LogID_" + System.currentTimeMillis();

            // Create the log entry structure for the activity logs
            DatabaseReference logEntryRef = activityLogsRef.child(currentDate).child(logID);
            logEntryRef.child("action").setValue("Updated Profile Details");
            logEntryRef.child("category").setValue("Profile");
            logEntryRef.child("timestamp").setValue(currentDate + "\n" + currentTime);

            navigateToProfileFragment();
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
            Toast.makeText(EditProfileScreen.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showDiscardChangesDialog();
    }

    private void showDiscardChangesDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Discard Changes");
        alertDialogBuilder.setMessage("Do you want to discard the changes?");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                navigateToProfileFragment();
                EditProfileScreen.super.onBackPressed();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do nothing, just close the dialog
            }
        });
        alertDialogBuilder.show();
    }

    private void navigateToProfileFragment() {
        Intent intent = new Intent(EditProfileScreen.this, NavigationScreen.class);
        intent.putExtra("activeFragment", "ProfileFragment");
        startActivity(intent);
        finish(); // Finish the EditProfileScreen activity
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void showMultiChoiceDialog(final String title, boolean[] checkedItems) {
        final String[] items;
        if (title.equals("Select Food Allergens")) {
            items = new String[]{"Peanuts", "Dairy", "Shellfish", "Wheat"};
        } else if (title.equals("Select Health Conditions")) {
            // Check if gender is Male to modify the health conditions
            if (binding.selectSexProfile.getText().toString().trim().equals("Male")) {
                items = new String[]{"Kidney Disease", "Diabetes", "Hypertension", "Heart Disease"};
            } else {
                items = new String[]{"Pregnancy", "Lactating", "Kidney Disease", "Diabetes", "Hypertension", "Heart Disease"};
            }
        } else {
            items = new String[0];
        }
        final boolean[] initialCheckedState = Arrays.copyOf(checkedItems, checkedItems.length);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(title);

        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> checkedItems[which] = isChecked);

        builder.setPositiveButton("OK", (dialog, which) -> updateSelectedItems(title, items, checkedItems));
        builder.setNegativeButton("Cancel", (dialog, which) -> System.arraycopy(initialCheckedState, 0, checkedItems, 0, initialCheckedState.length));

        builder.setNeutralButton("Clear All", (dialog, which) -> {
            Arrays.fill(checkedItems, false);
            if (title.equals("Select Food Allergens")) {
                binding.foodAllergensProfile.setText("");
            } else if (title.equals("Select Health Conditions")) {
                binding.healthConditionsProfile.setText("");
            }
        });

        builder.show();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void updateSelectedItems(String title, String[] items, boolean[] checkedItems) {
        StringBuilder selectedItemsText = new StringBuilder();
        int maxItemsToShow = 2;
        int selectedItemCount = 0;

        for (int i = 0; i < checkedItems.length; i++) {
            if (checkedItems[i]) {
                if (selectedItemCount < maxItemsToShow) {
                    if (selectedItemsText.length() > 0) {
                        selectedItemsText.append(", ");
                    }
                    selectedItemsText.append(items[i]);
                } else if (selectedItemCount == maxItemsToShow) {
                    selectedItemsText.append("...");
                    break;
                }
                selectedItemCount++;
            }
        }

        if (title.equals("Select Food Allergens")) {
            binding.foodAllergensProfile.setText(selectedItemsText.toString());
        } else if (title.equals("Select Health Conditions")) {
            binding.healthConditionsProfile.setText(selectedItemsText.toString());
        }
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            binding.profileEmailTextInput.setError("Email is required");
            return false;
        } else if (!isValidEmail(email)) {
            binding.profileEmailTextInput.setError("Invalid email format");
            return false;
        } else {
            binding.profileEmailTextInput.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            binding.profilePasswordTextInput.setError("Password is required");
            return false;
        } else if (password.length() < 8) {
            binding.profilePasswordTextInput.setError("Password must be at least 8 characters long");
            return false;
        } else {
            binding.profilePasswordTextInput.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(String confirmPassword, String password) {
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.profileConfirmPassTextInput.setError("Confirm password is required");
            return false;
        } else if (!confirmPassword.equals(password)) {
            binding.profileConfirmPassTextInput.setError("Passwords do not match");
            return false;
        } else {
            binding.profileConfirmPassTextInput.setError(null);
            return true;
        }
    }

    private boolean validateFirstName(String firstName) {
        if (TextUtils.isEmpty(firstName)) {
            binding.profileFirstNameTextInput.setError("First name is required");
            return false;
        } else {
            binding.profileFirstNameTextInput.setError(null);
            return true;
        }
    }

    private boolean validateLastName(String lastName) {
        if (TextUtils.isEmpty(lastName)) {
            binding.profileLastNameTextInput.setError("Last name is required");
            return false;
        } else {
            binding.profileLastNameTextInput.setError(null);
            return true;
        }
    }

    private boolean validateDOB(String dobStr) {
        if (TextUtils.isEmpty(dobStr)) {
            binding.profileDobTextInput.setError("Date of birth is required");
            return false;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            dateFormat.setLenient(false);

            try {
                Date dobDate = dateFormat.parse(dobStr);

                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(Objects.requireNonNull(dobDate));
                Calendar minAgeCalendar = Calendar.getInstance();
                minAgeCalendar.add(Calendar.YEAR, -99);
                Calendar maxAgeCalendar = Calendar.getInstance();
                maxAgeCalendar.add(Calendar.YEAR, -18);

                if (dobCalendar.before(minAgeCalendar) || dobCalendar.after(maxAgeCalendar)) {
                    binding.profileDobTextInput.setError("DOB within 18 to 99 years");
                    return false;
                } else {
                    binding.profileDobTextInput.setError(null);
                    return true;
                }
            } catch (ParseException e) {
                binding.profileDobTextInput.setError("Invalid DOB format");
                return false;
            }
        }
    }

    private boolean validateSex(String selectedSex) {
        if (TextUtils.isEmpty(selectedSex)) {
            binding.profileSexTextInput.setError("Please select a sex");
            return false;
        } else if (!isValidSex(selectedSex)) {
            binding.profileSexTextInput.setError("Invalid sex");
            return false;
        } else {
            binding.profileSexTextInput.setError(null);
            return true;
        }
    }

    private boolean isValidSex(String selectedSex) {
        List<String> validOptions = Arrays.asList("Male", "Female", "Non-binary");
        return validOptions.contains(selectedSex);
    }

    private boolean validateContactNumber(String contactNumber) {
        if (TextUtils.isEmpty(contactNumber)) {
            binding.profileContactNumTextInput.setError("Contact number is required");
            return false;
        } else if (!isValidContactNumber(contactNumber)) {
            binding.profileContactNumTextInput.setError("Invalid contact number format");
            return false;
        } else {
            binding.profileContactNumTextInput.setError(null);
            return true;
        }
    }

    private boolean isValidContactNumber(String contactNumber) {
        // Ensure the contact number has exactly 10 digits, disregarding the prefix
        String expectedFullContactNumber = "^[0-9]{10}$";

        if (!contactNumber.matches(expectedFullContactNumber)) {
            binding.profileContactNumTextInput.setError("Invalid contact number format");
            return false;
        } else {
            binding.profileContactNumTextInput.setError(null);
            return true;
        }
    }

    private boolean validatePhysicalActivityLevel(String selectedOption) {
        if (TextUtils.isEmpty(selectedOption)) {
            binding.profilePhyActivityTextInput.setError("Please select a physical activity level");
            return false;
        } else if (!isValidPhysicalActivityLevel(selectedOption)) {
            binding.profilePhyActivityTextInput.setError("Invalid physical activity level");
            return false;
        } else {
            binding.profilePhyActivityTextInput.setError(null);
            return true;
        }
    }

    private boolean isValidPhysicalActivityLevel(String selectedOption) {
        List<String> validOptions = Arrays.asList("Athlete", "Very Active", "Active", "Low Active", "Inactive", "Sedentary");
        return validOptions.contains(selectedOption);
    }

    private boolean validateHeight(String heightStr) {
        if (TextUtils.isEmpty(heightStr)) {
            binding.profileHeightTextInput.setError("Height is required");
            return false;
        } else {
            binding.profileHeightTextInput.setError(null);
            return true;
        }
    }

    private boolean validateWeight(String weightStr) {
        if (TextUtils.isEmpty(weightStr)) {
            binding.profileWeightTextInput.setError("Weight is required");
            return false;
        } else {
            binding.profileWeightTextInput.setError(null);
            return true;
        }
    }
}