package com.example.eventmanager.domain;

import androidx.lifecycle.LiveData;
import com.example.eventmanager.data.local.dao.AchatDao;
import com.example.eventmanager.data.local.dao.EventDao;
import com.example.eventmanager.data.local.entities.Event;
import java.text.SimpleDateFormat;
import java.util.*;

public class EventRepository {
    private final EventDao eventDao;
    private final AchatDao achatDao;

    public EventRepository(EventDao eventDao, AchatDao achatDao) {
        this.eventDao = eventDao;
        this.achatDao = achatDao;
    }

    public LiveData<List<Event>> getEventsByUser(int userId) {
        return eventDao.getEventsByUser(userId);
    }

    public LiveData<List<Event>> getPendingEvents(int userId) {
        return achatDao.getPendingEvents(userId);
    }

    public LiveData<List<Event>> getUpcomingEvents(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        return achatDao.getUpcomingEvents(userId, today);
    }

    public LiveData<List<Event>> getPastEvents(int userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
        return achatDao.getPastEvents(userId, today);
    }
}