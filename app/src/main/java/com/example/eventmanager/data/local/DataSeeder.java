package com.example.eventmanager.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.eventmanager.data.local.entities.*;
import java.util.concurrent.Executors;

public class DataSeeder {

    private static final String PREF_NAME    = "seeder_prefs";
    private static final String KEY_SEEDED   = "is_seeded";

    public static void seedIfNeeded(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(KEY_SEEDED, false)) return; // already seeded

        Executors.newSingleThreadExecutor().execute(() -> {
            AppDatabase db = AppDatabase.getInstance(context);
            seed(db);
            prefs.edit().putBoolean(KEY_SEEDED, true).apply();
        });
    }

    private static void seed(AppDatabase db) {

        // ── USERS ────────────────────────────────────────────────────────────
        // Password for both = "Test@1234" (pre-hashed with BCrypt)
        int idUser1 = 1;  // Alice — the event creator
        int idUser2 = 2;  // Marc  — the attendee

        // ── CATEGORIES ───────────────────────────────────────────────────────
        Category cat1 = new Category(); cat1.nom = "Music";
        cat1.description = "Concerts, festivals and live performances";

        Category cat2 = new Category(); cat2.nom = "Tech";
        cat2.description = "Conferences, hackathons and workshops";

        Category cat3 = new Category(); cat3.nom = "Sport";
        cat3.description = "Tournaments, races and fitness events";

        Category cat4 = new Category(); cat4.nom = "Art";
        cat4.description = "Exhibitions, galleries and creative shows";

        long idCat1 = insertCategory(db, cat1);
        long idCat2 = insertCategory(db, cat2);
        long idCat3 = insertCategory(db, cat3);
        long idCat4 = insertCategory(db, cat4);

        // ── EVENTS created by User1 (Alice) ──────────────────────────────────
        long idE1 = insertEvent(db, (int)idUser1, (int)idCat1,
                "Jazz Night", "A smooth jazz evening downtown",
                "2026-07-15", 20, 0, 3, 0, false, 0, 200);

        long idE2 = insertEvent(db, (int)idUser1, (int)idCat2,
                "Android Summit", "Best practices in mobile dev",
                "2025-08-10", 9, 0, 8, 0, true, 49.99, 300);

        long idE3 = insertEvent(db, (int)idUser1, (int)idCat3,
                "City Marathon", "Annual 10km city run",
                "2026-09-05", 7, 30, 4, 0, false, 0, 500);

        // ── EVENTS from other organizers (for Marc to attend) ─────────────────
        // Past events (Marc attended)
        long idE4 = insertEvent(db, (int)idUser1, (int)idCat4,
                "Street Art Expo", "Urban art exhibition",
                "2026-03-20", 14, 0, 5, 0, false, 0, 100);

        long idE5 = insertEvent(db, (int)idUser1, (int)idCat1,
                "Rock Festival", "3-day rock music festival",
                "2026-04-01", 18, 0, 0, 3, true, 79.99, 1000);

        // Upcoming events (Marc registered and approved)
        long idE6 = insertEvent(db, (int)idUser1, (int)idCat2,
                "AI Workshop", "Hands-on ML workshop",
                "2026-12-01", 10, 0, 6, 0, true, 29.99, 50);

        long idE7 = insertEvent(db, (int)idUser1, (int)idCat3,
                "Yoga Retreat", "Weekend wellness retreat",
                "2026-11-15", 8, 0, 0, 2, false, 0, 30);

        // Pending events (Marc applied, not approved yet)
        long idE8 = insertEvent(db, (int)idUser1, (int)idCat1,
                "Piano Gala", "Classical piano evening",
                "2026-10-20", 19, 30, 2, 0, true, 35.00, 80);

        long idE9 = insertEvent(db, (int)idUser1, (int)idCat4,
                "Photography Expo", "Best shots of the year",
                "2026-10-28", 11, 0, 4, 0, false, 0, 60);

        // ── ACHATS for User2 (Marc) ───────────────────────────────────────────
        // Past attended (approved = true, past date)
        insertAchat(db, (int)idUser2, (int)idE4, 1, "2026-03-01",  0,     true);
        insertAchat(db, (int)idUser2, (int)idE5, 2, "2026-03-15",  159.98, true);

        // Upcoming (approved = true, future date)
        insertAchat(db, (int)idUser2, (int)idE6, 1, "2026-09-10",  29.99, true);
        insertAchat(db, (int)idUser2, (int)idE7, 1, "2026-10-01",  0,     true);

        // Pending (approved = false)
        insertAchat(db, (int)idUser2, (int)idE8, 1, "2026-09-20",  35.00, false);
        insertAchat(db, (int)idUser2, (int)idE9, 2, "2026-09-22",  0,     false);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private static long insertCategory(AppDatabase db, Category cat) {
        db.categoryDao().insertCategory(cat);
        // Room doesn't return id for void insert — use a query
        return db.categoryDao().getLastInsertedId();
    }

    private static long insertEvent(AppDatabase db, int userId, int catId,
                                    String titre, String desc, String date,
                                    int heure, int minute, int dureeHeure,
                                    int dureeJour, boolean isPayant,
                                    double prix, int maxTickets) {
        Event e = new Event();
        e.id_user        = userId;
        e.id_category    = catId;
        e.titre          = titre;
        e.description    = desc;
        e.date           = date;
        e.heure          = heure;
        e.minute         = minute;
        e.duree_heure    = dureeHeure;
        e.duree_jour     = dureeJour;
        e.is_payant      = isPayant;
        e.prix           = prix;
        e.nbr_max_tickets = maxTickets;
        e.lieu           = "Casablanca";
        return db.eventDao().insertEvent(e);
    }

    private static void insertAchat(AppDatabase db, int userId, int eventId,
                                    int nbrTicket, String dateAchat,
                                    double montant, boolean approved) {
        Achat a = new Achat();
        a.id_user       = userId;
        a.id_event      = eventId;
        a.nbr_ticket    = nbrTicket;
        a.date_achat    = dateAchat;
        a.montant_total = montant;
        a.approved      = approved;
        db.achatDao().insertAchat(a);
    }
}