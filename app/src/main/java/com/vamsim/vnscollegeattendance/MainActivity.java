package com.vamsim.vnscollegeattendance;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    CardView cardLogout,cardQRScanner,cardProfile,cardAttendance,cardAbout;
    private AppUpdateManager appUpdateManager;
    private static final int RC_APP_UPDATE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,MainActivity.this,RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

        cardLogout = findViewById(R.id.logout);
        cardQRScanner = findViewById(R.id.scanqr);
        cardProfile = findViewById(R.id.profile);
        cardAttendance = findViewById(R.id.attendance);
        cardAbout = findViewById(R.id.aboutus);

        cardQRScanner.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),qrscanner.class)));

        cardProfile.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),profile.class)));

        cardLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        });

        cardAttendance.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),studentAttendance.class)));

        cardAbout.setOnClickListener(v -> startActivity((new Intent(getApplicationContext(),aboutus.class))));
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == RC_APP_UPDATE && resultCode != RESULT_OK){
            Toast.makeText(MainActivity.this, "Please Update the app", Toast.LENGTH_SHORT).show();
            finish();
            System.exit(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if(appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS){
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,AppUpdateType.IMMEDIATE,MainActivity.this,RC_APP_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(MainActivity.this, "Click Logout to exit", Toast.LENGTH_SHORT).show();
    }

}