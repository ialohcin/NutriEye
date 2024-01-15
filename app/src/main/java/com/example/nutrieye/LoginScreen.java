package com.example.nutrieye;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LoginScreen extends AppCompatActivity {
    private AlertDialog alertDialog;
    private TextInputEditText emailLogin, passwordLogin, newConfirmPasswordEditText;
    private TextInputLayout emailTextInput, passwordLoginTextInput, newConfirmPasswordLayout;
    private TextView forgotPassword, toSignUp;
    private MaterialButton loginButton;
    private CardView loginContainer;
    private ProgressDialog loginProgress;
    private CheckBox rememberUser;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        initComponents();

        setupClickableSpan();

        auth = FirebaseAuth.getInstance();

        emailLogin.addTextChangedListener(createTextWatcher(emailLogin));
        passwordLogin.addTextChangedListener(createTextWatcher(passwordLogin));

        // Check and auto-login if "Remember Me" is enabled
        checkAndAutoLogin();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginButtonClick();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordRecoveryDialog();
            }
        });
    }

    private void checkAndAutoLogin() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isUserRemembered = preferences.getBoolean("isUserRemembered", false);

        if (isUserRemembered) {
            String savedEmail = preferences.getString("savedEmail", "");
            String savedPassword = preferences.getString("savedPassword", "");

            // Set the partially masked email for display
            emailLogin.setText(savedEmail);
            rememberUser.setChecked(true);

            // Mask the password with asterisks based on its length
            String maskedPassword = maskPassword(savedPassword);
            passwordLogin.setText(maskedPassword);

            autoLogin(savedEmail, savedPassword);
        }
    }

    private String maskPassword(String password) {
        int passwordLength = password.length();
        StringBuilder maskedPassword = new StringBuilder();

        // Replace characters with asterisks
        for (int i = 0; i < passwordLength; i++) {
            maskedPassword.append('*');
        }

        return maskedPassword.toString();
    }

    private void autoLogin(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginProgress.dismiss(); // Dismiss any existing progress dialog

                        if (task.isSuccessful()) {
                            // Successful login
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                String uid = currentUser.getUid();

                                // Log the activity here
                                // Create ActivityLogs structure
                                DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

                                // Get current time
                                String currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());
                                String currentDay = new SimpleDateFormat("MMM dd yyyy", Locale.US).format(Calendar.getInstance().getTime());

                                // Generate a unique ID for the log entry
                                String logID = "LogID_" + System.currentTimeMillis();

                                // Create the log entry structure
                                DatabaseReference logEntryRef = activityLogsRef.child(currentDay).child(logID);
                                logEntryRef.child("action").setValue("Auto-Logged In");
                                logEntryRef.child("category").setValue("Authentication");
                                logEntryRef.child("timestamp").setValue(currentDay + " " + currentTime);

                                // Navigate to the desired screen
                                Intent intent = new Intent(LoginScreen.this, NavigationScreen.class);
                                intent.putExtra("USER_UID", uid);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Handle login failure
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // Incorrect credentials
                                Toast.makeText(LoginScreen.this, "Incorrect email or password. Please verify your credentials", Toast.LENGTH_SHORT).show();
                            } else {
                                // Other errors
                                Toast.makeText(LoginScreen.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        // Explicitly check the 'rememberUser' checkbox
        rememberUser.setChecked(true);
    }

    private void saveUserLoginState(String email, String password) {
        // Save user's login state in SharedPreferences if rememberUser is checked
        if (rememberUser.isChecked()) {
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isUserRemembered", true);
            editor.putString("savedEmail", email);
            editor.putString("savedPassword", password);
            editor.apply();
        } else {
            // If rememberUser is not checked, clear the Remember Me preferences
            SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("isUserRemembered");
            editor.remove("savedEmail");
            editor.remove("savedPassword");
            editor.apply();
        }
    }

    private void initComponents() {
        emailLogin = findViewById(R.id.emailAddLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        emailTextInput = findViewById(R.id.emailAddTextInput);
        passwordLoginTextInput = findViewById(R.id.passwordLoginTextInput);
        forgotPassword = findViewById(R.id.forgotPassword);
        rememberUser = findViewById(R.id.rememberUser);
        loginButton = findViewById(R.id.loginButton);
        toSignUp = findViewById(R.id.signUpAcc);
        loginContainer = findViewById(R.id.loginContainer);
        loginProgress = new ProgressDialog(this);

        int paintFlags = forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG;
        forgotPassword.setPaintFlags(paintFlags);

        loginContainer.setBackgroundResource(R.drawable.cardview);
    }

    private void showPasswordRecoveryDialog(){
        View alertDialogView = LayoutInflater.from(this).inflate(R.layout.forgot_password, null);

        TextInputLayout recoveryEmailLayout = alertDialogView.findViewById(R.id.recoveryEmailLayout);
        TextInputEditText recoveryEmailEditText = alertDialogView.findViewById(R.id.recoveryEmailEditText);

        TextInputLayout newPasswordLayout = alertDialogView.findViewById(R.id.newPasswordLayout);
        TextInputEditText newPasswordEditText = alertDialogView.findViewById(R.id.newPasswordEditText);

        newConfirmPasswordLayout = alertDialogView.findViewById(R.id.newConfirmPasswordLayout);
        newConfirmPasswordEditText = alertDialogView.findViewById(R.id.newConfirmPasswordEditText);

        Button cancelButton = alertDialogView.findViewById(R.id.cancelRecovery);
        Button confirmButton = alertDialogView.findViewById(R.id.confirmRecovery);
        ImageButton closeDialog = alertDialogView.findViewById(R.id.closeDialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.TransparentAlertDialog);
        builder.setView(alertDialogView);

        alertDialog = builder.create();

        recoveryEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                validateEmail(editable.toString().trim(), recoveryEmailLayout);
            }
        });

        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validatePassword(editable.toString().trim(), newPasswordLayout);
            }
        });

        newConfirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                validateConfirmPassword(newConfirmPasswordEditText.getText().toString().trim(), newPasswordEditText.getText().toString().trim());
            }
        });

        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the bottom sheet dialog
                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the bottom sheet dialog
                alertDialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recoveryEmail = recoveryEmailEditText.getText().toString().trim();
                String newPassword = newPasswordEditText.getText().toString().trim();
                String newConfirmPassword = newConfirmPasswordEditText.getText().toString().trim();

                if (validateEmail(recoveryEmail, recoveryEmailLayout) && validatePassword(newPassword, newPasswordLayout) && validateConfirmPassword(newConfirmPassword, newPassword)) {
                    // Perform Firebase operations for password recovery
                    recoverPassword(recoveryEmail, newPassword, newConfirmPassword);
                }
            }
        });

        alertDialog.show();

    }

    private void recoverPassword(String recoveryEmail, String newPassword, String newConfirmPassword) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        usersRef.orderByChild("Profile/email").equalTo(recoveryEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Email exists in the database, proceed with password recovery
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String userUID = snapshot.getKey(); // Retrieve userUID
                        // Assume the 'contactNum' field is present in the database
                        String contactNum = snapshot.child("Profile/contactNum").getValue(String.class);

                        if (newPassword.equals(newConfirmPassword)) {
                            // Generate a new password (replace with your logic)
                            generateNewPassword(newPassword);

                            if (contactNum != null && !contactNum.isEmpty()) {
                                // Use SmsManager to send the new password via SMS
                                sendSms(contactNum, newPassword);

                                // After sending the message, you can notify the user
                                Toast.makeText(LoginScreen.this, "Password sent to your contact number", Toast.LENGTH_SHORT).show();

                                // Dismiss the recovery dialog
                                alertDialog.dismiss();
                            } else {
                                // Handle the case where the contact number is not available
                                Toast.makeText(LoginScreen.this, "Contact number not available", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Passwords do not match, show an error message
                            Toast.makeText(LoginScreen.this, "Passwords do not match. Please enter matching passwords.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // Email does not exist in the database
                    Toast.makeText(LoginScreen.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential database error
                Toast.makeText(LoginScreen.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendSms(String phoneNumber, String newPassword) {
        try {
            // Create the SMS manager
            SmsManager smsManager = SmsManager.getDefault();

            // Define the message to be sent
            String message = "Hello!\n\nYour password has been reset. If you didn't request this change, please secure your account and contact support immediately.\n\nYour new password is: " + newPassword + "\n\nHave a great day!\n\nFrom,\n\n- NutriEye Team";

            // Send the SMS
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception or show an error message
            Toast.makeText(LoginScreen.this, "Failed to send SMS. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateNewPassword(String newPassword) {
        // Add your logic to generate a new password
        return newPassword;
    }


    private void setupClickableSpan() {
        String text = "Don't Have an Account? Sign Up";

        SpannableString spanString = new SpannableString(text);

        ClickableSpan clickable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                startSignUpActivity();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.rgb(142, 190, 38));
                ds.setUnderlineText(false);
            }
        };

        spanString.setSpan(clickable, 23, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        toSignUp.setText(spanString);
        toSignUp.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private TextWatcher createTextWatcher(final TextInputEditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText == emailLogin) {
                    validateEmail(editable.toString().trim(), emailTextInput);
                } else if (editText == passwordLogin) {
                    validatePassword(editable.toString().trim(), passwordLoginTextInput);
                }
            }
        };
    }

    private void handleLoginButtonClick() {
        loginProgress.setTitle("Logging in");
        loginProgress.setMessage("Verifying User Credentials...");
        loginProgress.setCancelable(false);
        loginProgress.show();

        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        boolean isEmailValid = validateEmail(email, emailTextInput);
        boolean isPasswordValid = validatePassword(password, passwordLoginTextInput);

        if (TextUtils.isEmpty(email)) {
            emailTextInput.setError("Email is required");
            loginProgress.dismiss();
            return;
        } else {
            emailTextInput.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLoginTextInput.setError("Password is required");
            loginProgress.dismiss();
            return;
        } else {
            passwordLoginTextInput.setError(null);
        }

        if (isEmailValid && isPasswordValid && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
            usersRef.orderByChild("Profile/email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Email exists in the database, proceed with sign-in
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String userUID = snapshot.getKey(); // Retrieve userUID
                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    loginProgress.dismiss();
                                    if (!task.isSuccessful()) {
                                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                            // Incorrect credentials
                                            Toast.makeText(LoginScreen.this, "Incorrect email or password. Please verify your credentials", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Other errors
                                            Toast.makeText(LoginScreen.this, task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        FirebaseUser currentUser = auth.getCurrentUser();
                                        if (currentUser != null) {
                                            String uid = currentUser.getUid();
                                            if (!currentUser.isEmailVerified()) {
                                                // Email exists but not verified
                                                Toast.makeText(LoginScreen.this, "Please verify your email first. Thank you!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Log the activity here
                                                // Create ActivityLogs structure
                                                DatabaseReference userRootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userUID);
                                                DatabaseReference activityLogsRef = userRootRef.child("ActivityLogs");

                                                // Get current time
                                                String currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.US).format(Calendar.getInstance().getTime());
                                                String currentDay = new SimpleDateFormat("MMM dd yyyy", Locale.US).format(Calendar.getInstance().getTime());

                                                // Generate a unique ID for the log entry
                                                String logID = "LogID_" + System.currentTimeMillis();

                                                // Create the log entry structure
                                                DatabaseReference logEntryRef = activityLogsRef.child(currentDay).child(logID);
                                                logEntryRef.child("action").setValue("Logged In");
                                                logEntryRef.child("category").setValue("Authentication");
                                                logEntryRef.child("timestamp").setValue(currentDay + " " + currentTime);

                                                // Successful login

                                                // Save user's login state in SharedPreferences if rememberUser is checked
                                                saveUserLoginState(email,password);

                                                Intent intent = new Intent(LoginScreen.this, NavigationScreen.class);
                                                intent.putExtra("USER_UID", uid);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }
                                }
                            });
                        }

                    } else {
                        // Email does not exist in the database
                        Toast.makeText(LoginScreen.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                        loginProgress.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential database error
                    Toast.makeText(LoginScreen.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    loginProgress.dismiss();
                }
            });
        }
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(LoginScreen.this, SignUpScreen.class);
        startActivity(intent);
        LoginScreen.this.finishAffinity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginScreen.this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you really want to exit?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int option) {
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

    private boolean validateEmail(String email, TextInputLayout layout) {
        if (TextUtils.isEmpty(email)) {
            layout.setError("Email is required");
            return false;
        } else if (!isValidEmail(email)) {
            layout.setError("Invalid email format");
            return false;
        } else if (!isValidDomain(email)) {
            layout.setError("Invalid email domain");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidDomain(CharSequence email) {
        String[] validDomains = {"edu", "com", "gov"}; // Add your valid domains here

        String[] parts = email.toString().split("@");
        if (parts.length != 2) {
            return false;
        }

        String domain = parts[1].toLowerCase();

        for (String validDomain : validDomains) {
            if (domain.endsWith("." + validDomain)) {
                return true;
            }
        }

        return false;
    }

    private boolean validatePassword(String password, TextInputLayout passwordLayout) {
        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Password is required");
            return false;
        } else if (password.length() < 8) {
            passwordLayout.setError("Password must be at least 8 characters long");
            return false;
        } else {
            passwordLayout.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword(String confirmPassword, String password) {
        if (TextUtils.isEmpty(confirmPassword)) {
            newConfirmPasswordLayout.setError("Confirm password is required");
            return false;
        } else if (!confirmPassword.equals(password)) {
            newConfirmPasswordLayout.setError("Passwords do not match");
            return false;
        } else {
            newConfirmPasswordLayout.setError(null);
            return true;
        }
    }
}
