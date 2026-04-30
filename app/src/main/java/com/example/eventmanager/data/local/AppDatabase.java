package com.example.eventmanager.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.eventmanager.data.local.dao.AchatDao;
import com.example.eventmanager.data.local.dao.CategoryDao;
import com.example.eventmanager.data.local.dao.EventDao;
import com.example.eventmanager.data.local.dao.PlanDao;
import com.example.eventmanager.data.local.dao.SubscriptionDao;
import com.example.eventmanager.data.local.dao.UserDao;
import com.example.eventmanager.data.local.entities.Achat;
import com.example.eventmanager.data.local.entities.Category;
import com.example.eventmanager.data.local.entities.Event;
import com.example.eventmanager.data.local.entities.Plan;
import com.example.eventmanager.data.local.entities.Subscription;
import com.example.eventmanager.data.local.entities.User;

@Database(entities = {User.class, Category.class, Event.class,
        Achat.class, Subscription.class, Plan.class},
        version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao         userDao();
    public abstract CategoryDao     categoryDao();
    public abstract EventDao        eventDao();
    public abstract AchatDao        achatDao();
    public abstract SubscriptionDao subscriptionDao();
    public abstract PlanDao planDao();


    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class,
                            "event_manager_db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}