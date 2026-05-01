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
import com.example.eventmanager.ui.home.adapters.MyEventsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyEventsFragment extends Fragment implements Searchable {

    private int userId;
    private MyEventsAdapter adapter;
    private EventRepository repo;
    private List<Event> allEvents = new ArrayList<>();

    public MyEventsFragment() {
        // Required empty public constructor
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myevents, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        RecyclerView rv = view.findViewById(R.id.rvMyEvents);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MyEventsAdapter();
        rv.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        repo = new EventRepository(db.eventDao(), db.achatDao());

        repo.getEventsByUser(userId).observe(getViewLifecycleOwner(), events -> {
            allEvents = events;
            adapter.submitList(events);
        });

        view.findViewById(R.id.fabCreate).setOnClickListener(v -> {
            // Launch the new Create Event screen
            android.content.Intent intent = new android.content.Intent(getContext(), com.example.eventmanager.ui.home.CreateEventActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onSearch(String query) {
        if (adapter == null) return;
        if (query.isEmpty()) {
            adapter.submitList(new ArrayList<>(allEvents));
            return;
        }
        List<Event> filtered = new ArrayList<>();
        for (Event e : allEvents) {
            if (e.titre.toLowerCase().contains(query.toLowerCase()) ||
                    e.lieu.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        adapter.submitList(filtered);
    }
}