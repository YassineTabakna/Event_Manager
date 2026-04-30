package com.example.eventmanager.data.local.dao;

import androidx.room.*;
import com.example.eventmanager.data.local.entities.Plan;

@Dao
public interface PlanDao {
    @Query("SELECT * FROM plan WHERE id_plan = :planId LIMIT 1")
    Plan getPlanById(int planId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPlan(Plan plan);
}