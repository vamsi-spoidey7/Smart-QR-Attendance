package com.vamsim.vnscollegeattendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vamsim.vnscollegeattendance.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class studentAttendance extends AppCompatActivity {
    Spinner monthSpinner,yearSpinner,classSpinner,teacherSpinner;
    RecyclerView recyclerView;
    String[] months = {"Select Month","January","February","March","April","May","June","July","August","September","October","November","December"};
    String[] years = {"Select Year","2022","2023","2024","2025","2026","2027"};
    List<String> classes = new ArrayList<>();
    List<String> teachers = new ArrayList<>();
    String yearValue,monthValue,classValue,teacherName;
    TextView nodata;
    Button getAttendance;
    myAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance);

//        FirebaseDatabase.getInstance().goOnline();

        @SuppressLint("HardwareIds") String uid = Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);

        monthSpinner=findViewById(R.id.monthSpinner);
        yearSpinner = findViewById(R.id.yearSpinner);
        classSpinner = findViewById(R.id.classspinner);
        teacherSpinner = findViewById(R.id.teacherSpinner);
        getAttendance = findViewById(R.id.getAttendance);
        nodata = findViewById(R.id.nodata);

        recyclerView = findViewById(R.id.userAttendanceList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(studentAttendance.this, android.R.layout.simple_spinner_item,months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>(studentAttendance.this, android.R.layout.simple_spinner_item,years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        teachers.add(0, "Select Teacher");
        DatabaseReference teacherdb = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Attendance");
        teacherdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot d : snapshot.getChildren()) {
                        teachers.add(d.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        classes.add(0, "Select Class");

        ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(studentAttendance.this, android.R.layout.simple_spinner_item, teachers);
        teacherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(teacherAdapter);

        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(studentAttendance.this, android.R.layout.simple_spinner_item, classes);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        classSpinner.setAdapter(classAdapter);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView)parent.getChildAt(0)).setTextSize(17);
                monthValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView)parent.getChildAt(0)).setTextSize(17);
                yearValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        teacherSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView)parent.getChildAt(0)).setTextSize(17);
                    teacherName = parent.getItemAtPosition(position).toString();

                    DatabaseReference classdb = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Attendance").child(teacherName);
                    classdb.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                classes.clear();
                                classes.add(0, "Select Class");
                                classSpinner.setAdapter(classAdapter);
                                for(DataSnapshot d : snapshot.getChildren()) {
                                    classes.add(d.getKey());
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView)parent.getChildAt(0)).setTextSize(17);
                classValue = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getAttendance.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                if(yearValue.equals("Select Year")){
                    Toast.makeText(studentAttendance.this, "Select the Year", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(monthValue.equals("Select Month")){
                    Toast.makeText(studentAttendance.this, "Select the Month", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (teacherName.equals("Select Teacher")){
                    Toast.makeText(studentAttendance.this, "Select the Teacher Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(classValue.equals("Select Class")){
                    Toast.makeText(studentAttendance.this, "Select the Class", Toast.LENGTH_SHORT).show();
                    return;
                }
                Query dbref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("Attendance").child(teacherName).child(classValue).child(yearValue).child(monthValue);
                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            nodata.setVisibility(View.GONE);
                        }
                        else{
                            nodata.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                FirebaseRecyclerOptions<model> options = new FirebaseRecyclerOptions.Builder<model>().setQuery(dbref, model.class).build();
                Log.d("vamsi", "onClick: "+options);
                adapter = new myAdapter(options);
                adapter.startListening();
                recyclerView.setAdapter(adapter);
                Log.d("vamsi", "onClick: "+recyclerView);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}