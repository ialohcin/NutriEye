package com.example.nutrieye;

import android.app.ProgressDialog;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import org.jetbrains.annotations.NotNull;

public class LoginScreen extends AppCompatActivity {

    private TextInputEditText emailLogin, passwordLogin;
    private TextInputLayout emailTextInput, passwordLoginTextInput;
    private TextView forgotPassword, toSignUp;
    private MaterialButton loginButton;
    private CardView loginContainer;
    private ProgressDialog loginProgress;

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLoginButtonClick();
            }
        });
    }

    private void initComponents() {
        emailLogin = findViewById(R.id.emailAddLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        emailTextInput = findViewById(R.id.emailAddTextInput);
        passwordLoginTextInput = findViewById(R.id.passwordLoginTextInput);
        forgotPassword = findViewById(R.id.forgotPassword);
        loginButton = findViewById(R.id.loginButton);
        toSignUp = findViewById(R.id.signUpAcc);
        loginContainer = findViewById(R.id.loginContainer);
        loginProgress = new ProgressDialog(this);

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
        loginProgress.setTitle("Logging in");
        loginProgress.setMessage("Verifying User Credentials...");
        loginProgress.setCancelable(false);
        loginProgress.show();

        String email = emailLogin.getText().toString().trim();
        String password = passwordLogin.getText().toString().trim();

        boolean isEmailValid = validateEmail(email);
        boolean isPasswordValid = validatePassword(password);

        if (TextUtils.isEmpty(email)) {
            emailTextInput.setError("Email is required");
            loginProgress.dismiss();
        } else {
            emailTextInput.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLoginTextInput.setError("Password is required");
            loginProgress.dismiss();
        } else {
            passwordLoginTextInput.setError(null);
        }

        if (isEmailValid && isPasswordValid && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
           auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
               @Override
               public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                   loginProgress.dismiss();
                   if(!task.isSuccessful()){
                       Toast.makeText(LoginScreen.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                   } else {
                       if(!auth.getCurrentUser().isEmailVerified()){
                           loginProgress.dismiss();
                           Toast.makeText(LoginScreen.this, "Please verify your email first. Thank you!", Toast.LENGTH_SHORT).show();
                       } else{
                           startActivity(new Intent(LoginScreen.this, NavigationScreen.class));
                           loginProgress.dismiss();
                           finish();
                       }
                   }
               }
           });
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
