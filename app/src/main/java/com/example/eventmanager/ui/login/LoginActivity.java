package com.example.eventmanager.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventmanager.R;
import com.example.eventmanager.domain.SessionManager;
import com.example.eventmanager.ui.home.HomeActivity;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
        // XML -> Activty(View) -> ViewModel -> repository -> Dao -> Entity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // If already logged in → go directly to Home
        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("user_id", session.getUserId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        EditText etEmail    = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        Button   btnLogin   = findViewById(R.id.btnLogin);
        TextView btnGoRegister = findViewById(R.id.btnGoRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString().trim();
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.login(email, pass);
        });

        btnGoRegister.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );

        // REPLACE the loginResult observer
        viewModel.loginResult.observe(this, user -> {
            session.saveSession(user.id_user); // ← ADD this line
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra("user_id", user.id_user);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        viewModel.errorMessage.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }
}