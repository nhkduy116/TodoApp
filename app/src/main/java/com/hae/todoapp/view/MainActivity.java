package com.hae.todoapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
//        Xem comment ở layout activity_main.xml
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);

        replaceFragment(new HomeFragment());
        binding.navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
        TextView user_name = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_profile_name);
        TextView user_email = binding.navigationView.getHeaderView(0).findViewById(R.id.tv_profile_email);
        ImageView user_avt = binding.navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        if (firebaseUser != null) {
            String photoUrl = firebaseUser.getPhotoUrl().toString();
            user_name.setText(firebaseUser.getDisplayName());
            user_email.setText(firebaseUser.getEmail());
            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            //          img_avt_df bị lỗi không xem được ảnh dùng tạm img_avt_df1
            Glide.with(this).load(photoUrl).apply(requestOptions).placeholder(R.drawable.img_avt).error(R.drawable.img_avt_df1).into(user_avt);
        } else {
            //          img_avt_df bị lỗi không xem được ảnh dùng tạm img_avt_df1
            user_avt.setImageResource(R.drawable.img_avt_df1);
        }

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
//          Bị trùng id với dòng 85 nên đổi id thành item_user_avt
        MenuItem menuItem = menu.findItem(R.id.item_user_avt);
        View view = menuItem.getActionView();
        assert view != null;
        ImageView userAvt = view.findViewById(R.id.tb_user_avt);
        if (firebaseUser != null) {
            String photoUrl = firebaseUser.getPhotoUrl().toString();
            RequestOptions requestOptions = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(this).load(photoUrl).apply(requestOptions).placeholder(R.drawable.img_avt)
//          img_avt_df bị lỗi không xem được ảnh dùng tạm img_avt_df1
                    .error(R.drawable.img_avt_df1).into(userAvt);
        } else {
//          img_avt_df bị lỗi không xem được ảnh dùng tạm img_avt_df1
            userAvt.setImageResource(R.drawable.img_avt_df1);
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
}