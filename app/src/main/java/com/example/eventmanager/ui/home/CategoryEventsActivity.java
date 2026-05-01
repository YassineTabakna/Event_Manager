package com.example.eventmanager.ui.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.ui.home.adapters.UpcomingAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoryEventsActivity extends AppCompatActivity {

    private RecyclerView rvCategoryEvents;
    private UpcomingAdapter adapter;
    private EditText etSearch;

    // We keep a copy of ALL events here to search against
    private List<Event> allCategoryEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_events);

        int categoryId = getIntent().getIntExtra("category_id", -1);
        String catName = getIntent().getStringExtra("category_name");

        if (getSupportActionBar() != null && catName != null) {
            getSupportActionBar().setTitle(catName);
        }

        initViews();

        if (categoryId == -1) return;

        // Fetch data
        AppDatabase.getInstance(this).eventDao().getEventsByCategory(categoryId)
                .observe(this, new Observer<List<Event>>() {
                    @Override
                    public void onChanged(List<Event> events) {
                        if (events != null) {
                            allCategoryEvents = events;
                            // Trigger an initial filter in case there's already text in the search bar
                            filterEvents(etSearch.getText().toString().trim());
                        }
                    }
                });
    }

    private void initViews() {
        rvCategoryEvents = findViewById(R.id.rv_category_events);
        rvCategoryEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UpcomingAdapter();
        rvCategoryEvents.setAdapter(adapter);

        etSearch = findViewById(R.id.et_search_category_events);

        // Listen for typing in the search bar
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // This method handles the actual search logic
    private void filterEvents(String query) {
        if (query.isEmpty()) {
            processAndSubmitList(allCategoryEvents);
            return;
        }

        List<Event> filteredList = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Event e : allCategoryEvents) {
            // Search by Title OR Location
            boolean matchesTitle = e.titre != null && e.titre.toLowerCase().contains(lowerQuery);
            boolean matchesLocation = e.lieu != null && e.lieu.toLowerCase().contains(lowerQuery);

            if (matchesTitle || matchesLocation) {
                filteredList.add(e);
            }
        }

        processAndSubmitList(filteredList);
    }

    // Your existing processing logic (Upcoming vs History) stays exactly the same
    private void processAndSubmitList(List<Event> eventsToDisplay) {
        List<Object> displayItems = new ArrayList<>();
        List<Event> upcomingList = new ArrayList<>();
        List<Event> historyList = new ArrayList<>();

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        for (Event event : eventsToDisplay) {
            if (event.date != null) {
                if (event.date.compareTo(today) >= 0) {
                    upcomingList.add(event);
                } else {
                    historyList.add(event);
                }
            }
        }

        if (!upcomingList.isEmpty()) {
            displayItems.add("UPCOMING");
            displayItems.addAll(upcomingList);
        }

        if (!historyList.isEmpty()) {
            displayItems.add("HISTORY");
            displayItems.addAll(historyList);
        }

        adapter.submitList(displayItems);
    }
}