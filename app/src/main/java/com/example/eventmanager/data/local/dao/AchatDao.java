package com.example.eventmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.eventmanager.data.local.entities.Event;
import java.util.List;

@Dao
public interface AchatDao {

    // Pending (not approved) — joined with event for display
    @Query("SELECT event.* FROM event " +
            "INNER JOIN acheter ON event.id_event = acheter.id_event " +
            "WHERE acheter.id_user = :userId AND acheter.approved = 0 " +
            "ORDER BY acheter.date_achat ASC")
    LiveData<List<Event>> getPendingEvents(int userId);

    // Upcoming (approved, future date) — sorted by date
    @Query("SELECT event.* FROM event " +
            "INNER JOIN acheter ON event.id_event = acheter.id_event " +
            "WHERE acheter.id_user = :userId AND acheter.approved = 1 " +
            "AND event.date >= :today " +
            "ORDER BY event.date ASC")
    LiveData<List<Event>> getUpcomingEvents(int userId, String today);

    // Past (approved, past date)
    @Query("SELECT event.* FROM event " +
            "INNER JOIN acheter ON event.id_event = acheter.id_event " +
            "WHERE acheter.id_user = :userId AND acheter.approved = 1 " +
            "AND event.date < :today " +
            "ORDER BY event.date DESC")
    LiveData<List<Event>> getPastEvents(int userId, String today);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchat(com.example.eventmanager.data.local.entities.Achat achat);
}