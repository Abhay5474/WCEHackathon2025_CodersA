package com.example.miniproject;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hackathon.Login;
import com.example.hackathon.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class SignUp extends Fragment {
    private Button registerButton;
    private EditText mail, pass, confirmPass;
    private TextView passtxt;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        registerButton = view.findViewById(R.id.registerButton);
        mail = view.findViewById(R.id.email);
        pass = view.findViewById(R.id.password);
        confirmPass = view.findViewById(R.id.confirmPassword);
        passtxt = view.findViewById(R.id.passtxt);

        registerButton.setOnClickListener(v -> registerUser());
        return view;
    }

    private void registerUser() {
        passtxt.setVisibility(View.INVISIBLE);
        String email = mail.getText().toString().trim();
        String password = pass.getText().toString().trim();
        String confirmPassword = confirmPass.getText().toString().trim();

        if (!validateInput(email, password, confirmPassword)) {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> handleRegistration(task, email));
    }

    private boolean validateInput(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            mail.setError("Please enter email!");
            mail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mail.setError("Enter a valid email!");
            mail.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            pass.setError("Please enter password!");
            pass.requestFocus();
            return false;
        }
        if (password.length() < 8) {
            pass.setError("Password must be at least 8 characters.");
            pass.requestFocus();
            return false;
        }
        if (!password.equals(confirmPassword)) {
            passtxt.setVisibility(View.VISIBLE);
            passtxt.setText("Passwords Didn't Match!");
            passtxt.setTextColor(Color.RED);
            passtxt.setTextSize(20);
            passtxt.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            return false;
        }
        return true;
    }

    private void handleRegistration(Task<AuthResult> task, String email) {
        if (task.isSuccessful()) {
            FirebaseUser firebaseUser = auth.getCurrentUser();
            if (firebaseUser != null) {
                saveUserData(firebaseUser.getUid(), email);
            }
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.landingFragment, new Login())
                    .commit();
        } else {
            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Registration failed!";
            Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
        }
    }

    private void saveUserData(String userId, String email) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("role", "user");

        databaseReference.child(userId).setValue(userMap)
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to save user data!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
