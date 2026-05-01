package com.example.eventmanager.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.DataSeeder;
import com.example.eventmanager.ui.home.fragments.CategoriesFragment;
import com.example.eventmanager.ui.home.fragments.MyEventsFragment;
import com.example.eventmanager.ui.home.fragments.PendingFragment;
import com.example.eventmanager.ui.home.fragments.UpcomingFragment;

public class HomeActivity extends AppCompatActivity {

    private int userId;
    private TextView tvTitle;
    private EditText etSearch;
    private boolean searchVisible = false;
    private long backPressedTime = 0;

    private ImageButton tabCategories, tabPending, tabUpcoming, tabMyEvents;
    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity(); // close entire app
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSharedPreferences("seeder_prefs", MODE_PRIVATE)
                //.edit().clear().apply();
        DataSeeder.seedIfNeeded(this);
        setContentView(R.layout.activity_home);

        userId = getIntent().getIntExtra("user_id", -1);

        tvTitle       = findViewById(R.id.tvFragmentTitle);
        etSearch      = findViewById(R.id.etSearch);
        tabCategories = findViewById(R.id.tabCategories);
        tabPending    = findViewById(R.id.tabPending);
        tabUpcoming   = findViewById(R.id.tabUpcoming);
        tabMyEvents   = findViewById(R.id.tabMyEvents);

        // Settings
        // REPLACE the Toast in btnSettings click
        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
        });

        // Search toggle
        findViewById(R.id.btnSearch).setOnClickListener(v -> {
            searchVisible = !searchVisible;
            etSearch.setVisibility(searchVisible ? View.VISIBLE : View.GONE);
            if (searchVisible) etSearch.requestFocus();
            // adjust fragment container margin
            FrameLayout container = findViewById(R.id.fragmentContainer);
            container.setPadding(0, searchVisible ? dpToPx(56) : 0, 0, 0);
        });
        // ADD this after etSearch is found
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            public void onTextChanged(CharSequence s, int i, int b, int c) {
                // forward query to current fragment
                Fragment current = getSupportFragmentManager()
                        .findFragmentById(R.id.fragmentContainer);
                if (current instanceof Searchable) {
                    ((Searchable) current).onSearch(s.toString().trim());
                }
            }
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Tabs
        tabCategories.setOnClickListener(v -> showFragment(new CategoriesFragment(), "Categories"));
        tabPending.setOnClickListener(v    -> showFragment(new PendingFragment(),    "Requests"));
        tabUpcoming.setOnClickListener(v   -> showFragment(new UpcomingFragment(),   "My Tickets"));
        tabMyEvents.setOnClickListener(v   -> showFragment(new MyEventsFragment(),   "My Events"));

        // Default tab
        showFragment(new CategoriesFragment(), "Categories");
    }

    private void showFragment(Fragment fragment, String title) {
        tvTitle.setText(title);
        updateTabColors(title);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    private void updateTabColors(String active) {
        int gold  = getResources().getColor(R.color.gold);
        int white = getResources().getColor(R.color.white_60);
        tabCategories.setColorFilter(active.equals("Categories") ? gold : white);
        tabPending.setColorFilter(active.equals("Requests")    ? gold : white);
        tabUpcoming.setColorFilter(active.equals("My Tickets") ? gold : white);
        tabMyEvents.setColorFilter(active.equals("My Events")  ? gold : white);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}