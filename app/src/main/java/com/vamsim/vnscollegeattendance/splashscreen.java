package com.vamsim.vnscollegeattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;



@SuppressLint("CustomSplashScreen")
public class splashscreen extends AppCompatActivity implements LifecycleObserver {


    private static final int APP_PACKAGE_DOT_COUNT = 2; // number of dots present in package name
    private static final String DUAL_APP_ID_999 = "999";
    private static final char DOT = '.';


    private void checkAppCloning()
    {
        String path = getFilesDir().getPath();
        if (path.contains(DUAL_APP_ID_999))
        {
            killProcess();
        } else
        {
            int count = getDotCount(path);
            if (count > APP_PACKAGE_DOT_COUNT)
            {
                killProcess();
            }
        }
    }

    private int getDotCount(String path)
    {
        int count = 0;
        for (int i = 0; i < path.length(); i++)
        {
            if (count > APP_PACKAGE_DOT_COUNT)
            {
                break;
            }
            if (path.charAt(i) == DOT)
            {
                count++;
            }
        }
        return count;
    }

    private void killProcess()
    {
        Toast.makeText(splashscreen.this, "You cannot clone this App", Toast.LENGTH_SHORT).show();
        finish();
        System.exit(0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        ProcessLifecycleOwner.get().getLifecycle().addObserver(new splashscreen());

        checkAppCloning();
        new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(),LoginActivity.class)),2000);
    }




    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onAppDidEnterForeground() {
        FirebaseDatabase.getInstance().goOnline();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onAppDidEnterBackground() {
        FirebaseDatabase.getInstance().goOffline();
    }
}