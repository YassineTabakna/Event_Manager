package com.example.eventmanager.ui.home;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.R;

public class CategoryEventsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // reuse activity_home layout temporarily
        setContentView(R.layout.activity_home);
        int categoryId   = getIntent().getIntExtra("category_id", -1);
        String catName   = getIntent().getStringExtra("category_name");
        // your team will develop this screen
    }
}