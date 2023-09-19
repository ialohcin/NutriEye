package com.example.nutrieye;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class CameraScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}