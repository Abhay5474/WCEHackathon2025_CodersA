package com.example.hackathon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.miniproject.SignUp;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity {
BottomNavigationView loginnav;
FirebaseAuth auth;
DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginnav = (BottomNavigationView) findViewById(R.id.loginOrSignUp);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("Users");
        FirebaseUser user = auth.getCurrentUser();
        if(user != null){
            redirectUser(user);
        }
        loginnav.setOnItemSelectedListener(item -> {
            if(item.getItemId()==R.id.login)
                getSupportFragmentManager().beginTransaction().replace(R.id.landingFragment, new Login()).commit();
            if(item.getItemId()==R.id.signup)
                getSupportFragmentManager().beginTransaction().replace(R.id.landingFragment, new SignUp()).commit();
            return true;
        });
    }
    private void redirectUser(FirebaseUser user) {
        database.child(user.getUid()).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String role = snapshot.getValue(String.class);
                    Intent intent;
                    switch (role) {
                        case "organization":
                            intent = new Intent(MainActivity.this, Org_Home_Page.class);
                            break;
                        default:
                            intent = new Intent(MainActivity.this, User_Home_Page.class);
                            break;
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle potential errors
            }
        });
    }
}