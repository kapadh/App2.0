package com.example.projectkapadh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toolbar;

import com.example.projectkapadh.Fragment.AboutUsFragment;
import com.example.projectkapadh.Fragment.ChangePasswordFragment;
import com.example.projectkapadh.Fragment.HelpFragment;

public class NextToProfileActivity extends AppCompatActivity {


    FrameLayout frameLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_to_prifile);

        toolbar = findViewById(R.id.nextToProfileToolbar);
        setActionBar(toolbar);
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions
                finish();
            }
        });

        frameLayout = findViewById(R.id.nextToProfileContainer);

        String setFragmentType = getIntent().getStringExtra("setFragment");

        if(setFragmentType.equals("PasswordAndSecurity")){
            setFragment(new ChangePasswordFragment());
            toolbar.setTitle("Password And Security");

        }else if (setFragmentType.equals("Help")){
            setFragment(new HelpFragment());
            toolbar.setTitle("Help");

        }else if (setFragmentType.equals("AboutUs")){
            setFragment(new AboutUsFragment());
            toolbar.setTitle("About KAPADH");

        }else{
            finish();
        }

    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}