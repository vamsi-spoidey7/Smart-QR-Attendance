package com.vamsim.vnscollegeattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vamsim.vnscollegeattendance.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class profile extends AppCompatActivity {
    TextView profileUserName,profileRollNumber,profileEmail,profilePhone;
    RelativeLayout profileContent,fetchDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

//        FirebaseDatabase.getInstance().goOnline();

        profileUserName = findViewById(R.id.profileUserName);
        profileRollNumber = findViewById(R.id.profileRollNumber);
        profileEmail = findViewById(R.id.profileEmail);
        profilePhone = findViewById(R.id.profilePhone);

        profileContent = findViewById(R.id.profileContents);
        fetchDatabase = findViewById(R.id.progressFetch);

        @SuppressLint("HardwareIds") String uid = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);
        String value = sharedPreferences.getString("lemail","");
        if(Objects.equals(value, "admin@gmail.com")){

            String name = "Admin";
            String roll = "123";
            String email = "admin@gmail.com";
            String phone = "1234567890";

            profileUserName.setText(name);
            profileRollNumber.setText(roll);
            profileEmail.setText(email);
            profilePhone.setText(phone);

            profileContent.setVisibility(View.VISIBLE);
            fetchDatabase.setVisibility(View.GONE);
        }
        else{
            DatabaseReference root = FirebaseDatabase.getInstance().getReference("users").child(uid);
            root.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        String gotUserName = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                        String gotRollNumber = Objects.requireNonNull(snapshot.child("rollnumber").getValue()).toString();
                        String gotEmail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                        String gotPhone = Objects.requireNonNull(snapshot.child("phonenumber").getValue()).toString();

                        profileUserName.setText(gotUserName);
                        profileRollNumber.setText(gotRollNumber);
                        profileEmail.setText(gotEmail);
                        profilePhone.setText(gotPhone);

                        profileContent.setVisibility(View.VISIBLE);
                        fetchDatabase.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    @Override
    public void onBackPressed() {
//        FirebaseDatabase.getInstance().goOffline();
        super.onBackPressed();
    }
}