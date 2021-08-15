package com.example.projectkapadh;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import com.google.firebase.database.DatabaseReference;

public class ServiceProviderProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_profile);

        if(getIntent() != null){
            title = getIntent().getStringExtra("SplTitle");
        }


        toolbar = findViewById(R.id.SppToolbar);
        toolbar.setTitle(title);
        setActionBar(toolbar);
        View view = toolbar.getChildAt(0);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions
                finish();
            }
        });

    }
}