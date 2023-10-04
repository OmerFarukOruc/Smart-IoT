package com.omerfarukoruc.iotproject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity
{

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        registerButton.setOnClickListener(v -> register());

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    private void register()
    {
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();


        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
        {
            showToast("Please enter all required fields.");
            return;
        }

        if (!password.equals(confirmPassword))
        {
            showToast("Passwords do not match.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task ->
        {
            if (task.isSuccessful())
            {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null)
                {
                    String userId = user.getUid();
                    saveUserData(userId, name, email);
                }
            }
            else
            {
                showToast("Registration failed: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void saveUserData(String userId, String name, String email)
    {
        User user = new User(name, email);
        mDatabase.child("Users").child(userId).setValue(user).addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                showToast("Registration successful!");
                startActivity( new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }

            else
            {
                showToast("Failed to save user data: " + Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

    private void showToast(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
