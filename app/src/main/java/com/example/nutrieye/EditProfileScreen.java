package com.example.nutrieye;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.regex.Pattern;

public class EditProfileScreen extends AppCompatActivity {
    public String userUID;
    private SharedPreferences sharedPreferences;

    ActivityEditProfileScreenBinding binding;
    private boolean[] foodAllergenCheckedItems;
    private boolean[] healthConditionCheckedItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userUID = getIntent().getStringExtra("USER_UID");

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.dobProfile.setEnabled(false);
        binding.emailAddProfile.setEnabled(false);
        binding.selectSexProfile.setEnabled(false);

        binding.emailAddProfile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) { validateEmail(Objects.requireNonNull(binding.emailAddProfile.getText().toString().trim()));}
        });

        binding.passwordProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validatePassword(Objects.requireNonNull(binding.passwordProfile.getText()).toString().trim()); }
        });

        binding.confirmPassProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateConfirmPassword(Objects.requireNonNull(binding.confirmPassProfile.getText()).toString().trim(), Objects.requireNonNull(binding.passwordProfile.getText()).toString().trim()); }
        });

        binding.firstNameProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateFirstName(Objects.requireNonNull(binding.firstNameProfile.getText()).toString().trim()); }
        });

        binding.lastNameProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateLastName(Objects.requireNonNull(binding.lastNameProfile.getText()).toString().trim()); }
        });

        binding.dobProfile.setFocusable(false);
        binding.dobProfile.setOnClickListener(view -> {
            binding.dobProfile.setFocusableInTouchMode(true);
            binding.dobProfile.requestFocus();

            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(binding.dobProfile.getWindowToken(), 0);

            DatePickerDialog dobPickerDialog = new DatePickerDialog(EditProfileScreen.this, (datePicker, year1, month1, dayOfMonth) -> {
                month1 = month1 + 1;
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year1);
                calendar1.set(Calendar.MONTH, month1 - 1);
                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.US);
                String date = dateFormat.format(calendar1.getTime());
                binding.dobProfile.setText(date);
            },year,month - 1,day);


            dobPickerDialog.show();
        });

        binding.dobProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateDOB(Objects.requireNonNull(binding.dobProfile.getText()).toString().trim()); }
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
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateContactNumber(Objects.requireNonNull(binding.contactNumProfile.getText()).toString().trim()); }
        });

        binding.heightProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            public void afterTextChanged(Editable editable) { validateHeight(Objects.requireNonNull(binding.heightProfile.getText()).toString().trim()); }
        });

        binding.weightProfile.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            public void afterTextChanged(Editable editable) { validateWeight(Objects.requireNonNull(binding.weightProfile.getText()).toString().trim()); }
        });

        ArrayAdapter<String> phyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.physical_activity_levels)); // R.array.physical_activity_levels should be the array resource for activity levels in your strings.xml file
        binding.phyActivityLvlProfile.setAdapter(phyAdapter);


        binding.phyActivityLvlProfile.setOnClickListener(view -> hideKeyboard());

        binding.phyActivityLvlProfile.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = parent.getItemAtPosition(position).toString();
            validatePhysicalActivityLevel(selectedOption);
        });

        foodAllergenCheckedItems = new boolean[]{false, false, false, false};
        healthConditionCheckedItems = new boolean[]{false, false, false, false};

        binding.foodAllergensProfile.setOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog(  "Select Food Allergens", foodAllergenCheckedItems);
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

            String emailStr = Objects.requireNonNull(binding.emailAddProfile.getText()).toString().trim();
            String passwordStr = Objects.requireNonNull(binding.passwordProfile.getText()).toString().trim();
            String confirmPasswordStr = Objects.requireNonNull(binding.confirmPassProfile.getText()).toString().trim();
            String firstNameStr = Objects.requireNonNull(binding.firstNameProfile.getText()).toString().trim();
            String lastNameStr = Objects.requireNonNull(binding.lastNameProfile.getText()).toString().trim();
            String dobStr = Objects.requireNonNull(binding.dobProfile.getText()).toString().trim();
            String selectedSex = binding.selectSexProfile.getText().toString().trim();
            String contactNumberStr = Objects.requireNonNull(binding.contactNumProfile.getText()).toString().trim();
            String selectedPhysicalActivityLevel = binding.phyActivityLvlProfile.getText().toString().trim();
            String healthConditionStr = binding.healthConditionsProfile.getText().toString().trim();
            String foodAllergensStr = binding.foodAllergensProfile.getText().toString().trim();
            String heightStr = Objects.requireNonNull(binding.heightProfile.getText()).toString().trim();
            String weightStr = Objects.requireNonNull(binding.weightProfile.getText()).toString().trim();

            // Set default values if the strings are empty
            if (healthConditionStr.isEmpty()) {
                healthConditionStr = "N/A"; // Setting default value for health conditions
            }

            if (foodAllergensStr.isEmpty()) {
                foodAllergensStr = "N/A"; // Setting default value for food allergens
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
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    // Firebase integration for user details in Realtime Database
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference reference = db.getReference("Users").child(userUID).child("Profile");

                    // Only modify the fields that are allowed to be updated
                    HashMap<String, Object> updatedUserData = new HashMap<>();
                    updatedUserData.put("firstName", firstNameStr);
                    updatedUserData.put("lastName", lastNameStr);
                    updatedUserData.put("password", passwordStr);
                    updatedUserData.put("contactNum", contactNumberStr);
                    updatedUserData.put("confirmPass", confirmPasswordStr);
                    updatedUserData.put("foodAllergens", foodAllergensStr);
                    updatedUserData.put("healthConditions", healthConditionStr);
                    updatedUserData.put("height", Double.parseDouble(heightStr));
                    updatedUserData.put("weight", Double.parseDouble(weightStr));
                    updatedUserData.put("phyActivity", selectedPhysicalActivityLevel);

                    reference.updateChildren(updatedUserData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                               handleProfileUpdateSuccess();
                            } else {
                                if (task.getException() != null) {
                                    Toast.makeText(EditProfileScreen.this, "Profile Update Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditProfileScreen.this, "Profile Update Failed. Please verify your changes.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
            }
        });

        fetchUserDataFromFirebase();

    }

    private void fetchUserDataFromFirebase() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(userUID).child("Profile");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
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

                    binding.emailAddProfile.setText(email);
                    binding.passwordProfile.setText(password);
                    binding.confirmPassProfile.setText(confirmPassword);

                    binding.firstNameProfile.setText(firstName);
                    binding.lastNameProfile.setText(lastName);
                    binding.dobProfile.setText(dob);
                    binding.selectSexProfile.setText(sex);
                    binding.contactNumProfile.setText(contactNumber);
                    binding.weightProfile.setText(String.valueOf(weight));
                    binding.heightProfile.setText(String.valueOf(height));


                    binding.phyActivityLvlProfile.setText(phyActivity);
                    // Your array resource for activity levels in strings.xml
                    String[] allActivityLevels = getResources().getStringArray(R.array.physical_activity_levels);

                    // Determine the valid options based on the fetched phyActivity
                    List<String> validOptions;
                    if (phyActivity != null && phyActivity.equals("Very Active")) {
                        validOptions = Arrays.asList("Very Active");
                    } else if (phyActivity != null && phyActivity.equals("Moderately Active")) {
                        validOptions = Arrays.asList("Very Active", "Moderately Active");
                    } else if (phyActivity != null && phyActivity.equals("Lightly Active")) {
                        validOptions = Arrays.asList("Very Active", "Moderately Active", "Lightly Active");
                    } else {
                        validOptions = Arrays.asList(allActivityLevels); // Show all options if phyActivity is null or unexpected
                    }

                    ArrayAdapter<String> phyAdapter = new ArrayAdapter<>(EditProfileScreen.this, android.R.layout.simple_dropdown_item_1line, validOptions);
                    binding.phyActivityLvlProfile.setAdapter(phyAdapter);

                    binding.foodAllergensProfile.setText(foodAllergens);
                    binding.healthConditionsProfile.setText(healthConditions);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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


    private void handleProfileUpdateSuccess() {
        Toast.makeText(EditProfileScreen.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();

        navigateToProfileFragment();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToProfileFragment();
    }

    private void navigateToProfileFragment() {
        Intent intent = new Intent(EditProfileScreen.this, NavigationScreen.class);
        intent.putExtra("activeFragment","ProfileFragment");
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
                items = new String[]{"Pregnancy", "Diabetes", "Hypertension", "Heart Disease"};
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
        String pattern = "^(09)[0-9]{9}$"; // 11-digit numeric format starting with "09"
        return Pattern.matches(pattern, contactNumber);
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
        List<String> validOptions = Arrays.asList("Very Active", "Moderately Active", "Lightly Active", "Sedentary");
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