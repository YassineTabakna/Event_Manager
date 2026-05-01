package com.example.eventmanager.ui.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.domain.EventRepository;
import com.example.eventmanager.domain.SessionManager;
import com.example.eventmanager.ui.home.Searchable;
import com.example.eventmanager.ui.home.adapters.UpcomingAdapter;

import java.util.ArrayList;
import java.util.List;

public class UpcomingFragment extends Fragment implements Searchable {

    private int userId;
    private UpcomingAdapter adapter;
    private EventRepository repo;
    private List<Object> allItems = new ArrayList<>();

    public UpcomingFragment() {
        // Required empty public constructor
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        RecyclerView rv = view.findViewById(R.id.rvUpcoming);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new UpcomingAdapter(true); // Tells it to show the Cancel button!
        rv.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        repo = new EventRepository(db.eventDao(), db.achatDao());

        repo.getUpcomingEvents(userId).observe(getViewLifecycleOwner(), upcoming -> {
            repo.getPastEvents(userId).observe(getViewLifecycleOwner(), past -> {
                List<Object> merged = new ArrayList<>();
                if (!upcoming.isEmpty()) {
                    merged.add("UPCOMING");
                    merged.addAll(upcoming);
                }
                if (!past.isEmpty()) {
                    merged.add("HISTORY");
                    merged.addAll(past);
                }
                allItems = merged;
                adapter.submitList(new ArrayList<>(merged));
            });
        });

        return view;
    }

    @Override
    public void onSearch(String query) {
        if (adapter == null) return;
        if (query.isEmpty()) {
            adapter.submitList(new ArrayList<>(allItems));
            return;
        }
        List<Object> filtered = new ArrayList<>();
        for (Object item : allItems) {
            if (item instanceof String) continue; // skip headers
            Event e = (Event) item;
            if (e.titre.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        adapter.submitList(filtered);
    }
}