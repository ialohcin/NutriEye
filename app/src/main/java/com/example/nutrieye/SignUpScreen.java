package com.example.nutrieye;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class SignUpScreen extends AppCompatActivity {

    private LinearLayout account, personal, health;
    private CardView signUpContainer;
    private MaterialButton registerButton, goToPersonalButton, backToPersonalButton, backtoAccountButton, goToHealthButton;
    private TextView loginAcc1, loginAcc2, loginAcc3;

    private TextInputEditText email, password, confirmPass, firstName, lastName, dob, contactNumber, height, weight;
    private AutoCompleteTextView selectSex, selectPhyActivityLvl, selectFoodAllergens, selectHealthConditions;
    private TextInputLayout emailTextInput, passwordTextInput, confirmPassTextInput, firstNameTextInput, lastNameTextInput, dobTextInput, sexTextInput, contactNumTextInput,
            heightTextInput, weightTextInput, phyActivityTextInput, foodAllergensTextInput, healthConditionsTextInput;

    ProgressDialog signUpProgress;

    private boolean[] foodAllergenCheckedItems;
    private boolean[] healthConditionCheckedItems;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_screen);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        auth = FirebaseAuth.getInstance();

        account = findViewById(R.id.accountDetails);
        personal = findViewById(R.id.personalDetails);
        health = findViewById(R.id.healthDetails);
        health.setVisibility(View.INVISIBLE);
        personal.setVisibility(View.INVISIBLE);

        signUpProgress = new ProgressDialog(this);

        signUpContainer = findViewById(R.id.signUpContainer);
        signUpContainer.setBackgroundResource(R.drawable.cardview);

        email = findViewById(R.id.emailAddSignUp);
        password = findViewById(R.id.passwordSignUp);
        confirmPass = findViewById(R.id.confirmPassSignUp);

        emailTextInput = findViewById(R.id.emailAddTextInput);
        passwordTextInput = findViewById(R.id.passwordTextInput);
        confirmPassTextInput = findViewById(R.id.confirmPassTextInput);

        email.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateEmail(Objects.requireNonNull(email.getText()).toString().trim()); }
        });

        password.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validatePassword(Objects.requireNonNull(password.getText()).toString().trim()); }
        });

        confirmPass.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateConfirmPassword(Objects.requireNonNull(confirmPass.getText()).toString().trim(), Objects.requireNonNull(password.getText()).toString().trim()); }
        });

        goToPersonalButton = findViewById(R.id.goToPersonalButton);

        goToPersonalButton.setOnClickListener(view -> {
            Toast.makeText(SignUpScreen.this, "", Toast.LENGTH_SHORT).show();
            personal.setVisibility(View.VISIBLE);
            account.setVisibility(View.INVISIBLE);
            health.setVisibility(View.INVISIBLE);
        });

        firstName = findViewById(R.id.firstNameSignUp);
        firstNameTextInput = findViewById(R.id.firstNameTextInput);
        lastName = findViewById(R.id.lastNameSignUp);
        lastNameTextInput = findViewById(R.id.lastNameTextInput);
        dob = findViewById(R.id.dobSignUp);
        dobTextInput = findViewById(R.id.dobTextInput);
        selectSex = findViewById(R.id.selectSexSignUp);
        sexTextInput = findViewById(R.id.selectSexTextInput);
        contactNumber = findViewById(R.id.contactNumSignUp);
        contactNumTextInput = findViewById(R.id.contactNumTextInput);

        firstName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateFirstName(Objects.requireNonNull(firstName.getText()).toString().trim()); }
        });

        lastName.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateLastName(Objects.requireNonNull(lastName.getText()).toString().trim()); }
        });

        dob.setFocusable(false);
        dob.setOnClickListener(view -> {
            dob.setFocusableInTouchMode(true);
            dob.requestFocus();
            DatePickerDialog dobPickerDialog = new DatePickerDialog(SignUpScreen.this, (datePicker, year1, month1, dayOfMonth) -> {
                month1 = month1 + 1;
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, year1);
                calendar1.set(Calendar.MONTH, month1 - 1);
                calendar1.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.US);
                String date = dateFormat.format(calendar1.getTime());
                dob.setText(date);
            },year,month - 1,day);
            dobPickerDialog.show();
        });

        dob.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateDOB(Objects.requireNonNull(dob.getText()).toString().trim()); }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Male", "Female", "Non-binary"});

        selectSex.setAdapter(adapter);

        selectSex.setOnClickListener(view -> hideKeyboard());

        selectSex.setOnItemClickListener((parent, view, position, l) -> {
            String selectedSex = parent.getItemAtPosition(position).toString();
            validateSex(selectedSex);
        });

        contactNumber.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            public void afterTextChanged(Editable editable) { validateContactNumber(Objects.requireNonNull(contactNumber.getText()).toString().trim()); }
        });

        backtoAccountButton = findViewById(R.id.goToAccountButton);
        backtoAccountButton.setOnClickListener(view -> {
            personal.setVisibility(View.INVISIBLE);
            account.setVisibility(View.VISIBLE);
            health.setVisibility(View.INVISIBLE);
        });

        goToHealthButton = findViewById(R.id.goToHealthButton);
        goToHealthButton.setOnClickListener(view -> {
            personal.setVisibility(View.INVISIBLE);
            account.setVisibility(View.INVISIBLE);
            health.setVisibility(View.VISIBLE);
        });

        height = findViewById(R.id.heightSignUp);
        heightTextInput = findViewById(R.id.heightTextInput);

        height.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            public void afterTextChanged(Editable editable) { validateHeight(Objects.requireNonNull(height.getText()).toString().trim()); }
        });

        weight = findViewById(R.id.weightSignUp);
        weightTextInput = findViewById(R.id.weightTextInput);

        weight.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            public void afterTextChanged(Editable editable) { validateWeight(Objects.requireNonNull(weight.getText()).toString().trim()); }
        });

        selectPhyActivityLvl = findViewById(R.id.phyActivityLvlSignUp);
        phyActivityTextInput = findViewById(R.id.phyActivityTextInput);

        ArrayAdapter<String> phyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                new String[]{"Very Active", "Moderately Active", "Lightly Active", "Sedentary"});
        selectPhyActivityLvl.setAdapter(phyAdapter);

        selectPhyActivityLvl.setOnClickListener(view -> hideKeyboard());

        selectPhyActivityLvl.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = parent.getItemAtPosition(position).toString();
            validatePhysicalActivityLevel(selectedOption);
        });

        foodAllergenCheckedItems = new boolean[]{false, false, false, false};
        healthConditionCheckedItems = new boolean[]{false, false, false, false};

        foodAllergensTextInput = findViewById(R.id.foodAllergensTextInput);
        selectFoodAllergens = findViewById(R.id.foodAllergensSignUp);

        selectFoodAllergens.setOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Food Allergens", foodAllergenCheckedItems);
        });
        foodAllergensTextInput.setEndIconOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Food Allergens", foodAllergenCheckedItems);
        });

        healthConditionsTextInput = findViewById(R.id.healthConditionsTextInput);
        selectHealthConditions = findViewById(R.id.healthConditionsSignUp);

        selectHealthConditions.setOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Health Conditions", healthConditionCheckedItems);
        });
        healthConditionsTextInput.setEndIconOnClickListener(view -> {
            hideKeyboard();
            showMultiChoiceDialog("Select Health Conditions", healthConditionCheckedItems);
        });

        backToPersonalButton = findViewById(R.id.backToPersonalButton);

        backToPersonalButton.setOnClickListener(view -> {
            personal.setVisibility(View.VISIBLE);
            account.setVisibility(View.INVISIBLE);
            health.setVisibility(View.INVISIBLE);
        });

        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(view -> {
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                currentFocus.clearFocus();
            }

            signUpProgress.setTitle("Registering Account");
            signUpProgress.setMessage("Verifying Information...");
            signUpProgress.setCancelable(false);
            signUpProgress.show();

            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();
            String confirmPasswordStr = confirmPass.getText().toString().trim();
            String firstNameStr = firstName.getText().toString().trim();
            String lastNameStr = lastName.getText().toString().trim();
            String dobStr = dob.getText().toString().trim();
            String selectedSex = selectSex.getText().toString().trim();
            String contactNumberStr = contactNumber.getText().toString().trim();
            String selectedPhysicalActivityLevel = selectPhyActivityLvl.getText().toString().trim();
            String selectHealthConditionsStr = selectHealthConditions.getText().toString().trim();
            String selectFoodAllergensStr = selectFoodAllergens.getText().toString().trim();
            String heightStr = height.getText().toString().trim();
            String weightStr = weight.getText().toString().trim();

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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignUpScreen.this);
                alertDialog.setTitle("Validation Error");
                alertDialog.setMessage("Please check the form for errors and complete all fields.");
                alertDialog.setPositiveButton("OK", (dialogInterface, option) -> dialogInterface.dismiss());
                alertDialog.show();
            } else {
                auth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            signUpProgress.dismiss(); // Dismiss the progress dialog if registration fails
                        } else {
                            final FirebaseUser user = auth.getCurrentUser();
                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(SignUpScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        signUpProgress.dismiss(); // Dismiss the progress dialog if email verification fails
                                    } else {
                                        // Registration and email verification successful
                                        Toast.makeText(SignUpScreen.this, "Please Verify your Email. Thank you!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpScreen.this, LoginScreen.class));
                                        finish();

                                        signUpProgress.dismiss(); // Dismiss the progress dialog after registration and email verification
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });


        loginAcc1 = findViewById(R.id.goToLoginAcc1);
        loginAcc2 = findViewById(R.id.goToLoginAcc2);
        loginAcc3 = findViewById(R.id.goTologinAcc3);

        SpannableString spanString = getSpannableString();

        loginAcc1.setText(spanString);
        loginAcc2.setText(spanString);
        loginAcc3.setText(spanString);
        loginAcc1.setMovementMethod(LinkMovementMethod.getInstance());
        loginAcc2.setMovementMethod(LinkMovementMethod.getInstance());
        loginAcc3.setMovementMethod(LinkMovementMethod.getInstance());
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
                ds.setColor(Color.rgb(142, 190 , 38));
                ds.setUnderlineText(false);
            }
        };

        spanString.setSpan(clickable, 25,30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
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
                selectFoodAllergens.setText("");
            } else if (title.equals("Select Health Conditions")) {
                selectHealthConditions.setText("");
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
            selectFoodAllergens.setText(selectedItemsText.toString());
        } else if (title.equals("Select Health Conditions")) {
            selectHealthConditions.setText(selectedItemsText.toString());
        }
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailTextInput.setError("Email is required");
            return false;
        } else if (!isValidEmail(email)) {
            emailTextInput.setError("Invalid email format");
            return false;
        } else {
            emailTextInput.setError(null);
            return true;
        }
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordTextInput.setError("Password is required");
            return false;
        } else if (password.length() < 8) {
            passwordTextInput.setError("Password must be at least 8 characters long");
            return false;
        } else {
            passwordTextInput.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(String confirmPassword, String password) {
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPassTextInput.setError("Confirm password is required");
            return false;
        } else if (!confirmPassword.equals(password)) {
            confirmPassTextInput.setError("Passwords do not match");
            return false;
        } else {
            confirmPassTextInput.setError(null);
            return true;
        }
    }

    private boolean validateFirstName(String firstName) {
        if (TextUtils.isEmpty(firstName)) {
            firstNameTextInput.setError("First name is required");
            return false;
        } else {
            firstNameTextInput.setError(null);
            return true;
        }
    }

    private boolean validateLastName(String lastName) {
        if (TextUtils.isEmpty(lastName)) {
            lastNameTextInput.setError("Last name is required");
            return false;
        } else {
            lastNameTextInput.setError(null);
            return true;
        }
    }

    private boolean validateDOB(String dobStr) {
        if (TextUtils.isEmpty(dobStr)) {
            dobTextInput.setError("Date of birth is required");
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
                    dobTextInput.setError("DOB within 18 to 99 years");
                    return false;
                } else {
                    dobTextInput.setError(null);
                    return true;
                }
            } catch (ParseException e) {
                dobTextInput.setError("Invalid DOB format");
                return false;
            }
        }
    }

    private boolean validateSex(String selectedSex) {
        if (TextUtils.isEmpty(selectedSex)) {
            sexTextInput.setError("Please select a sex");
            return false;
        } else if (!isValidSex(selectedSex)) {
            sexTextInput.setError("Invalid sex");
            return false;
        } else {
            sexTextInput.setError(null);
            return true;
        }
    }

    private boolean isValidSex(String selectedSex) {
        List<String> validOptions = Arrays.asList("Male", "Female", "Non-binary");
        return validOptions.contains(selectedSex);
    }

    private boolean validateContactNumber(String contactNumber) {
        if (TextUtils.isEmpty(contactNumber)) {
            contactNumTextInput.setError("Contact number is required");
            return false;
        } else if (!isValidContactNumber(contactNumber)) {
            contactNumTextInput.setError("Invalid contact number format");
            return false;
        } else {
            contactNumTextInput.setError(null);
            return true;
        }
    }

    private boolean isValidContactNumber(String contactNumber) {
        String pattern = "^(09)[0-9]{9}$"; // 11-digit numeric format starting with "09"
        return Pattern.matches(pattern, contactNumber);
    }

    private boolean validatePhysicalActivityLevel(String selectedOption) {
        if (TextUtils.isEmpty(selectedOption)) {
            phyActivityTextInput.setError("Please select a physical activity level");
            return false;
        } else if (!isValidPhysicalActivityLevel(selectedOption)) {
            phyActivityTextInput.setError("Invalid physical activity level");
            return false;
        } else {
            phyActivityTextInput.setError(null);
            return true;
        }
    }

    private boolean isValidPhysicalActivityLevel(String selectedOption) {
        List<String> validOptions = Arrays.asList("Very Active", "Moderately Active", "Lightly Active", "Sedentary");
        return validOptions.contains(selectedOption);
    }

    private boolean validateHeight(String heightStr) {
        if (TextUtils.isEmpty(heightStr)) {
            heightTextInput.setError("Height is required");
            return false;
        } else {
            heightTextInput.setError(null);
            return true;
        }
    }

    private boolean validateWeight(String weightStr) {
        if (TextUtils.isEmpty(weightStr)) {
            weightTextInput.setError("Weight is required");
            return false;
        } else {
            weightTextInput.setError(null);
            return true;
        }
    }
}
