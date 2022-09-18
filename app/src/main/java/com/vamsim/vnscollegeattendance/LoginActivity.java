package com.vamsim.vnscollegeattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText lemail,lPassword;
    Button loginbtn;
    ProgressBar progressBar;
    TextView dontHaveAccount,forgetPassword;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        lemail = findViewById(R.id.inputLoginEmail);
        lPassword = findViewById(R.id.inputLoginPassword);
        loginbtn = findViewById(R.id.loginbtn);
        progressBar = findViewById(R.id.progressBar2);
        dontHaveAccount = findViewById(R.id.donthaveaccount);
        forgetPassword = findViewById(R.id.forgetPassword);
        fAuth = FirebaseAuth.getInstance();

        @SuppressLint("HardwareIds") String uid = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        if(fAuth.getCurrentUser() != null){
            if(fAuth.getCurrentUser().isEmailVerified()){
                startActivity( new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        }


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();

                if(Objects.equals(lemail.getText().toString(), "admin@gmail.com") && lPassword.getText().toString().equals("admin123")){
                    editor.putString("lemail", "admin@gmail.com");
                    editor.apply();
                    startActivity( new Intent(getApplicationContext(),MainActivity.class));
                    return;
                }

                editor.clear().apply();

                if(!validateEmail() | !validatePassword()){
                    return;
                }

                lemail.onEditorAction(EditorInfo.IME_ACTION_DONE);
                lPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);

                String strLEmail = lemail.getText().toString().trim();
                String strLPassword = lPassword.getText().toString().trim();

                progressBar.setVisibility(View.VISIBLE);


                Query isRegistered = FirebaseDatabase.getInstance().getReference("users").child(uid);
                isRegistered.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Object actEmail = snapshot.child("email").getValue();
                        if(actEmail==null){
                            Toast.makeText(LoginActivity.this, "Please create an account with this device", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else{
                            if(TextUtils.equals(actEmail.toString(),strLEmail)){
                                fAuth.signInWithEmailAndPassword(strLEmail,strLPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            assert user != null;
                                            if(user.isEmailVerified()){
                                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity( new Intent(getApplicationContext(),MainActivity.class));
                                            }
                                            else{
                                                Toast.makeText(LoginActivity.this, "Verify your email to Login", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        else{
                                            Toast.makeText(LoginActivity.this, "Error ! "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                            }
                            else{
                                Toast.makeText(LoginActivity.this, "Please login with account registered in this device", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

        dontHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);

                DatabaseReference passwordChange = FirebaseDatabase.getInstance().getReference("users").child(uid);
                passwordChange.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String actEmail = Objects.requireNonNull(snapshot.child("email").getValue()).toString();
                            fAuth.sendPasswordResetEmail(actEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(LoginActivity.this, "Reset password link has sent to registered email!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    else{
                                        Toast.makeText(LoginActivity.this, "Try Again! Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Please create an account with this device", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }
        });

    }

    private Boolean validateEmail(){
        String val = lemail.getText().toString().trim();
        String emailPattern = "^[a-zA-Z0-9_.]+@[a-zA-Z-._]+?\\.[a-zA-Z]{2,}$";
        if(TextUtils.isEmpty(val)){
            lemail.setError("Email is Required");
            return false;
        }
        else if(!val.matches(emailPattern)){
            lemail.setError("Enter valid Domain Email");
            return  false;
        }
        else{
            lemail.setError(null);
            return true;
        }
    }

    public void onBackPressed() {
        Toast.makeText(LoginActivity.this, "You cannot BackPress here", Toast.LENGTH_SHORT).show();
    }

    private Boolean validatePassword(){
        String val = lPassword.getText().toString().trim();
        if(TextUtils.isEmpty(val)){
            lPassword.setError("Password is required");
            return false;
        }
        else{
            lPassword.setError(null);
            return true;
        }
    }
}