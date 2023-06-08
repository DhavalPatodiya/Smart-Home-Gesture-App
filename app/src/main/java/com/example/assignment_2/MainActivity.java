package com.example.assignment_2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {

    public static final String GESTURENAME = "gestureName";
    public Spinner spinner;
    public HashMap<String, String> gestureList = new HashMap<String,String>() {{
        put("LightOn","h_lighton");
        put("LightOff","h_lightoff");
        put("FanOn","h_fanon");
        put("FanOff","h_fanoff");
        put("FanUp","h_increasefanspeed");
        put("FanDown","h_decreasefanspeed");
        put("SetThermo","h_setthermo");
        put("Num0","h0");
        put("Num1","h1");
        put("Num2","h2");
        put("Num3","h3");
        put("Num4","h4");
        put("Num5","h5");
        put("Num6","h6");
        put("Num7","h7");
        put("Num8","h8");
        put("Num9","h9");
    }};

    public static final String GESTUREVIDEONAME = "GestureVideoName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configure_permissions();
        List<String> gestures = new ArrayList<String>(gestureList.keySet());
        Collections.sort(gestures);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, gestures);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    public void onClickPracticeGesture(View view){
        Intent intent = new Intent(this, PracticeGesture.class);
        String selectedItem = spinner.getSelectedItem().toString();
        if(gestureList.containsKey(selectedItem)){
            intent.putExtra(GESTUREVIDEONAME,gestureList.get(selectedItem));
            intent.putExtra(GESTURENAME,selectedItem);
        }
        startActivity(intent);
    }

    void configure_permissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 10);
            }

            int storagePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int REQUEST_EXTERNAL_STORAGE = 1;

            String[] PERMISSIONS = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
            };

            if (storagePermission != PackageManager.PERMISSION_GRANTED) {
                Log.i("log", "Read/Write Permissions needed!");
            }

            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS,
                    REQUEST_EXTERNAL_STORAGE
            );

            return;
        }
    }

}