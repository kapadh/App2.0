package com.example.projectkapadh.DbQueries;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.projectkapadh.Fragment.RegisterFragment;
import com.example.projectkapadh.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.projectkapadh.Fragment.RegisterFragment.progressDialog;
import static com.example.projectkapadh.Fragment.RegisterFragment.showProgressDialog;

public class AuthenticationQueries {

    ///////////////////////////////////////////////////////////////////////

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
   // public static String UserUID = mAuth.getCurrentUser().getUid();

    ///////////////////////////////////////////////////////////////////////

    public static void RegisterWithEmailAndPassword(String txt_email, String txt_password, String txt_user_name , Activity registerActivity, Button registerBtn){
        showProgressDialog();
        //registerUserBtn.setEnabled(false);
        //registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#80091262")));

        mAuth.createUserWithEmailAndPassword(txt_email,txt_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    String Address = "Not added yet";
                    String MobileNumber = "Not added yet";
                    String ProfileCoverLink = "Not added yet";
                    String ProfilePicLink = "Not added yet";

                    HashMap<String ,Object> userMap = new HashMap<>();
                    userMap.put("FullName",txt_user_name);
                    userMap.put("Email",txt_email);
                    userMap.put("Address", Address);
                    userMap.put("MobileNumber",MobileNumber);
                    userMap.put("ProfileCoverLink",ProfileCoverLink);
                    userMap.put("ProfilePicLink",ProfilePicLink);
                    userMap.put("isEmailVerified","No");
                    userMap.put("isMobileVerified", "No");

                    databaseReference.child("Users").child(mAuth.getUid()).child("UserProfileData").setValue(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressDialog.dismiss();
                                        registerActivity.startActivity(new Intent(registerActivity, HomeActivity.class));
                                        registerActivity.finish();
                                    }else{
                                        progressDialog.dismiss();
                                        registerBtn.setEnabled(true);
                                        registerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
                                        Toast.makeText(registerActivity, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
//                    firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("User_Profile_data")
//                            .set(userMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        HashMap<String ,Object> UserOrderHistory = new HashMap<>();
//                                        UserOrderHistory.put("history_list_size",(long)0);
//                                        firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("Order_history")
//                                                .set(UserOrderHistory).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()){
//                                                    progressDialog.dismiss();
//                                                    registerActivity.startActivity(new Intent(registerActivity, HomeActivity.class));
//                                                    registerActivity.finish();
//
//
//                                                }else{
//                                                    progressDialog.dismiss();
//                                                    registerBtn.setEnabled(true);
//                                                    registerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
//                                                    Toast.makeText(registerActivity, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    }else{
//                                        Toast.makeText(registerActivity, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });

                }else{
                    progressDialog.dismiss();
                    registerBtn.setEnabled(false);
                    registerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
                    Toast.makeText(registerActivity, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void RegisterWithGmail(Context context, Activity registerActivity){

        String UserUID = mAuth.getUid();

            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);
            if(account != null){
                String GMail = account.getEmail();
                String FullName = account.getDisplayName();
                String ProfilePicLink = account.getPhotoUrl().toString();

                String Address = "Not added yet";
                String MobileNumber = "Not added yet";
                String ProfileCoverLink = "Not added yet";

                HashMap<String ,Object> userMap = new HashMap<>();
                userMap.put("FullName",FullName);
                userMap.put("Email",GMail);
                userMap.put("Address", Address);
                userMap.put("MobileNumber",MobileNumber);
                userMap.put("ProfileCoverLink",ProfileCoverLink);
                userMap.put("ProfilePicLink",ProfilePicLink);
                userMap.put("isEmailVerified","Yes");
                userMap.put("isMobileVerified", "No");

                databaseReference.child("Users").child(UserUID).child("UserProfileData").setValue(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    //progressDialog.dismiss();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user, registerActivity, context);
                                }else{
                                    progressDialog.dismiss();
                                    Toast.makeText(registerActivity, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
//                    firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("User_Profile_data")
//                            .set(userMap)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        HashMap<String ,Object> UserOrderHistory = new HashMap<>();
//                                        UserOrderHistory.put("history_list_size",(long)0);
//                                        firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("Order_history")
//                                                .set(UserOrderHistory).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                if (task.isSuccessful()){
//                                                    FirebaseUser user = mAuth.getCurrentUser();
//                                                    updateUI(user, registerActivity, context);
//                                                }else{
//                                                    progressDialog.dismiss();
//                                                    //registerUserBtn.setEnabled(true);
//                                                    //registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
//                                                    Toast.makeText(context, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                }
//                                            }
//                                        });
//                                    }else{
//                                        Toast.makeText(context, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });

            }else{
                updateUI(null, registerActivity, context);
            }

    }

    public static void putUserDataWithMobile(String phoneNumber, Activity activity) {

        String FullName = "Username";
        String Email = "Not added yet";
        String Address = "Not added yet";
        String MobileNumber = phoneNumber;
        String ProfileCoverLink = "Not added yet";
        String ProfilePicLink = "Not added yet";

        HashMap<String ,Object> userMap = new HashMap<>();
        userMap.put("FullName",FullName);
        userMap.put("Email",Email);
        userMap.put("Address", Address);
        userMap.put("MobileNumber",MobileNumber);
        userMap.put("ProfileCoverLink",ProfileCoverLink);
        userMap.put("ProfilePicLink",ProfilePicLink);
        userMap.put("isEmailVerified","No");
        userMap.put("isMobileVerified", "Yes");


//        firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("User_Profile_data")
//                .set(userMap)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()){
//                            HashMap<String ,Object> UserOrderHistory = new HashMap<>();
//                            UserOrderHistory.put("history_list_size",(long)0);
//                            firebaseFirestore.collection("USERS").document(mAuth.getUid()).collection("User_Data").document("Order_history")
//                                    .set(UserOrderHistory).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        startActivity(new Intent(getActivity(), HomeActivity.class));
//                                        getActivity().finish();
//                                    }else{
//                                        progressDialog.dismiss();
//                                        //registerUserBtn.setEnabled(true);
//                                        //registerUserBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
//                                        Toast.makeText(getActivity(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }else{
//                            Toast.makeText(getActivity(), "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        databaseReference.child("Users").child(mAuth.getUid()).child("UserProfileData").setValue(userMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressDialog.dismiss();
                            activity.startActivity(new Intent(activity, HomeActivity.class));
                            activity.finish();
                        }else{
                            progressDialog.dismiss();
//                            registerBtn.setEnabled(true);
//                            registerBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9999CE")));
                            Toast.makeText(activity, "Error : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });

    }

    public static void updateUI(FirebaseUser user, Activity registerActivity, Context context) {
        if(user == null){
            // Toast.makeText(getContext(), "Welcome Back", Toast.LENGTH_SHORT).show();
        }else{
            context.startActivity(new Intent(registerActivity, HomeActivity.class));
            registerActivity.finish();
        }
    }



}
