package com.example.eventmanager.ui.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.eventmanager.R;
import com.example.eventmanager.domain.EmailService;

import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private final EmailService emailService = new EmailService();
    private String generatedCode;

    // ── UI refs ──────────────────────────────────────────────
    private EditText etNom, etPrenom, etEmail, etUsername, etPassword, etDate;
    private TextView tvPasswordStrength;
    private Button   btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        etNom      = findViewById(R.id.etNom);
        etPrenom   = findViewById(R.id.etPrenom);
        etEmail    = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etDate     = findViewById(R.id.etDate);
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        btnRegister = findViewById(R.id.btnRegister);
        TextView tvBack = findViewById(R.id.tvBack);
        tvBack.setOnClickListener(v -> finish());

        // ── Password strength watcher ─────────────────────────
        etPassword.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            public void onTextChanged(CharSequence s, int i, int b, int c) {}
            public void afterTextChanged(Editable s) {
                updatePasswordStrength(s.toString());
            }
        });

        // ── Register button ───────────────────────────────────
        btnRegister.setOnClickListener(v -> {
            if (!validateFields()) return;
            if (!isPasswordStrong(etPassword.getText().toString())) {
                Toast.makeText(this,
                        "Mot de passe trop faible",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            sendCodeAndShowDialog();
        });

        // ── Observers ─────────────────────────────────────────
        viewModel.registerResult.observe(this, ok -> {
            Toast.makeText(this, "Compte créé !", Toast.LENGTH_SHORT).show();
            finish();
        });

        viewModel.errorMessage.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }

    // ── Password strength logic ───────────────────────────────
    private void updatePasswordStrength(String password) {
        if (password.isEmpty()) {
            tvPasswordStrength.setText("");
            return;
        }

        int score = 0;
        if (password.length() >= 8)                     score++;
        if (password.matches(".*[A-Z].*"))              score++;
        if (password.matches(".*[a-z].*"))              score++;
        if (password.matches(".*[0-9].*"))              score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=].*")) score++;

        StringBuilder hint = new StringBuilder();
        if (password.length() < 8)
            hint.append("• Au moins 8 caractères\n");
        if (!password.matches(".*[A-Z].*"))
            hint.append("• Une lettre majuscule\n");
        if (!password.matches(".*[a-z].*"))
            hint.append("• Une lettre minuscule\n");
        if (!password.matches(".*[0-9].*"))
            hint.append("• Un chiffre\n");
        if (!password.matches(".*[!@#$%^&*()_+\\-=].*"))
            hint.append("• Un symbole (!@#$...)\n");

        if (score <= 2) {
            tvPasswordStrength.setTextColor(0xFFE53935); // rouge
            tvPasswordStrength.setText("Faible\n" + hint);
        } else if (score == 3 || score == 4) {
            tvPasswordStrength.setTextColor(0xFFFFA000); // orange
            tvPasswordStrength.setText("Moyen\n" + hint);
        } else {
            tvPasswordStrength.setTextColor(0xFF43A047); // vert
            tvPasswordStrength.setText("Fort ✓");
        }
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8
                && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*")
                && password.matches(".*[0-9].*")
                && password.matches(".*[!@#$%^&*()_+\\-=].*");
    }

    // ── Email verification ────────────────────────────────────
    private void sendCodeAndShowDialog() {
        btnRegister.setEnabled(false);
        btnRegister.setText("Envoi du code...");

        generatedCode = String.format("%03d", new Random().nextInt(1000));
        String email  = etEmail.getText().toString().trim();
        String name   = etPrenom.getText().toString().trim();

        emailService.sendVerificationCode(email, name, generatedCode,
                new EmailService.EmailCallback() {
                    public void onSuccess() {
                        runOnUiThread(() -> {
                            btnRegister.setEnabled(true);
                            btnRegister.setText("Créer mon compte");
                            showVerificationDialog();
                        });
                    }
                    public void onError(String error) {
                        runOnUiThread(() -> {
                            btnRegister.setEnabled(true);
                            btnRegister.setText("Créer mon compte");
                            Toast.makeText(RegisterActivity.this,
                                    "Erreur : " + error, Toast.LENGTH_LONG).show();
                        });
                    }
                }
        );
    }

    private void showVerificationDialog() {
        android.app.AlertDialog.Builder builder =
                new android.app.AlertDialog.Builder(this);

        builder.setTitle("Vérification email");
        builder.setMessage("Un code à 3 chiffres a été envoyé à\n"
                + etEmail.getText().toString().trim());

        final EditText input = new EditText(this);
        input.setHint("Code à 3 chiffres");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setTextAlignment(android.view.View.TEXT_ALIGNMENT_CENTER);
        input.setTextSize(28f);

        int padding = (int) (24 * getResources().getDisplayMetrics().density);
        builder.setView(input);

        builder.setPositiveButton("Vérifier", (dialog, which) -> {
            String entered = input.getText().toString().trim();
            if (entered.equals(generatedCode)) {
                // Code correct → créer le compte
                viewModel.register(
                        etNom.getText().toString().trim(),
                        etPrenom.getText().toString().trim(),
                        etEmail.getText().toString().trim(),
                        etUsername.getText().toString().trim(),
                        etPassword.getText().toString().trim(),
                        etDate.getText().toString().trim()
                );
            } else {
                Toast.makeText(this,
                        "Code incorrect, réessayez", Toast.LENGTH_SHORT).show();
                showVerificationDialog(); // rouvre le dialog
            }
        });

        builder.setNegativeButton("Renvoyer", (dialog, which) -> {
            sendCodeAndShowDialog(); // renvoie un nouveau code
        });

        builder.setCancelable(false);
        builder.show();
    }

    // ── Validation ────────────────────────────────────────────
    private boolean validateFields() {
        if (etNom.getText().toString().trim().isEmpty() ||
                etPrenom.getText().toString().trim().isEmpty() ||
                etEmail.getText().toString().trim().isEmpty() ||
                etUsername.getText().toString().trim().isEmpty() ||
                etPassword.getText().toString().trim().isEmpty() ||
                etDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(etEmail.getText().toString().trim()).matches()) {
            Toast.makeText(this, "Email invalide", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}