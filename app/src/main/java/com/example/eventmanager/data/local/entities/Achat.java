package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "acheter",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id_user", childColumns = "id_user",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Event.class,
                        parentColumns = "id_event", childColumns = "id_event",
                        onDelete = ForeignKey.CASCADE)
        })
public class Achat {
    @PrimaryKey(autoGenerate = true)
    public int id_achat;
    public int id_user;
    public int id_event;
    public int nbr_ticket;
    public String date_achat;   // "YYYY-MM-DD"
    public double montant_total;
    public boolean approved;
}