package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "subscription",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id_user", childColumns = "id_user",
                onDelete = ForeignKey.CASCADE))
public class Subscription {
    @PrimaryKey(autoGenerate = true)
    public int id_sub;
    public int id_user;
    public int id_plan;
    public String date_debut;
    public String date_fin;
    public int event_created;
    public double montant;
}