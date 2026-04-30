package com.example.eventmanager.ui.home.adapters;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.data.local.entities.Event;

public class PendingAdapter extends ListAdapter<Event, PendingAdapter.VH> {

    public PendingAdapter() {
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
                .inflate(R.layout.item_pending, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Event event = getItem(position);
        holder.tvName.setText(event.titre);
        holder.tvEventDate.setText("📅 Event date : " + event.date);
        // Applied date comes from achat — placeholder for now
        holder.tvApplied.setText("🕐 Applied on : " + event.date);

        holder.itemView.setOnClickListener(v -> {
            // Navigate to event details (later)
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvEventDate, tvApplied;
        VH(View v) {
            super(v);
            tvName      = v.findViewById(R.id.tvEventName);
            tvEventDate = v.findViewById(R.id.tvEventDate);
            tvApplied   = v.findViewById(R.id.tvAppliedDate);
        }
    }
}