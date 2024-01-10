package com.hae.todoapp.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hae.todoapp.R;
import com.hae.todoapp.data.model.User;
import com.hae.todoapp.databinding.ActivityLoginBinding;
import com.hae.todoapp.utils.ProgressDialogLoadingUtils;
import com.hae.todoapp.utils.ToastUtils;
import com.hae.todoapp.viewmodel.LoginViewModel;
import com.hae.todoapp.viewmodel.UserViewModel;

public class LoginActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 101;
    private ActivityLoginBinding binding;
    private Context context;
    private UserViewModel mUserViewModel;
    private LoginViewModel mLoginViewModel;
    private boolean mCheckShow = true;
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        context = this;

        mLoginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding.setLoginViewModel(mLoginViewModel);
        oneTapClient = Identity.getSignInClient(LoginActivity.this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .requestId()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        mLoginViewModel.getSignInResult().observe(this, firebaseUser -> {
            ProgressDialogLoadingUtils.dismissProgressLoading();
            if (firebaseUser != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            } else {
                ToastUtils.showToastShort(context, getString(R.string.toast_wrong_email_password));
            }
        });

        mLoginViewModel.getSignInGoogleResult().observe(this, googleSignInAccount -> {
            if (googleSignInAccount != null) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            } else {
                ToastUtils.showToastShort(context, "Login with Google failed");
            }
        });

        binding.layoutLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showToastLong(context, "The Facebook login function is developing");
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
        binding.imvShowHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCheckShow) {
                    binding.imvShowHide.setImageResource(R.drawable.ic_hide);
                    binding.edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mCheckShow = !mCheckShow;
                } else {
                    binding.imvShowHide.setImageResource(R.drawable.ic_show);
                    binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    mCheckShow = !mCheckShow;
                }
            }
        });

        binding.layoutRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onClickLoginWithEmailAndPassword(View view) {
        if (mLoginViewModel.email.getValue() == null || mLoginViewModel.email.getValue().isEmpty()
            || mLoginViewModel.password.getValue() == null || mLoginViewModel.password.getValue().isEmpty()) {
            ToastUtils.showToastShort(context, getString(R.string.toast_enter_all_information));
        } else {
            ProgressDialogLoadingUtils.showProgressLoading(context);
            mLoginViewModel.signInWithEmailAndPassword();
        }
    }

    public void onClickLoginWithGoogle(View view) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 101);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                mLoginViewModel.signInWithGoogle(account);
            } catch (Exception e) {
                Log.d(TAG, e.getMessage() + "");
            }
        }
    }

}