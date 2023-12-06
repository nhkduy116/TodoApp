package com.hae.todoapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.hae.todoapp.R;
import com.hae.todoapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout,
                binding.toolbar, R.string.app_name, R.string.app_name);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navigationView.setNavigationItemSelectedListener(this);
//        binding.navigationView.setCheckedItem(R.id.nav_home);

        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_edit_profile) {
            startNewActivity(EditProfileActivity.class);
        } else if (itemId == R.id.nav_daily_tasks) {
            startNewActivity(DailyTasksActivity.class);
        } else if (itemId == R.id.nav_important_tasks) {
            startNewActivity(ImportantTasksActivity.class);
        } else if (itemId == R.id.nav_done_tasks) {
            startNewActivity(DoneTasksActivity.class);
        } else if (itemId == R.id.nav_sign_out) {
            signOut();
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startNewActivity(Class<?> cls) {
        startActivity(new Intent(MainActivity.this, cls));
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