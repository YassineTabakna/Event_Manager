package com.example.eventmanager.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.eventmanager.data.local.entities.User;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertUser(User user);

    @Query("SELECT * FROM user WHERE email = :email LIMIT 1")
    User getUserByEmail(String email);

    @Query("SELECT * FROM user WHERE username = :username LIMIT 1")
    User getUserByUsername(String username);

    @Query("SELECT COUNT(*) FROM user WHERE email = :email")
    int emailExists(String email);

    @Query("SELECT * FROM user WHERE id_user = :id LIMIT 1")
    User getUserById(int id);

    @Query("SELECT * FROM user")
    List<User> getAllUsers();

    @Update
    void updateUser(User user);
}