package com.vamsim.vnscollegeattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class qrscanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    ZXingScannerView scannerView;
    String username,rollnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

//        FirebaseDatabase.getInstance().goOnline();
        Log.d("vamsi", "onDataChange: oncreate ra");

        @SuppressLint("HardwareIds") String uid = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference("users").child(uid);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                username = Objects.requireNonNull(snapshot.child("username").getValue()).toString();
                rollnumber = Objects.requireNonNull(snapshot.child("rollnumber").getValue()).toString();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Dexter.withContext(getApplicationContext()).withPermission(Manifest.permission.CAMERA).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                scannerView.startCamera();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Toast.makeText(qrscanner.this, "Allow camera permission to scan qr", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();


    }

    @Override
    public void handleResult(Result rawResult) {

        Log.d("vamsi", "onDataChange: handle ra");

        @SuppressLint("HardwareIds") String uid = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        String studentqr = rawResult.getText();
        if(studentqr.length()>48){
            String[] det = studentqr.split("/");
            String teacherid = det[0];
            String teacherName = det[2];
            String className = det[3];
            DatabaseReference rootqr = FirebaseDatabase.getInstance().getReference("Admin").child(teacherid).child("qr");
            rootqr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String classqr = Objects.requireNonNull(snapshot.child("qrdata").getValue()).toString();
                        if(TextUtils.equals(classqr,studentqr)){
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfMonth = new SimpleDateFormat("MMMM");
                            Date date = new Date();
                            DatabaseReference userdata = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Attendance").child(teacherName).child(className).child(sdfYear.format(date)).child(sdfMonth.format(date)).child(sdfDay.format(date));
                            userdata.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        Toast.makeText(qrscanner.this, "You have already marked your Attendance", Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                        userdata.child("Date").setValue(sdfDay.format(date));
                                        userdata.child("Time").setValue(sdfTime.format(date));
                                        userdata.child("Status").setValue("Present");

                                        Toast.makeText(qrscanner.this, "Your Attendance is Marked Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        else{
                            Toast.makeText(qrscanner.this, "Scan Again for Attendance", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(qrscanner.this, "No Live Class Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            Toast.makeText(qrscanner.this, "Not a valid qr for attendance", Toast.LENGTH_SHORT).show();
        }
        onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }
}