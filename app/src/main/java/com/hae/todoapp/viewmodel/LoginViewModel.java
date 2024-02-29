package com.hae.todoapp.viewmodel;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.facebook.login.Login;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hae.todoapp.data.firebase.auth.FirebaseAuthenticationRepository;
import com.hae.todoapp.data.model.LoginUser;
import com.hae.todoapp.data.model.User;
import com.hae.todoapp.data.model.UserProfile;
import com.hae.todoapp.data.repository.UserRepository;
import com.hae.todoapp.utils.ProgressDialogLoadingUtils;

import java.util.Objects;
import java.util.Observable;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private final FirebaseAuthenticationRepository mAuthenticationRepository = new FirebaseAuthenticationRepository();
    private final MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public LiveData<FirebaseUser> getSignInResult() {
        return mFirebaseUserMutableLiveData;
    }

    public void signInWithEmailAndPassword() {
        String strUserEmail = email.getValue();
        String strUserPassword = password.getValue();

        if (strUserEmail == null || strUserPassword == null) {
            mFirebaseUserMutableLiveData.setValue(null);
        } else {
            mAuthenticationRepository.signInWithEmailAndPassword(strUserEmail, strUserPassword, new FirebaseAuthenticationRepository.SignInCallback() {
                @Override
                public void onSignInSuccess(FirebaseUser user) {
                    mFirebaseUserMutableLiveData.setValue(user);
                }

                @Override
                public void onSignInFailure(Exception e) {
                    mFirebaseUserMutableLiveData.setValue(null);
                }
            });
        }
    }

    public void signInWithGoogle(GoogleSignInAccount account) {
        mAuthenticationRepository.signInWithGoogle(account.getIdToken(), new FirebaseAuthenticationRepository.SignInGoogleCallback() {
            @Override
            public void onSignInGoogleSuccess(FirebaseUser user) {
                mFirebaseUserMutableLiveData.setValue(user);
            }

            @Override
            public void onSignInGoogleFailure(Exception e) {
                mFirebaseUserMutableLiveData.setValue(null);
            }
        });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#\\$%^&*+=])[^\\s]{6,}$";
        return password.matches(passwordPattern);
    }
}
