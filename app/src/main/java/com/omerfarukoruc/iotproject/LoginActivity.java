package com.omerfarukoruc.iotproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.core.splashscreen.SplashScreen;

import java.util.Objects;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity
{
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        emailInputLayout = findViewById(R.id.email_input_layout);
        passwordInputLayout = findViewById(R.id.password_input_layout);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        Button loginButton = findViewById(R.id.login_button);
        Button forgotPasswordButton = findViewById(R.id.forgot_password_button);
        Button registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(v ->login(Objects.requireNonNull(emailInput.getText()).toString(), Objects.requireNonNull(passwordInput.getText()).toString()));
        registerButton.setOnClickListener(v -> register());
        forgotPasswordButton.setOnClickListener(v -> showForgotPasswordDialog());
    }

    private void showForgotPasswordDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your email address to reset your password");

        final EditText emailEditText = new EditText(this);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(emailEditText);

        builder.setPositiveButton("Reset", (dialog, which) ->
        {
            String email = emailEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(email))
            {
                resetPassword(email);
            }

            else
            {
                showToast("Please enter your email address.");
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resetPassword(String email)
    {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                showToast("Password reset email sent. Please check your inbox.");
            }

            else
            {
                showToast("Failed to send password reset email: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void register()
    {
        startActivity( new Intent(LoginActivity.this, RegisterActivity.class));
    }

    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void login(String email, String password)
    {
        if (validateEmail() && validatePassword())
        {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, task ->
            {
                if (task.isSuccessful())
                {
                    // Login successful, retrieve the user
                    FirebaseUser user = mAuth.getCurrentUser();
                    // TODO: Handle successful login
                }

                else
                {
                    // Login failed, display an error message
                    Toast.makeText(LoginActivity.this, "Invalid email or password. Please try again.", Toast.LENGTH_SHORT).show();
                    //TODO
                }
            });
        }

    }

    private boolean validateEmail()
    {
        String email = Objects.requireNonNull(emailInput.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();
        if (email.isEmpty())
        {
            emailInputLayout.setError("E-mail can't be empty");
            return false;
        }

        /*if (password.isEmpty())
        {
            passwordInputLayout.setError("Password can't be empty");
            return false;
        }*/

        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailInputLayout.setError("Please enter a valid email address");
            return false;
        }

        else
        {
            emailInputLayout.setError(null);
            return true;
        }
    }

    private boolean validatePassword()
    {
        String password = Objects.requireNonNull(passwordInput.getText()).toString().trim();

        if (password.isEmpty())
        {
            passwordInputLayout.setError("Field can't be empty");
            return false;
        }

        else if (!PASSWORD_PATTERN.matcher(password).matches())
        {
            passwordInputLayout.setError("Password too weak");
            return false;
        }

        else
        {
            passwordInputLayout.setError(null);
            return true;
        }
    }
}
