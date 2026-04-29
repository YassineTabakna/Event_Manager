package com.example.eventmanager.ui.login;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.eventmanager.data.local.AppDatabase;
import com.example.eventmanager.data.local.entities.User;
import com.example.eventmanager.data.repositories.UserRepository;

public class LoginViewModel extends AndroidViewModel {

    private final UserRepository repo;
    public final MutableLiveData<User>   loginResult   = new MutableLiveData<>();
    public final MutableLiveData<String> errorMessage  = new MutableLiveData<>();
    public final MutableLiveData<Boolean> registerResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application app) {
        super(app);
        repo = new UserRepository(AppDatabase.getInstance(app).userDao());
    }

    public void login(String email, String password) {
        repo.login(email, password, (user, error) -> {
            if (error != null) errorMessage.postValue(error);
            else loginResult.postValue(user);
        });
    }

    public void register(String nom, String prenom, String email,
                         String username, String password, String date) {
        repo.register(nom, prenom, email, username, password, date, (ok, error) -> {
            if (error != null) errorMessage.postValue(error);
            else registerResult.postValue(true);
        });
    }
}