package com.hae.todoapp.data.firebase.auth;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hae.todoapp.data.model.User;

import java.util.Objects;

public class FirebaseAuthenticationRepository {
    private static final String TAG = "FirebaseAuthenticationRepository";
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

    public void signInWithGoogle(String googleIdToken, SignInGoogleCallback callback) {
        mFirebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(googleIdToken, null))
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithGoogle: task successful");
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
                        addUser(user);
                        callback.onSignInGoogleSuccess(firebaseUser);
                    } else {
                        Log.d(TAG, "signInWithGoogle: task failure");
                        Exception exception = task.getException();
                        callback.onSignInGoogleFailure(exception);
                    }
                });
    }

    private void addUser(User user) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUID()).set(user);
        DatabaseReference myRef = firebaseDatabase.getReference("list_user");
        String pathObject = user.getUID();
        Log.d(TAG, pathObject);
        myRef.child(pathObject).setValue(user);
    }

    public interface SignInCallback {
        void onSignInSuccess(FirebaseUser user);
        void onSignInFailure(Exception e);
    }

    public interface RegisterCallback {
        void onRegisterSuccess(FirebaseUser user);
        void onRegisterFailure(Exception e);
    }

    public interface SignInGoogleCallback {
        void onSignInGoogleSuccess(FirebaseUser user);
        void onSignInGoogleFailure(Exception e);
    }
}
