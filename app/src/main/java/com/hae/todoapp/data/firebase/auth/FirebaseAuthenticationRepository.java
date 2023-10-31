package com.hae.todoapp.data.firebase.auth;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class FirebaseAuthenticationRepository {
    private static final String TAG = "FirebaseAuthManager";
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    public void signInWithEmailAndPassword(String email, String password, SignInCallback callback) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       Log.d(TAG, "signInWithEmailAndPassword: task successful");
                       FirebaseUser user = task.getResult().getUser();
                       callback.onSignInSuccess(user);
                   } else {
                       Log.d(TAG, "signInWithEmailAndPassword: task failed");
                       Exception exception = task.getException();
                       callback.onSignInFailure(exception);
                   }
                });
    }

    public void registerWithEmailAndPassword(String userName, String email, String password, RegisterCallback callback) {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               Log.d(TAG, "registerWithEmailAndPassword: task successful");
               FirebaseUser user = task.getResult().getUser();
               UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                       .setDisplayName(userName)
                       .build();
               assert user != null;
               user.updateProfile(profileChangeRequest)
                       .addOnCompleteListener(updateTask -> {
                           if (updateTask.isSuccessful()) {
                               Log.d(TAG, "registerWithEmailAndPassword: updateTask successful");
                               callback.onRegisterSuccess(user);
                           } else {
                               callback.onRegisterFailure(updateTask.getException());
                               Log.d(TAG, "registerWithEmailAndPassword: updateTask failed");
                           }
                       });
           } else {
               Exception exception = task.getException();
               callback.onRegisterFailure(exception);
               Log.d(TAG, "registerWithEmailAndPassword: task failed");
           }
        });
    }

    public interface SignInCallback {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(Exception e);
    }

    public interface RegisterCallback {
        void onRegisterSuccess(FirebaseUser user);
        void onRegisterFailure(Exception e);
    }

}
