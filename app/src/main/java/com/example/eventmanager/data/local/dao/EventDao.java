package com.example.eventmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.eventmanager.data.local.entities.Event;
import java.util.List;

@Dao
public interface EventDao {

    // Events created by user
    @Query("SELECT * FROM event WHERE id_user = :userId ORDER BY date ASC")
    LiveData<List<Event>> getEventsByUser(int userId);

    // Events by category
    @Query("SELECT * FROM event WHERE id_category = :categoryId ORDER BY date ASC")
    LiveData<List<Event>> getEventsByCategory(int categoryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEvent(Event event);

    // Delete the entire event
    @Query("DELETE FROM event WHERE id_event = :eventId")
    void deleteEvent(int eventId);

    // Fetch a single event by its ID
    @Query("SELECT * FROM event WHERE id_event = :eventId LIMIT 1")
    LiveData<Event> getEventById(int eventId);
}