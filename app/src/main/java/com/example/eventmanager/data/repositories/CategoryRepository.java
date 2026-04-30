package com.example.eventmanager.domain;

import androidx.lifecycle.LiveData;
import com.example.eventmanager.data.local.dao.CategoryDao;
import com.example.eventmanager.data.local.entities.Category;
import java.util.List;

public class CategoryRepository {
    private final CategoryDao dao;
    public CategoryRepository(CategoryDao dao) { this.dao = dao; }
    public LiveData<List<Category>> getAllCategories() { return dao.getAllCategories(); }
}