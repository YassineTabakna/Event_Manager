package com.example.eventmanager.data.local.entities;

import androidx.room.Embedded;

public class PendingEvent {
    @Embedded
    public Event event;

    // This must match the column name you are querying from the Achat table
    public String date_achat;
}