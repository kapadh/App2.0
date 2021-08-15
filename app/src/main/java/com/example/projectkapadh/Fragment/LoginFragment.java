package com.example.projectkapadh.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectkapadh.HomeActivity;
import com.example.projectkapadh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.projectkapadh.Fragment.RegisterFragment.progressDialog;
import static com.example.projectkapadh.Fragment.RegisterFragment.showProgressDialog;
import static com.example.projectkapadh.MainActivity.onForgetPasswordFragment;
import static com.example.projectkapadh.MainActivity.onLoginFragment;
import static com.example.projectkapadh.MainActivity.onRegisterFragment;
import static com.example.projectkapadh.MainActivity.setRegisterFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Button logInWithMobileOrGoogle, loginBtn, showHidePassword;
    private EditText username, password;
    private TextView forgetPassword;
    private FrameLayout parentFrameLayout;
    private boolean btn = true;
    private FirebaseAuth mAuth;
    //public static boolean disableCloseBtn = false;


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    boolean isPhoneValid(CharSequence phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        parentFrameLayout = getActivity().findViewById(R.id.mainFrameLayout);
        forgetPassword = view.findViewById(R.id.forgetPassword);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        logInWithMobileOrGoogle = view.findViewById(R.id.logInWithMobileOrGoogle);
        loginBtn = view.findViewById(R.id.loginBtn);
        showHidePassword = view.findViewById(R.id.show_hide_password);
        mAuth = FirebaseAuth.getInstance();

        ///////////// Show Password /////////////////
        showHidePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    showHidePassword.setBackgroundResource(R.drawable.ic_baseline_visibility_off_24);
                    password.setSelection(password.getText().length());
                    btn = false;
                }else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    showHidePassword.setBackgroundResource(R.drawable.ic_baseline_visibility_24);
                    password.setSelection(password.getText().length());
                    btn = true;
                }

            }
        });
        ///////////// Show Password /////////////////

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onForgetPasswordFragment = true;
                setFragment(new ForgetPasswordFragment());
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_loginID = username.getText().toString();
                String txt_loginPass = password.getText().toString();
                if (!TextUtils.isEmpty(txt_loginID)){
                    if (isEmailValid(txt_loginID)){
                        if(!TextUtils.isEmpty(txt_loginPass)){

                            showProgressDialog();
                            logInUser(txt_loginID,txt_loginPass);

                        }else{
                            Toast.makeText(getActivity(), "Enter Password", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getActivity(), "Enter Valid Email / Mobile No. or Password", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getActivity(), "Enter your registered email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logInWithMobileOrGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegisterFragment = true;
                onLoginFragment = false;
                setFragment(new RegisterFragment());
            }
        });
    }

    private void logInUser(String txt_loginId, String txt_loginPass) {
        mAuth.signInWithEmailAndPassword(txt_loginId, txt_loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(getActivity(), HomeActivity.class));
                    getActivity().finish();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Error : " + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right,R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

}