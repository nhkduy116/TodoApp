package com.hae.todoapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hae.todoapp.data.repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends ViewModel {
    private UserRepository mUserRepository;
    public UserViewModel() {
        mUserRepository = new UserRepository();
    }
    public LiveData<FirebaseUser> signIn(String email, String password) {
        return mUserRepository.signIn(email, password);
    }
    public LiveData<FirebaseUser> signInWithGoogle(String googleIdToken) {
        return mUserRepository.signInWithGoogle(googleIdToken);
    }
}
