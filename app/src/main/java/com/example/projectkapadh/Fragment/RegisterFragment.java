package com.example.projectkapadh.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.projectkapadh.MainActivity;
import com.example.projectkapadh.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static com.example.projectkapadh.DbQueries.AuthenticationQueries.RegisterWithEmailAndPassword;
import static com.example.projectkapadh.DbQueries.AuthenticationQueries.RegisterWithGmail;
import static com.example.projectkapadh.MainActivity.onContinueWithEmailFragment;
import static com.example.projectkapadh.MainActivity.onLoginFragment;
import static com.example.projectkapadh.MainActivity.onRegisterFragment;
import static com.example.projectkapadh.MainActivity.onVerifyOtpFromRegisterFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;
    private static final String TAG = "GoogleActivity";

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    boolean isPhoneValid(CharSequence phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    private FrameLayout parentFrameLayout;
    public static EditText mobileNumber;
    private Button getOtpBtn, continueWithEmailBtn, registerUserBtn, alreadyHaveAcc, continueWithGoogleBtn;
    private ExpandableLayout expandableLayout1;
    private EditText regFullName, regEmail, regPassword, regConfirmPassword;
    private boolean CWEB = true;
    private ConstraintLayout registerLayout;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private DatabaseReference databaseReference;

    public static ProgressDialog progressDialog;
    //public static String verificationId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        alreadyHaveAcc = view.findViewById(R.id.alreadyHaveAcc);
        parentFrameLayout = getActivity().findViewById(R.id.mainFrameLayout);
        mobileNumber = view.findViewById(R.id.mobileNumber);
        getOtpBtn = view.findViewById(R.id.getOtpBtn);
        continueWithEmailBtn = view.findViewById(R.id.continueWithEmailBtn);
        expandableLayout1 = view.findViewById(R.id.expandable_layout_1);
        registerUserBtn = view.findViewById(R.id.registerUserBtn);
        registerLayout = view.findViewById(R.id.registerLayout);
        regFullName = view.findViewById(R.id.regFullName);
        regEmail = view.findViewById(R.id.regEmail);
        regPassword = view.findViewById(R.id.regPassword);
        regConfirmPassword = view.findViewById(R.id.regConfirmPassword);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        continueWithGoogleBtn = view.findViewById(R.id.continueWithGoogleBtn);
        progressDialog = new ProgressDialog(getContext());

        //registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80091262")));

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        // Configure Google Sign In

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alreadyHaveAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginFragment = true;
                setFragment(new LoginFragment());
            }
        });

        getOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNumber.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Enter Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mobileNumber.length() != 10) {
                    Toast.makeText(getActivity(), "Enter 10 Digit Mobile Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                //showProgressDialog();
                onVerifyOtpFromRegisterFragment = true;
                VerifyOtpFragment fragment = new VerifyOtpFragment();
                Bundle args = new Bundle();
                args.putString("PhoneNumber", mobileNumber.getText().toString());
                fragment.setArguments(args);
                setFragmentf(fragment);
            }
        });

        continueWithEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onContinueWithEmailFragment = true;
                //setFragmentf(new ContinueWithEmailFragment());
                expand(expandableLayout1);
                if (CWEB) {
                    continueWithEmailBtn.setText("Continue With Mobile Number");
                    continueWithEmailBtn.setCompoundDrawablesRelativeWithIntrinsicBounds
                            (0, 0, R.drawable.ic_baseline_arrow_drop_down, 0);
                    registerLayout.setVisibility(View.VISIBLE);
                    CWEB = false;
                } else {
                    continueWithEmailBtn.setText("Continue With Email");
                    continueWithEmailBtn.setCompoundDrawablesRelativeWithIntrinsicBounds
                            (0, 0, R.drawable.ic_baseline_arrow_drop_up, 0);
                    registerLayout.setVisibility(View.INVISIBLE);
                    CWEB = true;
                }
            }
        });

        continueWithGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                signIn();
            }
        });

        regEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        regFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        regPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        regConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = regEmail.getText().toString();
                String txt_password = regPassword.getText().toString();
                String txt_confirm_password = regConfirmPassword.getText().toString();
                if (isEmailValid(txt_email)) {
                    if (txt_password.equals(txt_confirm_password)) {
                        // RegisterUser(txt_email, txt_password);
                        RegisterWithEmailAndPassword(txt_email, txt_password, regFullName.getText().toString(), getActivity(), registerUserBtn);
                    } else {
                        Toast.makeText(getActivity(), "Password and Confirm Password must be same", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Email is not valid", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void checkInputs() {

        if (!TextUtils.isEmpty(regFullName.getText().toString())) {
            if (!TextUtils.isEmpty(regEmail.getText().toString())) {
                if (!TextUtils.isEmpty(regPassword.getText())) {
                    if (!TextUtils.isEmpty(regConfirmPassword.getText().toString()) && regPassword.getText().toString().equals(regConfirmPassword.getText().toString())) {
                        registerUserBtn.setEnabled(true);
                        registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#132AF6")));
                    } else {
                        //Toast.makeText(getActivity(), "Password Required", Toast.LENGTH_SHORT).show();
                        registerUserBtn.setEnabled(false);
                        registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
                    }
                } else {
                    //Toast.makeText(getActivity(), "Mobile No. Required", Toast.LENGTH_SHORT).show();
                    registerUserBtn.setEnabled(false);
                    registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
                }
            } else {
                // Toast.makeText(getActivity(), "Name Required", Toast.LENGTH_SHORT).show();
                registerUserBtn.setEnabled(false);
                registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
            }
        } else {
            //Toast.makeText(getActivity(), "Email Required", Toast.LENGTH_SHORT).show();
            registerUserBtn.setEnabled(false);
            registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
        }

    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                progressDialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        checkUserAlreadyRegistered(credential);
    }

    private void checkUserAlreadyRegistered(AuthCredential credential) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        String GMail = account.getEmail();
        mAuth.createUserWithEmailAndPassword(GMail, "defaultPassword")
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

//                            Log.d("status", task.getException().getMessage().toString());
//                            progressDialog.dismiss();
//                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);

                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "signInWithCredential:success");
                                                progressDialog.dismiss();
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                updateUI(user);
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                progressDialog.dismiss();
                                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                updateUI(null);
                                            }
                                        }
                                    });

                        } else {

                            mAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // Sign in success, update UI with the signed-in user's information
                                                Log.d(TAG, "signInWithCredential:success");
                                                RegisterWithGmail(getContext(), getActivity());

                                            } else {
                                                // If sign in fails, display a message to the user.
                                                progressDialog.dismiss();
                                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                                updateUI(null);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////////


    private void expand(ExpandableLayout expandableLayout) {
        if (expandableLayout.isExpanded()) {
            expandableLayout.collapse();
        } else {
            expandableLayout.expand();
        }
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left, R.anim.slideout_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void setFragmentf(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slideout_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    public static void showProgressDialog() {
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            // Toast.makeText(getContext(), "Welcome Back", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
        }
    }


}