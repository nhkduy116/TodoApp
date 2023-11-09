package com.hae.todoapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hae.todoapp.R;
import com.hae.todoapp.data.model.User;
import com.hae.todoapp.databinding.ActivitySignupBinding;
import com.hae.todoapp.utils.ProgressDialogLoadingUtils;
import com.hae.todoapp.utils.ToastUtils;
import com.hae.todoapp.viewmodel.SignupViewModel;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private Context context;
    private ActivitySignupBinding binding;
    private SignupViewModel mSignupViewModel;
    private SignInClient oneTapClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        mSignupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);
        binding.setSignupViewModel(mSignupViewModel);
        oneTapClient = Identity.getSignInClient(SignupActivity.this);

        binding.editName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    binding.constraintLayoutName.setBackgroundResource(R.drawable.layout_edittext_border_focus);
                } else {
                    binding.constraintLayoutName.setBackgroundResource(R.drawable.layout_edittext_border_unfocus);
                }
            }
        });
        binding.editEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    binding.constraintLayoutEmail.setBackgroundResource(R.drawable.layout_edittext_border_focus);
                } else {
                    binding.constraintLayoutEmail.setBackgroundResource(R.drawable.layout_edittext_border_unfocus);
                }
            }
        });
        binding.edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    binding.constraintLayoutPassword.setBackgroundResource(R.drawable.layout_edittext_border_focus);
                } else {
                    binding.constraintLayoutPassword.setBackgroundResource(R.drawable.layout_edittext_border_unfocus);
                }
            }
        });
        binding.layoutLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });

        mSignupViewModel.getRegistrationResult().observe(this, firebaseUser -> {
            ProgressDialogLoadingUtils.dismissProgressLoading();
            if (firebaseUser != null) {
                User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail());
                Log.d(TAG, "getRegistrationResult: " + user);
                addUser(user);
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        });

        mSignupViewModel.getExceptionResult().observe(this, firebaseException -> {
            ProgressDialogLoadingUtils.dismissProgressLoading();
            if (firebaseException == null) {
                return;
            } else {
                Log.d(TAG, firebaseException.getErrorCode() + "");
                switch (firebaseException.getErrorCode()) {
                    case "ERROR_INVALID_EMAIL":
                        ToastUtils.showToastLong(context, getString(R.string.toast_email_badly_formatted));
                        binding.editEmail.setError(getString(R.string.error_email_invalid));
                        binding.editEmail.requestFocus();
                        break;
                    case "ERROR_EMAIL_ALREADY_IN_USE":
                        ToastUtils.showToastLong(context, getString(R.string.toast_email_already_another_account));
                        break;
                    case "ERROR_WEAK_PASSWORD":
                        ToastUtils.showToastLong(context, getString(R.string.toast_password_invalid));
                        binding.edtPassword.setError(getString(R.string.error_password_invalid));
                        binding.edtPassword.requestFocus();
                        break;
                }
            }
        });
    }

    public void onClickRegister(View view) {
        if (mSignupViewModel.userName.getValue() == null || mSignupViewModel.userName.getValue().isEmpty()
            || mSignupViewModel.userEmail.getValue() == null || mSignupViewModel.userEmail.getValue().isEmpty()
            || mSignupViewModel.userPassword.getValue() == null || mSignupViewModel.userPassword.getValue().isEmpty()) {
            Toast.makeText(this, "Please complete all information", Toast.LENGTH_SHORT).show();
        } else {
            ProgressDialogLoadingUtils.showProgressLoading(context);
            mSignupViewModel.createAccountWithEmailAndPassword();
        }
    }

    private void addUser(User user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("list_user");
        String pathObject = user.getUID();
        Log.d(TAG, pathObject);
        myRef.child(pathObject).setValue(user);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.constraintLayoutEmail.setBackgroundResource(R.drawable.layout_edittext_border_unfocus);
                    binding.constraintLayoutPassword.setBackgroundResource(R.drawable.layout_edittext_border_unfocus);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}