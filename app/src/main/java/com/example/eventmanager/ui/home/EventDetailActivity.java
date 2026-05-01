package com.example.eventmanager.ui.home;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.Achat;
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.domain.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;

public class EventDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDateTime, tvLocation, tvDescription, tvPrice;
    private Button btnAttend, btnCancel; // Added btnCancel here
    private Event currentEvent;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        sessionManager = new SessionManager(this);
        int eventId = getIntent().getIntExtra("event_id", -1);

        if (eventId == -1) {
            Toast.makeText(this, "Error loading event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadEventData(eventId);
        checkIfAlreadyAttending(eventId);
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tv_detail_title);
        tvDateTime = findViewById(R.id.tv_detail_date_time);
        tvLocation = findViewById(R.id.tv_detail_location);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvPrice = findViewById(R.id.tv_detail_price);
        btnAttend = findViewById(R.id.btn_attend);
        btnCancel = findViewById(R.id.btn_cancel); // Initialize the new button

        btnAttend.setOnClickListener(v -> {
            if (currentEvent != null) {
                showVirtualPaymentDialog();
            }
        });

        // The exact same cancellation logic used in your adapter!
        btnCancel.setOnClickListener(v -> {
            if (currentEvent == null) return;

            new AlertDialog.Builder(EventDetailActivity.this)
                    .setTitle("Cancel Ticket")
                    .setMessage("Are you sure you want to cancel your attendance to " + currentEvent.titre + "?")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        int userId = sessionManager.getUserId();
                        Executors.newSingleThreadExecutor().execute(() -> {
                            AppDatabase.getInstance(EventDetailActivity.this)
                                    .achatDao()
                                    .deleteAchat(userId, currentEvent.id_event);

                            runOnUiThread(() -> Toast.makeText(EventDetailActivity.this, "Ticket Cancelled", Toast.LENGTH_SHORT).show());
                        });
                    })
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void loadEventData(int eventId) {
        AppDatabase.getInstance(this).eventDao().getEventById(eventId).observe(this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                if (event != null) {
                    currentEvent = event;
                    tvTitle.setText(event.titre);
                    tvDescription.setText(event.description);
                    tvLocation.setText("📍 " + (event.lieu != null ? event.lieu : "TBA"));
                    String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", event.heure, event.minute);
                    tvDateTime.setText("📅 " + event.date + "  🕐 " + timeFormatted);

                    if (event.is_payant) {
                        tvPrice.setText("Price: " + event.prix + " MAD");
                    } else {
                        tvPrice.setText("Price: Free");
                    }
                }
            }
        });
    }

    private void checkIfAlreadyAttending(int eventId) {
        int userId = sessionManager.getUserId();

        // Because this returns LiveData, it will automatically trigger again if the record is deleted!
        AppDatabase.getInstance(this).achatDao().getAchatByUserAndEvent(userId, eventId).observe(this, new Observer<Achat>() {
            @Override
            public void onChanged(Achat achat) {
                if (achat != null) {
                    // They have a ticket: Disable Attend, Show Cancel
                    btnAttend.setEnabled(false);
                    btnAttend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4A5568"))); // Gray color
                    btnAttend.setText(achat.approved ? "TICKET SECURED" : "REQUEST PENDING");

                    btnCancel.setVisibility(View.VISIBLE);
                } else {
                    // They don't have a ticket (or just deleted it): Enable Attend, Hide Cancel
                    btnAttend.setEnabled(true);
                    btnAttend.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4AF37"))); // Gold
                    btnAttend.setText("ATTEND EVENT");

                    btnCancel.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showVirtualPaymentDialog() {
        String message = currentEvent.is_payant ?
                "Confirm virtual payment of " + currentEvent.prix + " for 1 ticket?" :
                "Confirm your free registration for this event?";

        new AlertDialog.Builder(this)
                .setTitle("Confirm Attendance")
                .setMessage(message)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        processPurchase();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void processPurchase() {
        int userId = sessionManager.getUserId();
        if (userId == -1) return;

        btnAttend.setEnabled(false);
        btnAttend.setText("PROCESSING...");

        Achat achat = new Achat();
        achat.id_user = userId;
        achat.id_event = currentEvent.id_event;
        achat.nbr_ticket = 1;
        achat.date_achat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        achat.montant_total = currentEvent.is_payant ? currentEvent.prix : 0.0;

        // Reminder: set this to false if you want it to go to "Pending"
        achat.approved = true;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AppDatabase.getInstance(EventDetailActivity.this).achatDao().insertAchat(achat);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EventDetailActivity.this, "Ticket secured!", Toast.LENGTH_LONG).show();
                            // Removed finish(); here so the user stays on the screen and sees the buttons update!
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnAttend.setEnabled(true);
                            btnAttend.setText("ATTEND EVENT");
                            Toast.makeText(EventDetailActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }
}