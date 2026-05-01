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
import com.example.eventmanager.data.local.entities.Category;
import com.example.eventmanager.domain.CategoryRepository;
import com.example.eventmanager.domain.SessionManager;
import com.example.eventmanager.ui.home.Searchable;
import com.example.eventmanager.ui.home.adapters.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesFragment extends Fragment implements Searchable {

    private int userId;
    private CategoryAdapter adapter;
    private CategoryRepository repo;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        SessionManager sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getUserId(); // Grabbed safely, even if not strictly needed here yet

        RecyclerView rv = view.findViewById(R.id.rvCategories);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CategoryAdapter();
        rv.setAdapter(adapter);

        repo = new CategoryRepository(
                AppDatabase.getInstance(requireContext()).categoryDao()
        );

        repo.getAllCategories().observe(getViewLifecycleOwner(), adapter::submitList);

        view.findViewById(R.id.fabAdd).setOnClickListener(v -> {
            // will be developed later
        });

        return view;
    }

    @Override
    public void onSearch(String query) {
        if (adapter == null) return;
        if (query.isEmpty()) {
            repo.getAllCategories().observe(getViewLifecycleOwner(), adapter::submitList);
            return;
        }
        repo.getAllCategories().observe(getViewLifecycleOwner(), list -> {
            List<Category> filtered = new ArrayList<>();
            for (Category c : list) {
                if (c.nom.toLowerCase().contains(query.toLowerCase()) ||
                        c.description.toLowerCase().contains(query.toLowerCase())) {
                    filtered.add(c);
                }
            }
            adapter.submitList(filtered);
        });
    }
}