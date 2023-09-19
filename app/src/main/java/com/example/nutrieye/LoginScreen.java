package com.example.nutrieye;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginScreen extends AppCompatActivity {

    private TextInputEditText emailLogin, passwordLogin;
    private TextInputLayout emailTextInput, passwordLoginTextInput;
    private TextView forgotPassword, toSignUp;
    private MaterialButton loginButton;
    private CardView loginContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        initializeViews();

        setupClickableSpan();

        emailLogin.addTextChangedListener(createTextWatcher(emailLogin));
        passwordLogin.addTextChangedListener(createTextWatcher(passwordLogin));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginButtonClick();
            }
        });
    }

    private void initializeViews() {
        emailLogin = findViewById(R.id.emailAddLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        emailTextInput = findViewById(R.id.emailAddTextInput);
        passwordLoginTextInput = findViewById(R.id.passwordLoginTextInput);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.loginButton);
        toSignUp = findViewById(R.id.signUpAcc);
        loginContainer = findViewById(R.id.loginContainer);

        int paintFlags = forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG;
        forgotPassword.setPaintFlags(paintFlags);

        loginContainer.setBackgroundResource(R.drawable.cardview);
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
                    validateEmail(editable.toString().trim());
                } else if (editText == passwordLogin) {
                    validatePassword(editable.toString().trim());
                }
            }
        };
    }

    private void handleLoginButtonClick() {
        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        boolean isEmailValid = validateEmail(email);
        boolean isPasswordValid = validatePassword(password);

        if (TextUtils.isEmpty(email)) {
            emailTextInput.setError("Email is required");
        } else {
            emailTextInput.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLoginTextInput.setError("Password is required");
        } else {
            passwordLoginTextInput.setError(null);
        }

        if (isEmailValid && isPasswordValid && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            // Successful login
            Toast.makeText(LoginScreen.this, "Please Verify your Email Address", Toast.LENGTH_SHORT).show();
            // ...
            Intent intent = new Intent(LoginScreen.this, NavigationScreen.class);
            startActivity(intent);
            finish();
        }
    }

    private void startSignUpActivity() {
        Intent intent = new Intent(LoginScreen.this, SignUpScreen.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
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

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            emailTextInput.setError("Email is required");
            return false;
        } else if (!isValidEmail(email)) {
            emailTextInput.setError("Invalid email format");
            return false;
        } else if (!isValidDomain(email)) {
            emailTextInput.setError("Invalid email domain");
            return false;
        } else {
            emailTextInput.setError(null);
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

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            passwordLoginTextInput.setError("Password is required");
            return false;
        } else {
            passwordLoginTextInput.setError(null);
            return true;
        }
    }
}
