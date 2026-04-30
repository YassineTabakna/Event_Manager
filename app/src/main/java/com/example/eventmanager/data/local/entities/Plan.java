package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plan")
public class Plan {
    @PrimaryKey(autoGenerate = true)
    public int id_plan;
    public String nom;
    public double prix;
    public int max_event;
    public boolean event_payant;
    public int duree_jour;
}