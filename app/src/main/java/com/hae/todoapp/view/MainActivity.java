package com.hae.todoapp.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hae.todoapp.R;
import com.hae.todoapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final int FRAGMENT_HOME = 1;
    private static final int FRAGMENT_EDIT_PROFILE = 2;
    private static final int FRAGMENT_DAILY_TASKS = 3;
    private static final int FRAGMENT_IMPORTANT_TASKS = 4;
    private static final int FRAGMENT_DONE_TASKS = 5;
    private int mCurrentFragment = FRAGMENT_HOME;
    private ActivityMainBinding binding;
    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
        binding.navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        TextView user_name = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_profile_name);
        TextView user_email = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_profile_email);
        ImageView user_avt = binding.navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        if (firebaseUser != null) {
            user_name.setText(firebaseUser.getDisplayName());
            user_email.setText(firebaseUser.getEmail());
            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            if (firebaseUser.getPhotoUrl() == null) {
                Glide.with(this).load(R.drawable.user).apply(requestOptions).placeholder(R.drawable.user).error(R.drawable.user).into(user_avt);
            } else {
                String photoUrl = firebaseUser.getPhotoUrl().toString();
                Glide.with(this).load(photoUrl).apply(requestOptions).placeholder(R.drawable.user).error(R.drawable.user).into(user_avt);
            }
        } else {
            user_avt.setImageResource(R.drawable.user);
        }
        binding.navigationView.getHeaderView(0).findViewById(R.id.imv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.item_user_avt);
        View view = menuItem.getActionView();
        assert view != null;
        ImageView userAvt = view.findViewById(R.id.tb_user_avt);
        if (firebaseUser != null) {
            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            if (firebaseUser.getPhotoUrl() == null) {
                Glide.with(this).load(R.drawable.user).apply(requestOptions).placeholder(R.drawable.user).error(R.drawable.user).into(userAvt);
            } else {
                String photoUrl = firebaseUser.getPhotoUrl().toString();
                Glide.with(this).load(photoUrl).apply(requestOptions).placeholder(R.drawable.user).error(R.drawable.user).into(userAvt);
            }
        } else {
            userAvt.setImageResource(R.drawable.user);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            if (mCurrentFragment != FRAGMENT_HOME) {
                replaceFragment(new HomeFragment());
                mCurrentFragment = FRAGMENT_HOME;
            }
        } else if (itemId == R.id.nav_edit_profile) {
            if (mCurrentFragment != FRAGMENT_EDIT_PROFILE) {
                replaceFragment(new EditProfileFragment());
                mCurrentFragment = FRAGMENT_EDIT_PROFILE;
            }
        } else if (itemId == R.id.nav_daily_tasks) {
            if (mCurrentFragment != FRAGMENT_DAILY_TASKS) {
                replaceFragment(new DailyTasksFragment());
                mCurrentFragment = FRAGMENT_DAILY_TASKS;
            }
        } else if (itemId == R.id.nav_important_tasks) {
            if (mCurrentFragment != FRAGMENT_IMPORTANT_TASKS) {
                replaceFragment(new ImportantTasksFragment());
                mCurrentFragment = FRAGMENT_IMPORTANT_TASKS;
            }
        } else if (itemId == R.id.nav_done_tasks) {
            if (mCurrentFragment != FRAGMENT_DONE_TASKS) {
                replaceFragment(new DoneTasksFragment());
                mCurrentFragment = FRAGMENT_DONE_TASKS;
            }
        } else if (itemId == R.id.nav_sign_out) {
            signOut();
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateUI(FirebaseUser user) {

    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.commit();
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), GoogleSignInOptions.DEFAULT_SIGN_IN);
        googleSignInClient.signOut();
        googleSignInClient.revokeAccess();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finishAffinity();
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
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}