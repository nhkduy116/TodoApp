package com.hae.todoapp.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class UserRepository {
    private FirebaseAuth mFirebaseAuth;
    public UserRepository() {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }
    public LiveData<FirebaseUser> signIn(String email, String password) {
        MutableLiveData<FirebaseUser> resultLiveData = new MutableLiveData<>();
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                Log.d("UserRepository", user+"");
                resultLiveData.setValue(user);
            } else {
                resultLiveData.setValue(null);
            }
        });
        return resultLiveData;
    }

    public LiveData<FirebaseUser> signInWithGoogle(String googleIdToken) {
        MutableLiveData<FirebaseUser> resultLiveData = new MutableLiveData<>();
        mFirebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(googleIdToken, null))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mFirebaseAuth.getCurrentUser();
                        Log.d("UserRepository", user+"");
                        resultLiveData.setValue(user);
                    } else {
                        resultLiveData.setValue(null);
                    }
                });
        return resultLiveData;
    }
}
