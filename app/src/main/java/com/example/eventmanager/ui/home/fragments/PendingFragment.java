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
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.domain.EventRepository;
import com.example.eventmanager.ui.home.Searchable;
import com.example.eventmanager.ui.home.adapters.PendingAdapter;

import java.util.ArrayList;
import java.util.List;

public class PendingFragment extends Fragment implements Searchable {

    private final int userId;
    private PendingAdapter adapter;
    private EventRepository repo;
    private List<Event> allPending = new ArrayList<>();
    private TextView tvEmpty;

    public PendingFragment(int userId) { this.userId = userId; }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);

        RecyclerView rv = view.findViewById(R.id.rvPending);
        tvEmpty         = view.findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PendingAdapter();
        rv.setAdapter(adapter);

        AppDatabase db = AppDatabase.getInstance(requireContext());
        repo = new EventRepository(db.eventDao(), db.achatDao());

        repo.getPendingEvents(userId).observe(getViewLifecycleOwner(), events -> {
            allPending = events;
            adapter.submitList(events);
            tvEmpty.setVisibility(events.isEmpty() ? View.VISIBLE : View.GONE);
        });

        return view;
    }

    @Override
    public void onSearch(String query) {
        if (adapter == null) return;
        if (query.isEmpty()) {
            adapter.submitList(allPending);
            tvEmpty.setVisibility(allPending.isEmpty() ? View.VISIBLE : View.GONE);
            return;
        }
        List<Event> filtered = new ArrayList<>();
        for (Event e : allPending) {
            if (e.titre.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(e);
            }
        }
        adapter.submitList(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}