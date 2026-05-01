package com.example.eventmanager.ui.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.AttendeeInfo;
import com.example.eventmanager.ui.home.adapters.AttendeeAdapter;

import java.util.concurrent.Executors;

public class ManageEventActivity extends AppCompatActivity {

    private TextView tvTitle, tvDesc;
    private Button btnCancelEvent;
    private RecyclerView rvAttendees;
    private AttendeeAdapter adapter;
    private int eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_event);

        eventId = getIntent().getIntExtra("event_id", -1);
        if (eventId == -1) {
            finish();
            return;
        }

        initViews();
        loadEventDetails();
        loadAttendees();
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_manage_title);
        tvDesc = findViewById(R.id.tv_manage_desc);
        btnCancelEvent = findViewById(R.id.btn_cancel_event);
        rvAttendees = findViewById(R.id.rv_attendees);

        rvAttendees.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AttendeeAdapter(new AttendeeAdapter.OnAttendeeActionListener() {
            @Override
            public void onAccept(AttendeeInfo attendee) {
                Executors.newSingleThreadExecutor().execute(() ->
                        AppDatabase.getInstance(ManageEventActivity.this).achatDao().approveRequest(attendee.id_achat)
                );
            }

            @Override
            public void onReject(AttendeeInfo attendee) {
                new AlertDialog.Builder(ManageEventActivity.this)
                        .setTitle("Reject Request")
                        .setMessage("Remove " + attendee.prenom + " from this event?")
                        .setPositiveButton("Remove", (dialog, which) -> {
                            Executors.newSingleThreadExecutor().execute(() ->
                                    AppDatabase.getInstance(ManageEventActivity.this).achatDao().rejectRequest(attendee.id_achat)
                            );
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        rvAttendees.setAdapter(adapter);

        btnCancelEvent.setOnClickListener(v -> confirmCancelEvent());
    }

    private void loadEventDetails() {
        AppDatabase.getInstance(this).eventDao().getEventById(eventId).observe(this, event -> {
            if (event != null) {
                tvTitle.setText(event.titre);
                tvDesc.setText("Date: " + event.date + " | Location: " + event.lieu + "\n\n" + event.description);
            }
        });
    }

    private void loadAttendees() {
        AppDatabase.getInstance(this).achatDao().getAttendeesForEvent(eventId).observe(this, attendees -> {
            if (attendees != null) {
                adapter.submitList(attendees);
            }
        });
    }

    private void confirmCancelEvent() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you absolutely sure? This will delete the event and cancel all attendee tickets. This cannot be undone.")
                .setPositiveButton("DELETE EVENT", (dialog, which) -> {
                    Executors.newSingleThreadExecutor().execute(() -> {
                        // Deleting the event will automatically delete the Achats due to CASCADE foreign keys!
                        AppDatabase.getInstance(ManageEventActivity.this).eventDao().deleteEvent(eventId);
                        runOnUiThread(() -> {
                            Toast.makeText(ManageEventActivity.this, "Event Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    });
                })
                .setNegativeButton("Go Back", null)
                .show();
    }
}