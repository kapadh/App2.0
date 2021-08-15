package com.example.projectkapadh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.projectkapadh.Fragment.HistoryFragment;
import com.example.projectkapadh.Fragment.HomeFragment;
import com.example.projectkapadh.Fragment.HomepageFragment;
import com.example.projectkapadh.Fragment.ProfileFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class HomeActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private boolean ON_HOME_FRAGMENT = true;
    private boolean ON_HISTORY_FRAGMENT = false;
    private boolean ON_PROFILE_FRAGMENT = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        toolbar = findViewById(R.id.toolbar);
        setActionBar(toolbar);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container,new HomepageFragment());
        fragmentTransaction.commit();

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch(item.getItemId()){
                    case R.id.history:
                        if(!ON_HISTORY_FRAGMENT){
                            transaction.replace(R.id.container,new HistoryFragment());
                            ON_HISTORY_FRAGMENT = true;
                            ON_HOME_FRAGMENT = false;
                            ON_PROFILE_FRAGMENT = false;
                        }
                        break;
                    case R.id.home:
                        if(!ON_HOME_FRAGMENT){
                            transaction.replace(R.id.container,new HomepageFragment());
                            ON_HISTORY_FRAGMENT = false;
                            ON_HOME_FRAGMENT = true;
                            ON_PROFILE_FRAGMENT = false;
                        }

                        break;
                    case R.id.profile:
                        if(!ON_PROFILE_FRAGMENT){
                            transaction.replace(R.id.container,new ProfileFragment());
                            ON_HISTORY_FRAGMENT = false;
                            ON_HOME_FRAGMENT = false;
                            ON_PROFILE_FRAGMENT = true;
                        }

                        break;
                }
                transaction.commit();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }
}