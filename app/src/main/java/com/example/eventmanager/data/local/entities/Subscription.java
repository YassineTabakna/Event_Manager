package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(tableName = "subscription",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id_user", childColumns = "id_user"),
                @ForeignKey(entity = Plan.class, parentColumns = "id_plan", childColumns = "id_plan")
        })
public class Subscription {
    @PrimaryKey(autoGenerate = true)
    public int id_sub;

    public String date_debut;
    public String date_fin;
    public double montant;

    public int id_user;
    public int id_plan;

    public Subscription(String date_debut, String date_fin, double montant, int id_user, int id_plan) {
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.montant = montant;
        this.id_user = id_user;
        this.id_plan = id_plan;
    }
}
