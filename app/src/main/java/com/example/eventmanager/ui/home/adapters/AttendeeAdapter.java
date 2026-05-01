package com.example.eventmanager.ui.home.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.entities.AttendeeInfo;

public class AttendeeAdapter extends ListAdapter<AttendeeInfo, AttendeeAdapter.VH> {

    public interface OnAttendeeActionListener {
        void onAccept(AttendeeInfo attendee);
        void onReject(AttendeeInfo attendee);
    }

    private final OnAttendeeActionListener listener;

    public AttendeeAdapter(OnAttendeeActionListener listener) {
        super(new DiffUtil.ItemCallback<AttendeeInfo>() {
            @Override
            public boolean areItemsTheSame(@NonNull AttendeeInfo oldItem, @NonNull AttendeeInfo newItem) {
                return oldItem.id_achat == newItem.id_achat;
            }

            @Override
            public boolean areContentsTheSame(@NonNull AttendeeInfo oldItem, @NonNull AttendeeInfo newItem) {
                return oldItem.approved == newItem.approved;
            }
        });
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AttendeeInfo info = getItem(position);

        // Display full name
        holder.tvName.setText(info.prenom + " " + info.nom);

        if (info.approved) {
            holder.tvStatus.setText("Status: APPROVED");
            holder.tvStatus.setTextColor(0xFF4CAF50); // Green
            holder.btnAccept.setVisibility(View.GONE); // Hide Accept if already approved
        } else {
            holder.tvStatus.setText("Status: PENDING");
            holder.tvStatus.setTextColor(0xFFA0AEC0); // Gray
            holder.btnAccept.setVisibility(View.VISIBLE);
        }

        holder.btnAccept.setOnClickListener(v -> listener.onAccept(info));
        holder.btnReject.setOnClickListener(v -> listener.onReject(info));
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvStatus, btnAccept, btnReject;
        VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvAttendeeName);
            tvStatus = v.findViewById(R.id.tvAttendeeStatus);
            btnAccept = v.findViewById(R.id.btnAccept);
            btnReject = v.findViewById(R.id.btnReject);
        }
    }
}