package com.vamsim.vnscollegeattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.vamsim.vnscollegeattendance.R;
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

import java.util.Locale;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    EditText rUserName,rRollNumber,rPhoneNumber,rEmail,rPassword,rConfirmPassword;
    Button rbtn;
    TextView alreadyRegistered;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rUserName = findViewById(R.id.inputUserName);
        rRollNumber = findViewById(R.id.inputRollNumber);
        rPhoneNumber = findViewById(R.id.inputPhone);
        rEmail = findViewById(R.id.inputEmail);
        rPassword = findViewById(R.id.inputPassword);
        rConfirmPassword = findViewById(R.id.inputConfirmPassword);
        rbtn = findViewById(R.id.btnRegister);
        alreadyRegistered = findViewById(R.id.alreadyRegistered);
        progressBar = findViewById(R.id.progressBar);

        fAuth = FirebaseAuth.getInstance();


        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!validateUserName() | !validateRollNumber() | !validatePhoneNumber() | !validateEmail() | !validatePassword() | !validateConfirmPassword()){
                    return;
                }
                rUserName.onEditorAction(EditorInfo.IME_ACTION_DONE);
                rRollNumber.onEditorAction(EditorInfo.IME_ACTION_DONE);;
                rPhoneNumber.onEditorAction(EditorInfo.IME_ACTION_DONE);;
                rEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);;
                rPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);;
                rConfirmPassword.onEditorAction(EditorInfo.IME_ACTION_DONE);;

                progressBar.setVisibility(View.VISIBLE);

                String strRUserName = rUserName.getText().toString().trim();
                String strRRollNumber = rRollNumber.getText().toString().toUpperCase(Locale.ROOT).trim();
                String strRPhoneNumber = rPhoneNumber.getText().toString().trim();
                String strREmail = rEmail.getText().toString().trim();
                String strRPassword = rPassword.getText().toString().trim();

                @SuppressLint("HardwareIds") String uid = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

                Query isRegistered = FirebaseDatabase.getInstance().getReference("users");
                isRegistered.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Object isThere = snapshot.child(uid).getValue();
                        if(isThere != null){
                            Toast.makeText(RegisterActivity.this, "!!You have already created an account in this device!!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            boolean exists = false;
                            for(DataSnapshot snap : snapshot.getChildren()){
                                if(strRRollNumber.equals(Objects.requireNonNull(snap.child("rollnumber").getValue()).toString())){
                                    Toast.makeText(RegisterActivity.this, "!!An account already exists with this RollNumber!!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                    exists = true;
                                    break;
                                }
                            }
                            if(!exists){
                                fAuth.createUserWithEmailAndPassword(strREmail,strRPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            rootNode = FirebaseDatabase.getInstance();
                                            reference = rootNode.getReference("users");

                                            UserHelperClass helperClass = new UserHelperClass(strRUserName,strRRollNumber,strRPhoneNumber,strREmail,strRPassword,uid);
                                            reference.child(uid).setValue(helperClass);

                                            Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            assert user != null;
                                            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(RegisterActivity.this, "Email verification has sent to your registered email", Toast.LENGTH_SHORT).show();
                                                        startActivity( new Intent(getApplicationContext(),LoginActivity.class));
                                                    }
                                                    else{
                                                        Toast.makeText(RegisterActivity.this, "Try Again! Something Went Wrong!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    progressBar.setVisibility(View.GONE);
                                                }
                                            });
                                        }
                                        else{
                                            Toast.makeText(RegisterActivity.this, "Error ! "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });

        alreadyRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }

    public void onBackPressed() {
        Toast.makeText(RegisterActivity.this, "You cannot BackPress here", Toast.LENGTH_SHORT).show();
    }

    private Boolean validateUserName(){
        String val = rUserName.getText().toString().trim();
        if(TextUtils.isEmpty(val)){
            rUserName.setError("User Name is Required");
            return false;
        }
        else{
            rUserName.setError(null);
            return true;
        }
    }
    private Boolean validateRollNumber(){
        String val = rRollNumber.getText().toString().trim();
        if(TextUtils.isEmpty(val)){
            rRollNumber.setError("Roll Number is Required");
            return false;
        }
        else{
            rRollNumber.setError(null);
            return true;
        }
    }
    private Boolean validatePhoneNumber(){
        String val = rPhoneNumber.getText().toString().trim();
        if(TextUtils.isEmpty(val)){
            rPhoneNumber.setError("Roll Number is Required");
            return false;
        }
        else if(val.length()!=10){
            rPhoneNumber.setError("Enter valid Phone Number");
            return  false;
        }
        else{
            rPhoneNumber.setError(null);
            return true;
        }
    }
    private Boolean validateEmail(){
        String val = rEmail.getText().toString().trim();
        String emailPattern = "^[a-zA-Z0-9_.]+@[a-zA-Z-._]+?\\.[a-zA-Z]{2,}$";
        if(TextUtils.isEmpty(val)){
            rEmail.setError("Email is Required");
            return false;
        }
        else if(!val.matches(emailPattern)){
            rEmail.setError("Enter valid Domain Email");
            return  false;
        }
        else{
            rEmail.setError(null);
            return true;
        }
    }
    private Boolean validatePassword(){
        String val = rPassword.getText().toString().trim();
        String passwordVal = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                ".{4,}" +               //at least 4 characters
                "$";

        if(TextUtils.isEmpty(val)){
            rPassword.setError("Password is Required");
            return false;
        }
        else if(!val.matches(passwordVal)){
            rPassword.setError("Password too weak");
            return  false;
        }
        else{
            rPassword.setError(null);
            return true;
        }
    }
    private Boolean validateConfirmPassword(){
        String val1 = rPassword.getText().toString().trim();
        String val2 = rConfirmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(val2) || !TextUtils.equals(val1,val2)){
            rConfirmPassword.setError("Password not Matched");
            return false;
        }
        else{
            rConfirmPassword.setError(null);
            return true;
        }
    }


}