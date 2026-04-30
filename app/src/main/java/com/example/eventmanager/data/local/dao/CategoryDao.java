package com.example.eventmanager.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.eventmanager.data.local.entities.Category;
import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM category ORDER BY nom ASC")
    LiveData<List<Category>> getAllCategories();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCategory(Category category);  // change void → long

    @Query("SELECT id_category FROM category ORDER BY id_category DESC LIMIT 1")
    int getLastInsertedId();
}