package com.example.eventmanager.data.repositories;

import com.example.eventmanager.data.local.dao.UserDao;
import com.example.eventmanager.data.local.entities.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserRepository {

    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public UserRepository(UserDao userDao) {
        this.userDao = userDao;
    }

    public interface Callback<T> {
        void onResult(T result, String error);
    }

    public void register(String nom, String prenom, String email,
                         String username, String password, String dateNaissance,
                         Callback<Boolean> callback) {
        executor.execute(() -> {
            if (userDao.emailExists(email) > 0) {
                callback.onResult(false, "Email déjà utilisé");
                return;
            }
            String hashed = BCrypt.withDefaults()
                    .hashToString(12, password.toCharArray());
            User user = new User(nom, prenom, email, username, hashed, dateNaissance);
            userDao.insertUser(user);
            callback.onResult(true, null);
        });
    }

    public void login(String email, String password, Callback<User> callback) {
        executor.execute(() -> {
            // ADD THESE LOGS
            List<User> allUsers = userDao.getAllUsers();
            android.util.Log.d("LOGIN_DEBUG", "Total users in DB: " + allUsers.size());
            for (User u : allUsers) {
                android.util.Log.d("LOGIN_DEBUG", "User found: " + u.email);
            }
            android.util.Log.d("LOGIN_DEBUG", "Trying to login with: " + email);

            User user = userDao.getUserByEmail(email);
            android.util.Log.d("LOGIN_DEBUG", "getUserByEmail result: " + (user == null ? "NULL" : user.email));

            if (user == null) {
                callback.onResult(null, "Email introuvable");
                return;
            }
            //User user = userDao.getUserByEmail(email);
            /*if (user == null) {
                callback.onResult(null, "Email introuvable");
                return;
            }*/
            boolean match = BCrypt.verifyer()
                    .verify(password.toCharArray(), user.password)
                    .verified;
            if (match) callback.onResult(user, null);
            else callback.onResult(null, "Mot de passe incorrect");
        });
    }
}