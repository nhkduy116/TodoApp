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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hae.todoapp.data.firebase.auth.FirebaseAuthenticationRepository;
import com.hae.todoapp.data.model.LoginUser;
import com.hae.todoapp.data.model.User;
import com.hae.todoapp.data.repository.UserRepository;
import com.hae.todoapp.utils.ProgressDialogLoadingUtils;

import java.util.Objects;
import java.util.Observable;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private final FirebaseAuthenticationRepository mAuthenticationRepository = new FirebaseAuthenticationRepository();
    private final MutableLiveData<FirebaseUser> mFirebaseUserMutableLiveData = new MutableLiveData<>();
    private final FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
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

//    public LiveData<FirebaseUser> signInWithGoogle() {
//        return mUserRepository.signInWithGoogle(email.getValue());
//    }

//    public void onCLickLoginWithEmailPassword(View view) {
//        Log.d("LoginViewModel", "onCLickLoginWithEmailPassword");
//        if (Objects.equals(email.getValue(), "") || Objects.equals(password.getValue(), "")) {
//            ProgressDialogLoadingUtils.dismissProgressLoading();
//            Toast.makeText(view.getContext(), "Please complete all information.",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        } else {
//            mUserRef = mFirebaseDatabase.getReference("list_user");
//            ProgressDialogLoadingUtils.dismissProgressLoading();
//            Log.d("LoginActivity", email.getValue() + " " + password.getValue());
//            signInWithEmailPassword().observe((LifecycleOwner) view.getContext(), firebaseUser -> {
//                if (firebaseUser != null) {
//                    ProgressDialogLoadingUtils.dismissProgressLoading();
//                    String pathUser = firebaseUser.getUid();
//                    User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
//                    mUserRef.child(pathUser).setValue(user, new DatabaseReference.CompletionListener() {
//                        @Override
//                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
//                            Log.d(TAG, "user: " + user.toString());
//                            signInResult.setValue(firebaseUser);
//                        }
//                    });
//                }
//            });
//        }
//    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#\\$%^&*+=])[^\\s]{6,}$";
        return password.matches(passwordPattern);
    }
}
