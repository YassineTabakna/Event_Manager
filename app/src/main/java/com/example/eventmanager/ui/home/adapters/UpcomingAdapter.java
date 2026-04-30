package com.example.eventmanager.ui.home.adapters;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.eventmanager.R;
import com.example.eventmanager.data.local.entities.Event;

public class UpcomingAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_EVENT  = 1;

    public UpcomingAdapter() {
        super(new DiffUtil.ItemCallback<Object>() {
            public boolean areItemsTheSame(@NonNull Object a, @NonNull Object b) {
                return a.equals(b);
            }
            public boolean areContentsTheSame(@NonNull Object a, @NonNull Object b) {
                return a.equals(b);
            }
        });
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
            vh.itemView.setOnClickListener(v -> {
                // Navigate to event details (later)
            });
        }
    }

    static class EventVH extends RecyclerView.ViewHolder {
        TextView tvName, tvDate;
        EventVH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvEventName);
            tvDate = v.findViewById(R.id.tvEventDate);
        }
    }
}