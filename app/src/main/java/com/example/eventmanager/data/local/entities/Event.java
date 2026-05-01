package com.example.eventmanager.data.local.entities;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import java.util.Objects;

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
    public Integer id_user;      // Changed to Integer to allow nulls if needed
    public Integer id_category;  // Changed to Integer to allow nulls per SET_NULL
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

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return id_event == event.id_event &&
                heure == event.heure &&
                minute == event.minute &&
                Double.compare(event.prix, prix) == 0 &&
                Objects.equals(titre, event.titre) &&
                Objects.equals(date, event.date) &&
                Objects.equals(description, event.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id_event, titre, date, prix);
    }
}