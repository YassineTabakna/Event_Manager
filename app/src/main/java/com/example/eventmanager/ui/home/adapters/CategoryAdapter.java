package com.example.eventmanager.ui.home.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.data.local.entities.Category;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.VH> {

    // Nice background colors for cards
    private static final String[] COLORS = {
            "#1A3A4A", "#2D1B4E", "#1A3D2B",
            "#4A1A1A", "#1A2D4A", "#3D2B1A"
    };

    public CategoryAdapter() {
        super(new DiffUtil.ItemCallback<Category>() {
            public boolean areItemsTheSame(@NonNull Category a, @NonNull Category b) {
                return a.id_category == b.id_category;
            }
            public boolean areContentsTheSame(@NonNull Category a, @NonNull Category b) {
                return a.nom.equals(b.nom) && a.description.equals(b.description);
            }
        });
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Category cat = getItem(position);
        holder.tvName.setText(cat.nom);
        holder.tvDesc.setText(cat.description);

        // Assign color based on position
        String color = COLORS[position % COLORS.length];
        holder.cardBg.setBackgroundColor(Color.parseColor(color));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(),
                    com.example.eventmanager.ui.home.CategoryEventsActivity.class);
            intent.putExtra("category_id", cat.id_category);
            intent.putExtra("category_name", cat.nom);
            v.getContext().startActivity(intent);
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        LinearLayout cardBg;
        VH(View v) {
            super(v);
            tvName  = v.findViewById(R.id.tvCategoryName);
            tvDesc  = v.findViewById(R.id.tvCategoryDesc);
            cardBg  = v.findViewById(R.id.cardBg);
        }
    }
}