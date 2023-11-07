package com.hae.todoapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.hae.todoapp.data.firebase.auth.FirebaseAuthenticationRepository;
import com.hae.todoapp.data.model.SignupUser;

public class SignupViewModel extends ViewModel {
    private final FirebaseAuthenticationRepository mFirebaseAuthRepo = new FirebaseAuthenticationRepository();
    private static final String TAG = "SignupViewModel";
    public MutableLiveData<String> userName = new MutableLiveData<>();
    public MutableLiveData<String> userEmail = new MutableLiveData<>();
    public MutableLiveData<String> userPassword = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> mRegistrationResult = new MutableLiveData<>();
    private final MutableLiveData<FirebaseAuthException> mExceptionResult = new MutableLiveData<>();

    public MutableLiveData<FirebaseUser> getRegistrationResult() {
        return mRegistrationResult;
    }
    public MutableLiveData<FirebaseAuthException> getExceptionResult() {
        return mExceptionResult;
    }

    public void createAccountWithEmailAndPassword() {
        String strUserName = userName.getValue();
        String strUserEmail = userEmail.getValue();
        String strUserPassword = userPassword.getValue();

        Log.d(TAG, "createAccountWithEmailAndPassword: " + strUserName + "\n" + strUserEmail + "\n" + strUserPassword);

        if (strUserName == null || strUserEmail == null
            || strUserPassword == null) {
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
                    Log.d(TAG, "Exception: " + e.getMessage());
                    mExceptionResult.setValue((FirebaseAuthException) e);
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
