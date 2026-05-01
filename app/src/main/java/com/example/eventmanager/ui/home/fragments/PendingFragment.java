package com.example.eventmanager.ui.home.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.PendingEvent;
import com.example.eventmanager.domain.EventRepository;
import com.example.eventmanager.domain.SessionManager;
import com.example.eventmanager.ui.home.Searchable;
import com.example.eventmanager.ui.home.adapters.PendingAdapter;

import java.util.ArrayList;
import java.util.List;

public class PendingFragment extends Fragment implements Searchable {

    private int userId;
    private PendingAdapter adapter;
    private EventRepository repo;
    private List<PendingEvent> allPending = new ArrayList<>();
    private TextView tvEmpty;

    public PendingFragment() {
        // Required empty public constructor
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        // Safely get userId from SessionManager
        SessionManager sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId();

        RecyclerView rv = view.findViewById(R.id.rvPending);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PendingAdapter();
        rv.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        repo = new EventRepository(db.eventDao(), db.achatDao());

        repo.getPendingEvents(userId).observe(getViewLifecycleOwner(), events -> {
            allPending = events;
            updateUI(events);
        });

        return view;
    }

    @Override
    public void onSearch(String query) {
        if (adapter == null) return;

        if (query == null || query.isEmpty()) {
            updateUI(allPending);
            return;
        }

        List<PendingEvent> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (PendingEvent item : allPending) {
            if (item.event != null && item.event.titre != null) {
                if (item.event.titre.toLowerCase().contains(lowerQuery)) {
                    filtered.add(item);
                }
            }
        }
        updateUI(filtered);
    }

    private void updateUI(List<PendingEvent> list) {
        adapter.submitList(new ArrayList<>(list));
        tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }
}