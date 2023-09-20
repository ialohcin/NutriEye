package com.example.nutrieye;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.nutrieye.databinding.ActivityEditProfileScreenBinding;
import com.example.nutrieye.databinding.ActivityNavigationScreenBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class EditProfileScreen extends AppCompatActivity {

    ActivityEditProfileScreenBinding binding;
    private boolean[] foodAllergenCheckedItems;
    private boolean[] healthConditionCheckedItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

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
                new String[]{"Male", "Female", "Non-binary"});

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
                new String[]{"Very Active", "Moderately Active", "Lightly Active", "Sedentary"});
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

            String emailStr = Objects.requireNonNull(binding.emailAddProfile.getText()).toString().trim();
            String passwordStr = Objects.requireNonNull(binding.passwordProfile.getText()).toString().trim();
            String confirmPasswordStr = Objects.requireNonNull(binding.confirmPassProfile.getText()).toString().trim();
            String firstNameStr = Objects.requireNonNull(binding.firstNameProfile.getText()).toString().trim();
            String lastNameStr = Objects.requireNonNull(binding.lastNameProfile.getText()).toString().trim();
            String dobStr = Objects.requireNonNull(binding.dobProfile.getText()).toString().trim();
            String selectedSex = binding.selectSexProfile.getText().toString().trim();
            String contactNumberStr = Objects.requireNonNull(binding.contactNumProfile.getText()).toString().trim();
            String selectedPhysicalActivityLevel = binding.phyActivityLvlProfile.getText().toString().trim();
            String heightStr = Objects.requireNonNull(binding.heightProfile.getText()).toString().trim();
            String weightStr = Objects.requireNonNull(binding.weightProfile.getText()).toString().trim();

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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileScreen.this);
                builder.setMessage("The following changes have been saved. \n Please check your email for a confirmation link to confirm the changes")
                        .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
                builder.create().show();
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
    @Override
    public void onBackPressed() {
        navigateToProfileFragment();
    }

    private void navigateToProfileFragment() {
        Intent intent = new Intent(this, NavigationScreen.class);
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
            items = new String[]{"Pregnancy", "Diabetes", "Hypertension", "Heart Disease"};
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