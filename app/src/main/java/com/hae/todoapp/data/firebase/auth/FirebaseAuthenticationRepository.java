package com.hae.todoapp.data.firebase.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthenticationRepository {
    private static final String TAG = "FirebaseAuthManager";
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    public void signInWithEmailAndPassword(String email, String password, SignInCallback callback) {
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       FirebaseUser user = task.getResult().getUser();
                       callback.onSignInSuccess(user);
                   } else {
                       Exception exception = task.getException();
                       callback.onSignInFailure(exception);
                   }
                });
    }

    public interface SignInCallback {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(Exception e);
    }

}
