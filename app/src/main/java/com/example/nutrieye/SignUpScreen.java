package com.example.nutrieye;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.nutrieye.databinding.ActivitySignUpScreenBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class SignUpScreen extends AppCompatActivity {
    ActivitySignUpScreenBinding binding;
    ProgressDialog signUpProgress;
    private DatePickerDialog.OnDateSetListener dobSetListener;
    private boolean[] foodAllergenCheckedItems;
    private boolean[] healthConditionCheckedItems;
    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        auth = FirebaseAuth.getInstance();

        signUpProgress = new ProgressDialog(this);

        binding.personalDetails.setVisibility(View.INVISIBLE);
        binding.healthDetails.setVisibility(View.INVISIBLE);

        binding.signUpContainer.setBackgroundResource(R.drawable.cardview);

        binding.emailAddSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateEmail(Objects.requireNonNull(binding.emailAddSignUp.getText()).toString().trim());
            }
        });

        binding.passwordSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validatePassword(Objects.requireNonNull(binding.passwordSignUp.getText()).toString().trim());
            }
        });

        binding.confirmPassSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateConfirmPassword(Objects.requireNonNull(binding.confirmPassSignUp.getText()).toString().trim(), Objects.requireNonNull(binding.passwordSignUp.getText()).toString().trim());
            }
        });

        binding.goToPersonalButton.setOnClickListener(view -> {
            binding.personalDetails.setVisibility(View.VISIBLE);
            binding.accountDetails.setVisibility(View.INVISIBLE);
            binding.healthDetails.setVisibility(View.INVISIBLE);
        });

        binding.firstNameSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateFirstName(Objects.requireNonNull(binding.firstNameSignUp.getText()).toString().trim());
            }
        });

        binding.lastNameSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateLastName(Objects.requireNonNull(binding.lastNameSignUp.getText()).toString().trim());
            }
        });


        binding.dobSignUp.setFocusable(false);
        binding.dobSignUp.setOnClickListener(view -> {
            binding.dobSignUp.setFocusableInTouchMode(true);
            binding.dobSignUp.requestFocus();

            // Create a new DatePickerDialog
            DatePickerDialog dobPickerDialog = new DatePickerDialog(
                    SignUpScreen.this,
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

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            String date = dateFormat.format(calendar1.getTime());

            // Set the selected date to the TextView
            binding.dobSignUp.setText(date);
        };

        binding.dobSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateDOB(Objects.requireNonNull(binding.dobSignUp.getText()).toString().trim());
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Male", "Female"});

        binding.selectSexSignUp.setAdapter(adapter);

        binding.selectSexSignUp.setOnClickListener(view -> hideKeyboard());

        binding.selectSexSignUp.setOnItemClickListener((parent, view, position, l) -> {
            String selectedSex = parent.getItemAtPosition(position).toString();
            validateSex(selectedSex);
        });

        binding.contactNumSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            public void afterTextChanged(Editable editable) {
                validateContactNumber(Objects.requireNonNull(binding.contactNumSignUp.getText()).toString().trim());
            }
        });

        binding.goToAccountButton.setOnClickListener(view -> {
            binding.personalDetails.setVisibility(View.INVISIBLE);
            binding.accountDetails.setVisibility(View.VISIBLE);
            binding.healthDetails.setVisibility(View.INVISIBLE);
        });

        binding.goToHealthButton.setOnClickListener(view -> {
            binding.personalDetails.setVisibility(View.INVISIBLE);
            binding.accountDetails.setVisibility(View.INVISIBLE);
            binding.healthDetails.setVisibility(View.VISIBLE);
        });

        binding.heightSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            public void afterTextChanged(Editable editable) {
                validateHeight(Objects.requireNonNull(binding.heightSignUp.getText()).toString().trim());
            }
        });

        binding.weightSignUp.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            public void afterTextChanged(Editable editable) {
                validateWeight(Objects.requireNonNull(binding.weightSignUp.getText()).toString().trim());
            }
        });
        //                //new String[]{"Very Active", "Moderately Active", "Lightly Active", "Sedentary"});

//        ArrayAdapter<String> phyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
//                new String[]{"Athlete", "Very Active", "Active", "Low Active", "Inactive", "Sedentary"});

        // Define arrays for main options and descriptors
        String[] mainOptions = new String[]{"Athlete", "Very Active", "Active", "Low Active", "Inactive", "Sedentary"};
        String[] descriptors = new String[]{"Professional athlete", "Exercise 6-7 times a week", "Exercise 3-5 times a week", "Exercise 2-3 times a week", "Exercise 1-2 times a week", "Little to no exercise"};

        ArrayAdapter<String> phyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, mainOptions);

        binding.phyActivityLvlSignUp.setAdapter(phyAdapter);

        binding.phyActivityLvlSignUp.setOnClickListener(view -> hideKeyboard());

        binding.phyActivityLvlSignUp.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = mainOptions[position]; // The main option
            String descriptor = descriptors[position]; // The descriptor

            // Set helper text to the descriptor regardless of the error state
            binding.phyActivityTextInput.setHelperTextEnabled(true);
            binding.phyActivityTextInput.setHelperText(descriptor);

            // Get the green color from colors.xml
            int greenColor = ContextCompat.getColor(this, R.color.green);

            // Set helper text color to green
            binding.phyActivityTextInput.setHelperTextColor(ColorStateList.valueOf(greenColor));

            // Validate the selected option (you can still validate the main option if needed)
            validatePhysicalActivityLevel(selectedOption);
        });

        foodAllergenCheckedItems = new boolean[]{false, false, false, false};
        healthConditionCheckedItems = new boolean[]{false, false, false, false};

        binding.foodAllergensSignUp.setOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Food Allergens", foodAllergenCheckedItems);
        });
        binding.foodAllergensTextInput.setEndIconOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Food Allergens", foodAllergenCheckedItems);
        });

        binding.healthConditionsSignUp.setOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Health Conditions", healthConditionCheckedItems);
        });
        binding.healthConditionsTextInput.setEndIconOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Health Conditions", healthConditionCheckedItems);
        });

        binding.backToPersonalButton.setOnClickListener(view -> {
            binding.personalDetails.setVisibility(View.VISIBLE);
            binding.accountDetails.setVisibility(View.INVISIBLE);
            binding.healthDetails.setVisibility(View.INVISIBLE);
        });

        binding.registerButton.setOnClickListener(view -> {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }

            signUpProgress.setTitle("Registering Account");
            signUpProgress.setMessage("Verifying Information...");
            signUpProgress.setCancelable(false);
            signUpProgress.show();

            String emailStr = binding.emailAddSignUp.getText().toString().trim();
            String passwordStr = binding.passwordSignUp.getText().toString().trim();
            String confirmPasswordStr = binding.confirmPassSignUp.getText().toString().trim();
            String firstNameStr = binding.firstNameSignUp.getText().toString().trim();
            String lastNameStr = binding.lastNameSignUp.getText().toString().trim();
            String dobStr = binding.dobSignUp.getText().toString().trim();
            String selectedSex = binding.selectSexSignUp.getText().toString().trim();
            String prefix = binding.contactNumTextInput.getPrefixText().toString().trim();
            String contactNumberStr = binding.contactNumSignUp.getText().toString().trim();
            String physicalActivityLvlStr = binding.phyActivityLvlSignUp.getText().toString().trim();
//            Double heightStr = Double.valueOf(binding.heightSignUp.getText().toString().trim());
//            Double weightStr = Double.valueOf(binding.weightSignUp.getText().toString().trim());

            String heightStr = binding.heightSignUp.getText().toString().trim();
            String weightStr = binding.weightSignUp.getText().toString().trim();
            String healthConditionStr = binding.healthConditionsSignUp.getText().toString().trim();
            String foodAllergensStr = binding.foodAllergensSignUp.getText().toString().trim();

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
            if (!validatePhysicalActivityLevel(physicalActivityLvlStr)) hasError = true;
            if (!validateHeight(heightStr)) hasError = true;
            if (!validateWeight(weightStr)) hasError = true;

            if (hasError) {
                signUpProgress.dismiss();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpScreen.this);
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
                auth.createUserWithEmailAndPassword(emailStr, passwordStr)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    signUpProgress.dismiss(); // Dismiss the progress dialog if registration fails
                                } else {
                                    final FirebaseUser user = auth.getCurrentUser();
                                    if (user != null) {
                                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(SignUpScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                    signUpProgress.dismiss(); // Dismiss the progress dialog if email verification fails
                                                } else {
                                                    // Email verification sent successfully
                                                    Toast.makeText(SignUpScreen.this, "Verification email sent. Please check your email.", Toast.LENGTH_SHORT).show();

                                                    // Firebase integration for user details in Realtime Database
                                                    db = FirebaseDatabase.getInstance();
                                                    reference = db.getReference("Users");

                                                    String uid = ((FirebaseUser) user).getUid(); // Get the UID

                                                    // Create a nested "Profile" node under each userUID
                                                    HashMap<String, Object> userMap = new HashMap<>();
                                                    userMap.put("confirmPass", confirmPasswordStr);
                                                    userMap.put("contactNum", prefix + contactNumberStr);
                                                    userMap.put("dob", dobStr);
                                                    userMap.put("email", emailStr);
                                                    userMap.put("firstName", firstNameStr);
                                                    userMap.put("foodAllergens", finalFoodAllergensStr);
                                                    userMap.put("healthConditions", finalHealthConditionStr);
                                                    userMap.put("height", calculateWeightHeightValue(height));
                                                    userMap.put("lastName", lastNameStr);
                                                    userMap.put("password", passwordStr);
                                                    userMap.put("phyActivity", physicalActivityLvlStr);
                                                    userMap.put("profilePhoto", "null");
                                                    userMap.put("sex", selectedSex);
                                                    userMap.put("weight", calculateWeightHeightValue(weight));

                                                    HashMap<String, Object> userWithProfile = new HashMap<>();
                                                    userWithProfile.put("Profile", userMap);

                                                    reference.child(uid).setValue(userWithProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            signUpProgress.dismiss();

                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(SignUpScreen.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                                                                Intent intent = new Intent(SignUpScreen.this, LoginScreen.class);
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(SignUpScreen.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
            }
        });

        SpannableString spanString = getSpannableString();

        binding.goToLoginAcc1.setText(spanString);
        binding.goToLoginAcc2.setText(spanString);
        binding.goTologinAcc3.setText(spanString);

        binding.goToLoginAcc1.setMovementMethod(LinkMovementMethod.getInstance());
        binding.goToLoginAcc2.setMovementMethod(LinkMovementMethod.getInstance());
        binding.goTologinAcc3.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NotNull
    private SpannableString getSpannableString() {
        String text = "Already have an Account? Login";

        SpannableString spanString = new SpannableString(text);

        ClickableSpan clickable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Intent intent = new Intent(SignUpScreen.this, LoginScreen.class);
                startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.rgb(142, 190, 38));
                ds.setUnderlineText(false);
            }
        };

        spanString.setSpan(clickable, 25, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    private double calculateWeightHeightValue(double value) {
        // Rounds to one decimal place
        return Math.round(value * 10.0) / 10.0;
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
            if (binding.selectSexSignUp.getText().toString().trim().equals("Male")) {
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

        builder.setMultiChoiceItems(items, checkedItems, (dialog, which, isChecked) -> {
            checkedItems[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            updateSelectedItems(title, items, checkedItems);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            System.arraycopy(initialCheckedState, 0, checkedItems, 0, initialCheckedState.length);
        });

        builder.setNeutralButton("Clear All", (dialog, which) -> {
            Arrays.fill(checkedItems, false);
            if (title.equals("Select Food Allergens")) {
                binding.foodAllergensSignUp.setText("");
            } else if (title.equals("Select Health Conditions")) {
                binding.healthConditionsSignUp.setText("");
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
            binding.foodAllergensSignUp.setText(selectedItemsText.toString());
        } else if (title.equals("Select Health Conditions")) {
            binding.healthConditionsSignUp.setText(selectedItemsText.toString());
        }
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            binding.emailAddTextInput.setError("Email is required");
            return false;
        } else if (!isValidEmail(email)) {
            binding.emailAddTextInput.setError("Invalid email format");
            return false;
        } else {
            binding.emailAddTextInput.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            binding.passwordTextInput.setError("Password is required");
            return false;
        } else if (password.length() < 8) {
            binding.passwordTextInput.setError("Password must be at least 8 characters long");
            return false;
        } else {
            binding.passwordTextInput.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(String confirmPassword, String password) {
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPassTextInput.setError("Confirm password is required");
            return false;
        } else if (!confirmPassword.equals(password)) {
            binding.confirmPassTextInput.setError("Passwords do not match");
            return false;
        } else {
            binding.confirmPassTextInput.setError(null);
            return true;
        }
    }

    private boolean validateFirstName(String firstName) {
        if (TextUtils.isEmpty(firstName)) {
            binding.firstNameTextInput.setError("First name is required");
            return false;
        } else {
            binding.firstNameTextInput.setError(null);
            return true;
        }
    }

    private boolean validateLastName(String lastName) {
        if (TextUtils.isEmpty(lastName)) {
            binding.lastNameTextInput.setError("Last name is required");
            return false;
        } else {
            binding.lastNameTextInput.setError(null);
            return true;
        }
    }

    private boolean validateDOB(String dobStr) {
        if (TextUtils.isEmpty(dobStr)) {
            binding.dobTextInput.setError("Date of birth is required");
            return false;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            dateFormat.setLenient(false);

            try {
                Date dobDate = dateFormat.parse(dobStr);

                Calendar dobCalendar = Calendar.getInstance();
                dobCalendar.setTime(Objects.requireNonNull(dobDate));
                Calendar minAgeCalendar = Calendar.getInstance();
                minAgeCalendar.add(Calendar.YEAR, -18);

                if (dobCalendar.before(minAgeCalendar) || dobCalendar.equals(minAgeCalendar)) {
                    binding.dobTextInput.setError(null);
                    return true;
                } else {
                    binding.dobTextInput.setError("Must be at least 18 years old");
                    return false;
                }
            } catch (ParseException e) {
                binding.dobTextInput.setError("Invalid DOB format");
                return false;
            }
        }
    }


    private boolean validateSex(String selectedSex) {
        if (TextUtils.isEmpty(selectedSex)) {
            binding.selectSexTextInput.setError("Please select a sex");
            return false;
        } else if (!isValidSex(selectedSex)) {
            binding.selectSexTextInput.setError("Invalid sex");
            return false;
        } else {
            binding.selectSexTextInput.setError(null);
            return true;
        }
    }

    private boolean isValidSex(String selectedSex) {
        List<String> validOptions = Arrays.asList("Male", "Female");
        return validOptions.contains(selectedSex);
    }

    private boolean validateContactNumber(String contactNumber) {
        if (TextUtils.isEmpty(contactNumber)) {
            binding.contactNumTextInput.setError("Contact Number is required.");
            return false;
        } else if (!isValidContactNumber(contactNumber)) {
            binding.contactNumTextInput.setError("Invalid contact number format");
            return false;
        } else {
            binding.contactNumTextInput.setError(null);
            return true;
        }
    }


//    private boolean isValidContactNumber(String contactNumber) {
//        String pattern = "^(09)[0-9]{9}$"; // 11-digit numeric format starting with "09"
//        return Pattern.matches(pattern, contactNumber);
//    }

    private boolean isValidContactNumber(String contactNumber) {
        // Ensure the contact number has exactly 10 digits, disregarding the prefix
        String expectedFullContactNumber = "^[0-9]{10}$";

        if (!contactNumber.matches(expectedFullContactNumber)) {
            binding.contactNumTextInput.setError("Invalid contact number format");
            return false;
        } else {
            binding.contactNumTextInput.setError(null);
            return true;
        }
    }

    private boolean validatePhysicalActivityLevel(String selectedOption) {
        if (TextUtils.isEmpty(selectedOption)) {
            binding.phyActivityTextInput.setError("Please select a physical activity level");
            return false;
        } else if (!isValidPhysicalActivityLevel(selectedOption)) {
            binding.phyActivityTextInput.setError("Invalid physical activity level");
            return false;
        } else {
            binding.phyActivityTextInput.setError(null);
            return true;
        }
    }


    private boolean isValidPhysicalActivityLevel(String selectedOption) {
        List<String> validOptions = Arrays.asList("Athlete", "Very Active", "Active", "Low Active", "Inactive", "Sedentary");
        return validOptions.contains(selectedOption);
    }

    private boolean validateHeight(String heightStr) {
        if (TextUtils.isEmpty(heightStr)) {
            binding.heightTextInput.setError("Height is required");
            return false;
        } else {
            binding.heightTextInput.setError(null);
            return true;
        }
    }

    private boolean validateWeight(String weightStr) {
        if (TextUtils.isEmpty(weightStr)) {
            binding.weightTextInput.setError("Weight is required");
            return false;
        } else {
            binding.weightTextInput.setError(null);
            return true;
        }
    }
}
