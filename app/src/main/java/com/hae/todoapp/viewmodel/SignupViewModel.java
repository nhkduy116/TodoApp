package com.hae.todoapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.hae.todoapp.data.firebase.auth.FirebaseAuthenticationRepository;
import com.hae.todoapp.data.model.SignupUser;

public class SignupViewModel extends ViewModel {
    private FirebaseAuthenticationRepository mFirebaseAuthRepo = new FirebaseAuthenticationRepository();
    private static final String TAG = "SignupViewModel";
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> userEmail = new MutableLiveData<>();
    public MutableLiveData<String> userPassword = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> mRegistrationResult = new MutableLiveData<>();

    public MutableLiveData<FirebaseUser> getRegistrationResult() {
        return mRegistrationResult;
    }

    public void createAccountWithEmailAndPassword() {
        String strUserName = userName.getValue();
        String strUserEmail = userEmail.getValue();
        String strUserPassword = userPassword.getValue();

        if (strUserName == null || strUserEmail == null || !isValidEmail(strUserEmail)
            || strUserPassword == null || !isValidPassword(strUserPassword)) {
            Log.d(TAG, "createAccountWithEmailAndPassword: failed");
            mRegistrationResult.setValue(null);
        } else {
            mFirebaseAuthRepo.registerWithEmailAndPassword(strUserName, strUserEmail, strUserPassword, new FirebaseAuthenticationRepository.RegisterCallback() {
                @Override
                public void onRegisterSuccess(FirebaseUser user) {
                    Log.d(TAG, "createAccountWithEmailAndPassword: " + user);
                    mRegistrationResult.setValue(user);
                }

                @Override
                public void onRegisterFailure(Exception e) {
                    Log.d(TAG, "createAccountWithEmailAndPassword: failed");
                    mRegistrationResult.setValue(null);
                }
            });
        }
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
