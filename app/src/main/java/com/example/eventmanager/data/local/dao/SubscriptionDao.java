package com.example.eventmanager.data.local.dao;

import androidx.room.*;
import com.example.eventmanager.data.local.entities.Subscription;

@Dao
public interface SubscriptionDao {

    @Query("SELECT * FROM subscription WHERE id_user = :userId " +
            "ORDER BY date_fin DESC LIMIT 1")
    Subscription getActiveSubscription(int userId);
}