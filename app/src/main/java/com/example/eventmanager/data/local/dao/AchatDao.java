package com.example.eventmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.eventmanager.data.local.entities.Achat;
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.data.local.entities.PendingEvent;

import java.util.List;

@Dao
public interface AchatDao {

    // Pending (not approved) — joined with event for display
    @Query("SELECT event.*, acheter.date_achat FROM event " +
            "INNER JOIN acheter ON event.id_event = acheter.id_event " +
            "WHERE acheter.id_user = :userId AND acheter.approved = 0 " +
            "ORDER BY acheter.date_achat ASC")
    LiveData<List<PendingEvent>> getPendingEvents(int userId);

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


    // Check if a user already bought/requested a ticket for this event
    @Query("SELECT * FROM acheter WHERE id_user = :userId AND id_event = :eventId LIMIT 1")
    LiveData<Achat> getAchatByUserAndEvent(int userId, int eventId);

    // Delete a ticket
    @Query("DELETE FROM acheter WHERE id_user = :userId AND id_event = :eventId")
    void deleteAchat(int userId, int eventId);


    // Gets a list of users who requested to join a specific event
    @Query("SELECT acheter.id_achat, user.id_user, user.nom, user.prenom, acheter.approved, acheter.date_achat " +
            "FROM acheter INNER JOIN user ON acheter.id_user = user.id_user " +
            "WHERE acheter.id_event = :eventId ORDER BY acheter.date_achat DESC")
    LiveData<List<com.example.eventmanager.data.local.entities.AttendeeInfo>> getAttendeesForEvent(int eventId);

    // Approve a request
    @Query("UPDATE acheter SET approved = 1 WHERE id_achat = :achatId")
    void approveRequest(int achatId);

    // Reject/Delete a request
    @Query("DELETE FROM acheter WHERE id_achat = :achatId")
    void rejectRequest(int achatId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAchat(com.example.eventmanager.data.local.entities.Achat achat);
}