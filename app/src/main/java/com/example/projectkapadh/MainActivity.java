package com.example.projectkapadh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.projectkapadh.Fragment.LoginFragment;
import com.example.projectkapadh.Fragment.RegisterFragment;
import com.example.projectkapadh.Fragment.VerifyOtpFragment;


public class MainActivity extends AppCompatActivity {

    private FrameLayout frameLayout;
    public static boolean onRegisterFragment = false;
    public static boolean setRegisterFragment = false;
    public static boolean onLoginFragment = false;
    public static boolean onVerifyOtpFromRegisterFragment = false;
    public static boolean onVerifyOtpFromProfileFragment = false;
    public static boolean onContinueWithEmailFragment = false;
    public static boolean onForgetPasswordFragment = false;
    private String Fragment;
    private String PhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment = getIntent().getStringExtra("Fragment");
        PhoneNumber = getIntent().getStringExtra("PhoneNumber");

//        try {
//            Fragment = getIntent().getStringExtra("Fragment");
//            PhoneNumber = getIntent().getStringExtra("PhoneNumber");
//        }catch (Exception ignored){
//            Log.d("Exception", ignored.getMessage());
//        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ///////// status bar hide start///////////

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        ///////// status bar hide start///////////

        frameLayout = findViewById(R.id.mainFrameLayout);

        if(setRegisterFragment){
            setRegisterFragment = false;
            setDefaultFragment(new RegisterFragment());
        }else if(Fragment != null){

            VerifyOtpFragment fragment = new VerifyOtpFragment();
            Bundle args = new Bundle();
            args.putString("PhoneNumber", PhoneNumber);
            args.putString("FromProfile", "Yes");
            fragment.setArguments(args);
            setDefaultFragment(fragment);
        }else{
            setDefaultFragment(new RegisterFragment());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            //LoginFragment.disableCloseBtn = false;
           //RegisterFragment.disableCloseBtn = false;
            if (onContinueWithEmailFragment){
                onContinueWithEmailFragment = false;
                setFragment(new RegisterFragment());
                return false;
            }
            if (onForgetPasswordFragment){
                onForgetPasswordFragment = false;
                setFragment(new LoginFragment());
                return false;
            }
            if (onVerifyOtpFromRegisterFragment){
                onVerifyOtpFromRegisterFragment = false;
                setFragment(new RegisterFragment());
                return false;
            }
            if (onVerifyOtpFromProfileFragment){
                onVerifyOtpFromProfileFragment = false;
                finish();
                //setFragment(new RegisterFragment());
                return false;
            }
            if (onLoginFragment){
                onLoginFragment = false;
                setFragmentLTR(new RegisterFragment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setDefaultFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slideout_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setFragmentLTR(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}