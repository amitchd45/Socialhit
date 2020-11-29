package com.socialhit.app.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.socialhit.app.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeMainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private BottomNavigationView navView;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        navView = findViewById(R.id.nav_view);
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeFragment, R.id.profileFragment, R.id.usersFragment,R.id.chatFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment1);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment1);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logoutDialog();
                break;

            case R.id.search:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Do you want logout?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                checkUserStatus();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    private void checkUserStatus() {
        FirebaseUser user =  FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){

        }else {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }
}