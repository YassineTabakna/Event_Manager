package com.example.eventmanager.ui.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.Plan;
import com.example.eventmanager.data.local.entities.Subscription;
import com.example.eventmanager.data.local.entities.User;
import com.example.eventmanager.domain.SessionManager;
import com.example.eventmanager.ui.login.LoginActivity;

import java.util.concurrent.Executors;

public class ProfileActivity extends AppCompatActivity {

    private int userId;
    private EditText etPrenom, etNom, etUsername, etEmail, etDob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = getIntent().getIntExtra("user_id", -1);

        TextView tvBack    = findViewById(R.id.tvBack);
        TextView tvAvatar  = findViewById(R.id.tvAvatar);
        TextView tvName    = findViewById(R.id.tvFullName);
        TextView tvPlanName   = findViewById(R.id.tvPlanName);
        TextView tvPlanBadge  = findViewById(R.id.tvPlanBadge);
        TextView tvPlanDetails = findViewById(R.id.tvPlanDetails);
        Button   btnSave   = findViewById(R.id.btnSave);
        Button   btnLogout = findViewById(R.id.btnLogout);

        etPrenom   = findViewById(R.id.etPrenom);
        etNom      = findViewById(R.id.etNom);
        etUsername = findViewById(R.id.etUsername);
        etEmail    = findViewById(R.id.etEmail);
        etDob      = findViewById(R.id.etDob);

        tvBack.setOnClickListener(v -> finish());

        // Load user + subscription
        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(this);
            User user = db.userDao().getUserById(userId);
            if (user == null) return;

            Subscription sub  = db.subscriptionDao().getActiveSubscription(userId);
            Plan plan = null;
            if (sub != null) plan = db.planDao().getPlanById(sub.id_plan);

            final Plan finalPlan = plan;
            final Subscription finalSub = sub;

            runOnUiThread(() -> {
                // Fill fields
                String initial = String.valueOf(user.prenom.charAt(0)).toUpperCase();
                tvAvatar.setText(initial);
                tvName.setText(user.prenom + " " + user.nom);
                etPrenom.setText(user.prenom);
                etNom.setText(user.nom);
                etUsername.setText(user.username);
                etEmail.setText(user.email);
                etDob.setText(user.date_naissance);

                // Subscription card
                if (finalPlan == null) {
                    tvPlanName.setText("Free");
                    tvPlanBadge.setText("FREE");
                    tvPlanBadge.setTextColor(Color.parseColor("#4CAF50"));
                    tvPlanBadge.setBackgroundColor(Color.parseColor("#334CAF50"));
                    tvPlanDetails.setText(
                            "• No active subscription\n" +
                                    "• Limited event creation\n" +
                                    "• Paid events not available"
                    );
                } else {
                    tvPlanName.setText(finalPlan.nom);
                    tvPlanBadge.setText("ACTIVE");
                    tvPlanBadge.setTextColor(Color.parseColor("#C9A84C"));
                    tvPlanBadge.setBackgroundColor(Color.parseColor("#33C9A84C"));
                    tvPlanDetails.setText(
                            "• Max events : " + finalPlan.max_event + "\n" +
                                    "• Paid events : " + (finalPlan.event_payant ? "Yes" : "No") + "\n" +
                                    "• Valid until : " + finalSub.date_fin + "\n" +
                                    "• Duration : " + finalPlan.duree_jour + " days"
                    );
                }
            });
        });

        // Save changes
        btnSave.setOnClickListener(v -> {
            String prenom   = etPrenom.getText().toString().trim();
            String nom      = etNom.getText().toString().trim();
            String username = etUsername.getText().toString().trim();
            String email    = etEmail.getText().toString().trim();
            String dob      = etDob.getText().toString().trim();

            if (prenom.isEmpty() || nom.isEmpty() ||
                    username.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db   = AppDatabase.getInstance(this);
                User user        = db.userDao().getUserById(userId);
                user.prenom      = prenom;
                user.nom         = nom;
                user.username    = username;
                user.email       = email;
                user.date_naissance = dob;
                db.userDao().updateUser(user);
                runOnUiThread(() ->
                        Toast.makeText(this, "Profile updated ✓", Toast.LENGTH_SHORT).show()
                );
            });
        });

        // REPLACE the logout click
        btnLogout.setOnClickListener(v -> {
            new SessionManager(this).clearSession(); // ← ADD this
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}