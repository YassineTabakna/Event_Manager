package com.example.eventmanager.domain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailService {

    private static final String API_KEY = "";
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    private final OkHttpClient client = new OkHttpClient();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface EmailCallback {
        void onSuccess();
        void onError(String error);
    }

    public void sendVerificationCode(String toEmail, String toName,
                                     String code, EmailCallback callback) {
        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();

                JSONObject sender = new JSONObject();
                sender.put("name", "Event Manager");
                sender.put("email", "yassintabakna7@gmail.com");
                body.put("sender", sender);

                JSONArray toList = new JSONArray();
                JSONObject recipient = new JSONObject();
                recipient.put("email", toEmail);
                recipient.put("name", toName);
                toList.put(recipient);
                body.put("to", toList);

                body.put("subject", "Votre code de vérification");
                body.put("htmlContent",
                        "<h2>Code de vérification</h2>" +
                                "<p>Votre code est : <strong style='font-size:24px'>" + code + "</strong></p>" +
                                "<p>Ce code expire dans 10 minutes.</p>"
                );

                RequestBody requestBody = RequestBody.create(
                        body.toString(),
                        MediaType.parse("application/json")
                );

                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(requestBody)
                        .addHeader("api-key", API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .build();

                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) callback.onSuccess();
                else callback.onError("Erreur envoi email : " + response.code());

            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        });
    }
}