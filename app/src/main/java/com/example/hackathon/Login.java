package com.example.hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends Fragment {
    Button loginButton;
    EditText email, password;
    TextView forgotPassword;
    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        loginButton = view.findViewById(R.id.loginButton);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        forgotPassword = view.findViewById(R.id.forgotPassword);
//To be checked.....s
        forgotPassword.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            if (!userEmail.isEmpty()) {
                auth.sendPasswordResetEmail(userEmail)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Reset link sent to " + userEmail, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to send reset link!", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                email.setError("Please enter your email!");
            }
        });

        loginButton.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPass = password.getText().toString().trim();

            if (userEmail.isEmpty()) {
                email.setError("Please enter your email!");
                return;
            }

            if (userPass.isEmpty()) {
                password.setError("Please enter your password!");
                return;
            }

            auth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                checkUserRole(currentUser.getUid());
                            } else {
                                Toast.makeText(requireContext(), "User not found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        return view;
    }

    private void checkUserRole(String userId) {
        databaseReference.child(userId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.getValue(String.class);
                    Intent intent = null;

                    switch (role) {
                        case "organization":
                            intent = new Intent(requireContext(), Org_Home_Page.class);
                            break;
                        case "user":
                            intent = new Intent(requireContext(), User_Home_Page.class);
                            break;
                        default:
                            Toast.makeText(requireContext(), "Unknown role!", Toast.LENGTH_SHORT).show();
                            return;
                    }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Role not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Error fetching role!", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
