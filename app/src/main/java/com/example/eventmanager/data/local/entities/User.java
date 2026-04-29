package com.example.eventmanager.data.local.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {

    @PrimaryKey(autoGenerate = true)
    public int id_user;

    public String nom;
    public String prenom;
    public String email;
    public String username;
    public String password;
    public String date_naissance;

    public User(String nom, String prenom, String email,
                String username, String password, String date_naissance) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.username = username;
        this.password = password;
        this.date_naissance = date_naissance;
    }
}