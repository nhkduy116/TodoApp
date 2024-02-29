package com.hae.todoapp.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hae.todoapp.R;
import com.hae.todoapp.data.model.User;
import com.hae.todoapp.data.model.UserProfile;
import com.hae.todoapp.databinding.FragmentEditProfileBinding;
import com.hae.todoapp.utils.ProgressDialogLoadingUtils;
import com.hae.todoapp.utils.SweetAlertDialogUtils;
import com.hae.todoapp.viewmodel.UserProfileViewModel;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfileFragment extends Fragment{

    private FragmentEditProfileBinding binding;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private UserProfileViewModel userProfileViewModel;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private ArrayList<String> genderList;
    private String userGender, userPhotoUrl;
    final Handler handler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        userProfileViewModel = new ViewModelProvider(this).get(UserProfileViewModel.class);
        userProfileViewModel.fetchDataUserFromFireStore(firebaseUser.getUid());
        return binding.getRoot();
    }
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        genderList = new ArrayList<>();
        genderList.add("Male");
        genderList.add("Female");
        genderList.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genderList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.acpGender.setAdapter(adapter);
        binding.acpGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("acpGender", genderList.get(i));
                userGender = genderList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        userProfileViewModel.getUserProfileLiveData().observe(getViewLifecycleOwner(), userProfile -> {
            if (userProfile != null) {
                Log.d("getUserProfileObserve", userProfile.toString());
                RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
                if (userProfile.getPhotoUrl() == null) {
                    Glide.with(this).load(R.drawable.user).apply(requestOptions).placeholder(R.drawable.user).error(R.drawable.user).into(binding.civAvt);
                } else {
                    userPhotoUrl = userProfile.getPhotoUrl();
                    Glide.with(this).load(userPhotoUrl).apply(requestOptions).placeholder(R.drawable.user).error(R.drawable.user).into(binding.civAvt);
                }
                binding.setUserProfile(userProfile);
                ProgressDialogLoadingUtils.dismissProgressLoading();
                binding.scrollView.setVisibility(View.VISIBLE);
            } else {
                ProgressDialogLoadingUtils.dismissProgressLoading();
            }
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ProgressDialogLoadingUtils.showProgressLoading(getContext());
            }
        }, 120);

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SweetAlertDialogUtils.showSweetAlertDialogSuccessType(getContext());
            }
        });

        binding.editUsername.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (view.getId() ==R.id.edit_username) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });

        binding.tvDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mCalendar = Calendar.getInstance();
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(requireContext(), android.R.style.Theme_DeviceDefault_Dialog, dateSetListener,
                        year, month, dayOfMonth);
                dialog.show();
            }
        });


        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfile newUserProfile = new UserProfile(
                        firebaseUser.getUid(),
                        firebaseUser.getEmail(),
                        binding.editUsername.getText().toString().trim(),
                        binding.tvDateOfBirth.getText().toString(),
                        userGender,
                        userPhotoUrl
                );
                onClickUpdateUserProfile(newUserProfile);
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar mCalendar = Calendar.getInstance();
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String selectedDate = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(mCalendar.getTime());
                binding.tvDateOfBirth.setText(selectedDate);
            }
        };

        Log.d("onViewCreated", userProfileViewModel.getUserProfileLiveData().toString());
    }

    private void onClickUpdateUserProfile(UserProfile newUserProfile) {
        new SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover!")
                .setCancelText("No")
                .setConfirmText("Yes")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        userProfileViewModel.onClickChangeProfileUser(newUserProfile);
                        userProfileViewModel.getUpdated().observe(getViewLifecycleOwner(), updated -> {
                            if (updated) {
                                sweetAlertDialog.setTitleText("Updated!")
                                        .setContentText("Your profile has been updated!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            } else {
                                sweetAlertDialog.setTitleText("Updated!")
                                        .setContentText("Your profile update failed!")
                                        .setConfirmText("OK")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                        });

                    }
                })
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}