package com.hae.todoapp.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.hae.todoapp.data.model.User;
import com.hae.todoapp.data.model.UserProfile;
import com.hae.todoapp.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserProfileViewModel extends ViewModel {
    private final MutableLiveData<UserProfile> userProfileMutableLiveData = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration userProfileListener;
    private boolean hasUser = false;
    private MutableLiveData<Boolean> updated = new MutableLiveData<>(false);

    public LiveData<UserProfile> getUserProfileLiveData() {
        return userProfileMutableLiveData;
    }

    public boolean getHasUser() {
        return hasUser;
    }
    public LiveData<Boolean> getUpdated() {
        return updated;
    }
    public void fetchDataUserFromFireStore(String uid) {
        DocumentReference docRef = db.collection("users").document(uid);
        Log.e("fetchDataUserFromFireStore", docRef+"");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    UserProfile userProfile = task.getResult().toObject(UserProfile.class);
                    if (userProfile.getDateOfBirth().equals("")) {
                        String s = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH).format(new Date());
                        userProfile.setDateOfBirth(s);
                    }
                    userProfile.setGender(userProfile.getGender().equals("") ? "Male" : userProfile.getGender());
                    userProfileMutableLiveData.setValue(userProfile);
                    hasUser = true;
                } else {
                    hasUser = false;
                    Log.e("fetchDataUserFromFireStore", "Fetch data failed");
                    userProfileMutableLiveData.setValue(new UserProfile("", "", "", "", "", ""));
                }
            }
        });
    }

    public void onClickChangeProfileUser(UserProfile newUserProfile) {
        DocumentReference docRef = db.collection("users").document(newUserProfile.getUID());
        docRef.set(newUserProfile)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updated.setValue(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updated.setValue(false);
                    }
                });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (userProfileListener != null) {
            userProfileListener.remove();
        }
    }

}
