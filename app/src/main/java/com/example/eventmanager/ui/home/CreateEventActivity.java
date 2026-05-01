package com.example.eventmanager.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.Category;
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.domain.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class CreateEventActivity extends AppCompatActivity {

    private EditText etTitle, etDesc, etDate, etTime, etLocation, etDuration, etMaxTickets, etPrice;
    private Spinner spinnerCategory;
    private SwitchCompat switchIsPaid;
    private Button btnPublish;

    private List<Category> categoryList = new ArrayList<>();
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        sessionManager = new SessionManager(this);
        initViews();
        loadCategories();
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_event_title);
        etDesc = findViewById(R.id.et_event_desc);
        etDate = findViewById(R.id.et_event_date);
        etTime = findViewById(R.id.et_event_time);
        etLocation = findViewById(R.id.et_event_location);
        etDuration = findViewById(R.id.et_event_duration_hrs);
        etMaxTickets = findViewById(R.id.et_event_max_tickets);
        etPrice = findViewById(R.id.et_event_price);
        spinnerCategory = findViewById(R.id.spinner_category);
        switchIsPaid = findViewById(R.id.switch_is_paid);
        btnPublish = findViewById(R.id.btn_publish_event);

        // Show/Hide price field based on switch
        switchIsPaid.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etPrice.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) etPrice.setText("");
        });

        btnPublish.setOnClickListener(v -> saveEvent());
    }

    private void loadCategories() {
        AppDatabase.getInstance(this).categoryDao().getAllCategories().observe(this, categories -> {
            if (categories != null) {
                categoryList = categories;
                List<String> categoryNames = new ArrayList<>();
                for (Category c : categories) {
                    categoryNames.add(c.nom);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, categoryNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategory.setAdapter(adapter);
            }
        });
    }

    private void saveEvent() {
        // Basic Validation
        String title = etTitle.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String timeStr = etTime.getText().toString().trim();

        if (title.isEmpty() || date.isEmpty() || timeStr.isEmpty() || categoryList.isEmpty()) {
            Toast.makeText(this, "Please fill in Title, Date, Time, and Category", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPublish.setEnabled(false);
        btnPublish.setText("SAVING...");

        Event newEvent = new Event();
        newEvent.id_user = sessionManager.getUserId();

        // Get selected category ID
        int selectedPosition = spinnerCategory.getSelectedItemPosition();
        newEvent.id_category = categoryList.get(selectedPosition).id_category;

        newEvent.titre = title;
        newEvent.description = etDesc.getText().toString().trim();
        newEvent.date = date; // Format: "YYYY-MM-DD"
        newEvent.lieu = etLocation.getText().toString().trim();

        // Parse time (Expected "HH:MM")
        try {
            String[] timeParts = timeStr.split(":");
            newEvent.heure = Integer.parseInt(timeParts[0]);
            newEvent.minute = Integer.parseInt(timeParts[1]);
        } catch (Exception e) {
            newEvent.heure = 0;
            newEvent.minute = 0;
        }

        // Parse numbers safely
        try { newEvent.duree_heure = Integer.parseInt(etDuration.getText().toString()); } catch (Exception e) { newEvent.duree_heure = 0; }
        try { newEvent.nbr_max_tickets = Integer.parseInt(etMaxTickets.getText().toString()); } catch (Exception e) { newEvent.nbr_max_tickets = 0; }

        newEvent.is_payant = switchIsPaid.isChecked();
        if (newEvent.is_payant) {
            try { newEvent.prix = Double.parseDouble(etPrice.getText().toString()); } catch (Exception e) { newEvent.prix = 0.0; }
        } else {
            newEvent.prix = 0.0;
        }

        newEvent.duree_jour = 0; // Defaulting to 0 for now

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase.getInstance(CreateEventActivity.this).eventDao().insertEvent(newEvent);
            runOnUiThread(() -> {
                Toast.makeText(CreateEventActivity.this, "Event Created!", Toast.LENGTH_SHORT).show();
                finish(); // Close screen and return to My Events
            });
        });
    }
}