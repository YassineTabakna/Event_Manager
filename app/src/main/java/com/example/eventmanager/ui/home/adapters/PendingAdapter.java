package com.example.eventmanager.ui.home.adapters;

import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.entities.PendingEvent;
import com.example.eventmanager.ui.home.EventDetailActivity;

public class PendingAdapter extends ListAdapter<PendingEvent, PendingAdapter.VH> {

    public PendingAdapter() {
        super(new DiffUtil.ItemCallback<PendingEvent>() {
            public boolean areItemsTheSame(@NonNull PendingEvent a, @NonNull PendingEvent b) {
                return a.event.id_event == b.event.id_event;
            }
            public boolean areContentsTheSame(@NonNull PendingEvent a, @NonNull PendingEvent b) {
                return a.event.titre.equals(b.event.titre) && a.date_achat.equals(b.date_achat);
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
        PendingEvent pendingItem = getItem(position);

        holder.tvName.setText(pendingItem.event.titre);
        holder.tvEventDate.setText("📅 Event date : " + pendingItem.event.date);

        // The placeholder is fixed!
        holder.tvApplied.setText("🕐 Applied on : " + pendingItem.date_achat);

        holder.itemView.setOnClickListener(v -> {
            // Navigate to event details
            Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
            intent.putExtra("event_id", pendingItem.event.id_event);
            v.getContext().startActivity(intent);
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