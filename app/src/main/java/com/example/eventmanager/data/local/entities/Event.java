package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "event",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id_user", childColumns = "id_user",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id_category", childColumns = "id_category",
                        onDelete = ForeignKey.SET_NULL)
        })
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int id_event;
    public int id_user;
    public int id_category;
    public String titre;
    public String description;
    public String date;       // "YYYY-MM-DD"
    public String lieu;
    public int heure;
    public int minute;
    public int duree_heure;
    public int duree_jour;
    public boolean is_payant;
    public double prix;
    public int nbr_max_tickets;
}