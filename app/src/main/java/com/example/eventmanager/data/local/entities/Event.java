package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
@Entity(tableName = "event",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "id_user",
                        childColumns = "id_user_creator",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id_category",
                        childColumns = "id_category",
                        onDelete = ForeignKey.SET_NULL)
        })
public class Event {
    @PrimaryKey(autoGenerate = true)
    public int id_event;

    public String titre;
    public String description;
    public String date;
    public String lieu;
    public int heure;
    public int minute;
    public int duree_heure;
    public int duree_jour;
    public boolean is_payant;
    public double prix;
    public int nbr_max_tickets;

    // Foreign Key columns
    public int id_user_creator;
    public int id_category;

    public Event(String titre, String description, String date, String lieu,
                 int heure, int minute, int duree_heure, int duree_jour,
                 boolean is_payant, double prix, int nbr_max_tickets,
                 int id_user_creator, int id_category) {
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.heure = heure;
        this.minute = minute;
        this.duree_heure = duree_heure;
        this.duree_jour = duree_jour;
        this.is_payant = is_payant;
        this.prix = prix;
        this.nbr_max_tickets = nbr_max_tickets;
        this.id_user_creator = id_user_creator;
        this.id_category = id_category;
    }
}