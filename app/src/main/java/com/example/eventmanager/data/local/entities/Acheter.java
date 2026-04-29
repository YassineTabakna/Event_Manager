package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(tableName = "acheter",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id_user", childColumns = "id_user"),
                @ForeignKey(entity = Event.class, parentColumns = "id_event", childColumns = "id_event")
        })
public class Acheter {
    @PrimaryKey(autoGenerate = true)
    public int id_achat;

    public int nbr_ticket;
    public String date_achat;
    public double montant_total;

    public int id_user;
    public int id_event;

    public Acheter(int nbr_ticket, String date_achat, double montant_total, int id_user, int id_event) {
        this.nbr_ticket = nbr_ticket;
        this.date_achat = date_achat;
        this.montant_total = montant_total;
        this.id_user = id_user;
        this.id_event = id_event;
    }
}
