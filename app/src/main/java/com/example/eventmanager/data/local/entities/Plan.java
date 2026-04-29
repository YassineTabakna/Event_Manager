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

    public Plan(String nom, double prix, int max_event, boolean event_payant, int duree_jour) {
        this.nom = nom;
        this.prix = prix;
        this.max_event = max_event;
        this.event_payant = event_payant;
        this.duree_jour = duree_jour;
    }
}