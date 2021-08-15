package com.example.projectkapadh.Fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projectkapadh.HomeActivity;
import com.example.projectkapadh.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static com.example.projectkapadh.DbQueries.AuthenticationQueries.putUserDataWithMobile;
import static com.example.projectkapadh.Fragment.RegisterFragment.mobileNumber;
import static com.example.projectkapadh.Fragment.RegisterFragment.progressDialog;
import static com.example.projectkapadh.Fragment.RegisterFragment.showProgressDialog;
//import static com.example.projectkapadh.Fragment.RegisterFragment.verificationId;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VerifyOtpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyOtpFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VerifyOtpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment verifyOtpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyOtpFragment newInstance(String param1, String param2) {
        VerifyOtpFragment fragment = new VerifyOtpFragment();
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
            userPhoneNumber = getArguments().getString("PhoneNumber");
            fromProfile = getArguments().getString("FromProfile");
        }
    }

    private EditText inputCode1, inputCode2, inputCode3, inputCode4, inputCode5, inputCode6;
    private TextView textMobileNumber, resendOtpIn60Seconds;
    private Button verifyOtpBtn, resendOtpBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String userPhoneNumber, fromProfile = "";

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_verify_otp, container, false);
        //userPhoneNumber = mobileNumber.getText().toString();

        //onVerifyOtpFragment = true;
        databaseReference = FirebaseDatabase.getInstance().getReference();

        textMobileNumber = view.findViewById(R.id.textMobileNumber);
        textMobileNumber.setText(String.format(
                "+91-%s", userPhoneNumber
        ));

        inputCode1 = view.findViewById(R.id.inputCode1);
        inputCode2 = view.findViewById(R.id.inputCode2);
        inputCode3 = view.findViewById(R.id.inputCode3);
        inputCode4 = view.findViewById(R.id.inputCode4);
        inputCode5 = view.findViewById(R.id.inputCode5);
        inputCode6 = view.findViewById(R.id.inputCode6);
        verifyOtpBtn = view.findViewById(R.id.veriftOtpBtn);
        resendOtpBtn = view.findViewById(R.id.resendOtpBtn);
        resendOtpIn60Seconds = view.findViewById(R.id.resendOtpIn60Seconds);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setupOTPInput();

        verifyOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verification();
            }
        });

        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCode();
            }
        });

        new CountDownTimer(60000,1000){

            @Override
            public void onTick(long l) {
                resendOtpIn60Seconds.setText("Resend OTP in " + l/1000 + " Seconds");
                resendOtpIn60Seconds.setVisibility(View.VISIBLE);
                resendOtpBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80091262")));
                resendOtpBtn.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resendOtpIn60Seconds.setVisibility(View.INVISIBLE);
                resendOtpBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#132AF6")));
                resendOtpBtn.setEnabled(true);
            }
        }.start();

        Callbacks();

        sendOtp(userPhoneNumber);

        return view;
    }

    private void Callbacks() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                if(fromProfile != null){
                    databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("UserProfileData")
                            .child("isMobileVerified").setValue("Yes");
                    linkUserAuth(credential);
                    return;
                }

                progressDialog.dismiss();
                Log.d("TAG", "onVerificationCompleted:" + credential);
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

                Log.d("TAG Failed", e.getMessage());
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                Log.w("TAG", "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(getActivity(), "Invalid request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(getActivity(), "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {

//                progressDialog.dismiss();
                Log.d("TAG", "onCodeSent:" + verificationId);
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };

    }

    private void verification(){
        if(inputCode1.getText().toString().trim().isEmpty() ||
                inputCode2.getText().toString().trim().isEmpty() ||
                inputCode3.getText().toString().trim().isEmpty() ||
                inputCode4.getText().toString().trim().isEmpty() ||
                inputCode5.getText().toString().trim().isEmpty() ||
                inputCode6.getText().toString().trim().isEmpty()){
            Toast.makeText(getContext(), "Please enter valid code", Toast.LENGTH_SHORT).show();
            return;
        }
        String code = inputCode1.getText().toString() +
                inputCode2.getText().toString() +
                inputCode3.getText().toString() +
                inputCode4.getText().toString() +
                inputCode5.getText().toString() +
                inputCode6.getText().toString();

        if (mVerificationId != null){

            showProgressDialog();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            if(fromProfile != null){
                databaseReference.child("Users").child(mAuth.getCurrentUser().getUid()).child("UserProfileData")
                        .child("isMobileVerified").setValue("Yes");
                linkUserAuth(credential);
                return;
            }

            signInWithPhoneAuthCredential(credential);
//            FirebaseAuth.getInstance().signInWithCredential(credential)
//                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            progressDialog.dismiss();
//                            if (task.isSuccessful()){
//                                //Toast.makeText(getContext(), "Verification Completed", Toast.LENGTH_SHORT).show();
//                                //putUserDataWithMobile();
//                                putUserDataWithMobile(userPhoneNumber, getActivity());
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                updateUI(user);
//                            }else{
//                                Toast.makeText(getContext(), "The verification code entered was invalid", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
        }
    }

//    private void putUserDataWithMobile() {
//
//        String FullName = "Username";
//        String Email = "Not added yet";
//        String Address = "Not added yet";
//        String MobileNumber = userPhoneNumber;
//        String ProfileCoverLink = "Not added yet";
//        String ProfilePicLink = "Not added yet";
//
//        HashMap<String ,Object> userMap = new HashMap<>();
//        userMap.put("FullName",FullName);
//        userMap.put("Email",Email);
//        userMap.put("Address", Address);
//        userMap.put("MobileNumber",MobileNumber);
//        userMap.put("ProfileCoverLink",ProfileCoverLink);
//        userMap.put("ProfilePicLink",ProfilePicLink);
//        userMap.put("isEmailVerified","No");
//        userMap.put("isMobileVerified", "Yes");
//
//
////        firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("User_Profile_data")
////                .set(userMap)
////                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                    @Override
////                    public void onComplete(@NonNull Task<Void> task) {
////                        if (task.isSuccessful()){
////                            HashMap<String ,Object> UserOrderHistory = new HashMap<>();
////                            UserOrderHistory.put("history_list_size",(long)0);
////                            firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("Order_history")
////                                    .set(UserOrderHistory).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                @Override
////                                public void onComplete(@NonNull Task<Void> task) {
////                                    if (task.isSuccessful()){
////                                        startActivity(new Intent(getActivity(), HomeActivity.class));
////                                        getActivity().finish();
////                                    }else{
////                                        progressDialog.dismiss();
////                                        //registerUserBtn.setEnabled(true);
////                                        //registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
////                                        Toast.makeText(getActivity(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
////                                    }
////                                }
////                            });
////                        }else{
////                            Toast.makeText(getActivity(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
////                        }
////                    }
////                });
//
//        databaseReference.child("Users").child(mAuth.getUid()).child("UserProfileData").setValue(userMap)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            progressDialog.dismiss();
//                            startActivity(new Intent(getActivity(), HomeActivity.class));
//                            getActivity().finish();
//                        }else{
//                            progressDialog.dismiss();
////                            registerBtn.setEnabled(true);
////                            registerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
//                            Toast.makeText(getActivity(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//
//                        }
//                    }
//                });
//
//    }

    private void resendCode(){

        new CountDownTimer(60000,1000){

            @Override
            public void onTick(long l) {
                resendOtpIn60Seconds.setText("Resend OTP in " + l/1000 + " Seconds");
                resendOtpIn60Seconds.setVisibility(View.VISIBLE);
                resendOtpBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80091262")));
                resendOtpBtn.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resendOtpIn60Seconds.setVisibility(View.INVISIBLE);
                resendOtpBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#132AF6")));
                resendOtpBtn.setEnabled(true);
            }
        }.start();

        resendVerificationCode(userPhoneNumber, mResendToken);


    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void sendOtp(String mobileNumber) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91" + mobileNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(getActivity())                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Log.d("TAG", "signInWithCredential:success");
                            String uid = mAuth.getCurrentUser().getUid();
                            Log.d("PhoneUID", uid);

                            databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(uid)) {
                                        // run some code
                                        FirebaseUser user = task.getResult().getUser();
                                        updateUI(user);
                                    }else{
                                        progressDialog.setTitle("Registering User");
                                        progressDialog.setMessage("Wait a while...");
                                        putUserDataWithMobile(userPhoneNumber, getActivity());
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

//                            FirebaseUser user = task.getResult().getUser();
//                            updateUI(user);

                           // putUserDataWithMobile(userPhoneNumber, getActivity());

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });

    }

    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            startActivity(new Intent(getActivity(), HomeActivity.class));
            getActivity().finish();
        }
    }

    private void setupOTPInput() {

        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    inputCode2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    inputCode3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    inputCode4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    inputCode5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()){
                    inputCode6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void linkUserAuth(PhoneAuthCredential credential){
        //phoneCredential=PhoneAuthProvider.getCredential(verificationID,verificationCode);
        mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("TAG","linkWithCredential:success");
                    //FirebaseUser mergeAuthUser=task.getResult().getUser();
                    getActivity().finish();
                }else{
                    Log.w("TAG","linkWithCredential:failure",task.getException());
                }
            }
        });
    }


}