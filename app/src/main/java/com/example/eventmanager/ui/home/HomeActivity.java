package com.example.eventmanager.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.eventmanager.R;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.User;
import java.util.concurrent.Executors;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        int userId = getIntent().getIntExtra("user_id", -1);

        TextView tvAvatar      = findViewById(R.id.tvAvatar);
        TextView tvWelcomeName = findViewById(R.id.tvWelcomeName);
        TextView tvEmail       = findViewById(R.id.tvEmail);
        TextView tvUsername    = findViewById(R.id.tvUsername);
        TextView tvDob         = findViewById(R.id.tvDob);
        Button   btnLogout     = findViewById(R.id.btnLogout);

        // Load user from DB
        Executors.newSingleThreadExecutor().execute(() -> {
            User user = AppDatabase.getInstance(this)
                    .userDao()
                    .getUserById(userId);
            if (user == null) return;
            runOnUiThread(() -> {
                String initial = String.valueOf(user.prenom.charAt(0)).toUpperCase();
                tvAvatar.setText(initial);
                tvWelcomeName.setText(user.prenom + " " + user.nom);
                tvEmail.setText("✉  " + user.email);
                tvUsername.setText("@  " + user.username);
                tvDob.setText("🎂  " + user.date_naissance);
            });
        });

        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(this,
                    com.example.eventmanager.ui.login.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}