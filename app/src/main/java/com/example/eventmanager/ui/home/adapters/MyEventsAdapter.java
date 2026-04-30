package com.example.eventmanager.ui.home.adapters;

import android.graphics.Color;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.data.local.entities.Event;

public class MyEventsAdapter extends ListAdapter<Event, MyEventsAdapter.VH> {

    public MyEventsAdapter() {
        super(new DiffUtil.ItemCallback<Event>() {
            public boolean areItemsTheSame(@NonNull Event a, @NonNull Event b) {
                return a.id_event == b.id_event;
            }
            public boolean areContentsTheSame(@NonNull Event a, @NonNull Event b) {
                return a.titre.equals(b.titre);
            }
        });
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myevent, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Event event = getItem(position);
        holder.tvName.setText(event.titre);
        holder.tvDate.setText("📅 " + event.date);

        if (event.is_payant) {
            holder.tvType.setText("PAID · " + event.prix + " $");
            holder.tvType.setTextColor(Color.parseColor("#C9A84C"));
            holder.tvType.setBackgroundColor(Color.parseColor("#33C9A84C"));
        } else {
            holder.tvType.setText("FREE");
            holder.tvType.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvType.setBackgroundColor(Color.parseColor("#334CAF50"));
        }

        holder.itemView.setOnClickListener(v -> {
            // Navigate to manage event (later)
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, tvType;
        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvEventName);
            tvDate = v.findViewById(R.id.tvEventDate);
            tvType = v.findViewById(R.id.tvEventType);
        }
    }
}