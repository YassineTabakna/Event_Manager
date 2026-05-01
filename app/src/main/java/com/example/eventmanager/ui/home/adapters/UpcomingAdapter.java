package com.example.eventmanager.ui.home.adapters;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.domain.SessionManager;
import com.example.eventmanager.ui.home.EventDetailActivity;

import java.util.concurrent.Executors;

public class UpcomingAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EVENT  = 1;

    private boolean isMyTicketsScreen; // Flag to show/hide the cancel button

    // Use this constructor for "My Tickets"
    public UpcomingAdapter(boolean isMyTicketsScreen) {
        super(new DiffUtil.ItemCallback<Object>() {
            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                if (oldItem instanceof Event && newItem instanceof Event) {
                    return ((Event) oldItem).id_event == ((Event) newItem).id_event;
                }
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.isMyTicketsScreen = isMyTicketsScreen;
    }

    // Default constructor for Category Events screen (hides cancel button)
    public UpcomingAdapter() {
        this(false);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position) instanceof String ? TYPE_HEADER : TYPE_EVENT;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            TextView tv = new TextView(parent.getContext());
            tv.setPadding(0, 24, 0, 8);
            tv.setTextColor(0xFFC9A84C);
            tv.setTextSize(11f);
            tv.setLetterSpacing(0.12f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            return new RecyclerView.ViewHolder(tv) {};
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming, parent, false);
        return new EventVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_HEADER) {
            ((TextView) holder.itemView).setText((String) getItem(position));
        } else {
            Event event = (Event) getItem(position);
            EventVH vh = (EventVH) holder;
            vh.tvName.setText(event.titre);
            vh.tvDate.setText("📅 " + event.date + "  🕐 "
                    + String.format("%02d:%02d", event.heure, event.minute));

            // Show or hide the Cancel button based on where the adapter is being used
            vh.btnCancel.setVisibility(isMyTicketsScreen ? View.VISIBLE : View.GONE);

            // Handle the Cancel Logic
            vh.btnCancel.setOnClickListener(v -> {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Cancel Ticket")
                        .setMessage("Are you sure you want to cancel your attendance to " + event.titre + "?")
                        .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                            SessionManager session = new SessionManager(v.getContext());
                            int userId = session.getUserId();

                            Executors.newSingleThreadExecutor().execute(() -> {
                                AppDatabase.getInstance(v.getContext()).achatDao().deleteAchat(userId, event.id_event);
                            });
                            Toast.makeText(v.getContext(), "Ticket Cancelled", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            });

            vh.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), EventDetailActivity.class);
                intent.putExtra("event_id", event.id_event);
                v.getContext().startActivity(intent);
            });
        }
    }

    static class EventVH extends RecyclerView.ViewHolder {
        TextView tvName, tvDate, btnCancel;
        EventVH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvEventName);
            tvDate = v.findViewById(R.id.tvEventDate);
            btnCancel = v.findViewById(R.id.btnCancelTicket);
        }
    }
}