package com.example.projectkapadh.Fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.projectkapadh.MainActivity;
//import com.example.projectkapadh.NextToProfileActivity;
import com.example.projectkapadh.Model.ProfileDataModel;
import com.example.projectkapadh.NextToProfileActivity;
import com.example.projectkapadh.R;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

import static android.app.Activity.RESULT_OK;
import static com.example.projectkapadh.DbQueries.AuthenticationQueries.databaseReference;
import static com.example.projectkapadh.DbQueries.AuthenticationQueries.putUserDataWithMobile;
import static com.example.projectkapadh.Fragment.RegisterFragment.progressDialog;
import static com.example.projectkapadh.MainActivity.onVerifyOtpFromProfileFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout logOutBtn, passwordAndSecurityBtn, helpBtn, aboutUsBtn;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private TextView FullName, MobileNumber, isMobileVerifiedText, Email, isEmailVerifiedText, Address;
    private ImageView ProfileCover;
    private RoundedImageView ProfilePic;
    private Button verifyMobileNumberBtn, verifyEmailBtn, verifyAddressBtn;

    private DatabaseReference userDatabaseRef;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private String verificationId;
    private ImageButton editProfileBtn;
    private ImageView editCoverPicBtn;
    private TextView editProfilePicBtn;
    private ProgressDialog progressDialog;
    private boolean setCover = false, setProfile = false;
    private EditText editCustomerNameEditText;
    private boolean isDone = true;
    private CardView customerNameCardView;
    private boolean isPhoneAdded = false, isEmailAdded = false;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        //setProfileData(firebaseFirestore);
    }

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        logOutBtn = view.findViewById(R.id.logOutBtn);
        editProfileBtn = view.findViewById(R.id.editProfile);
        aboutUsBtn = view.findViewById(R.id.aboutUsBtn);
        helpBtn = view.findViewById(R.id.helpBtn);
        passwordAndSecurityBtn = view.findViewById(R.id.passwordAndSecurityBtn);
        editCustomerNameEditText = view.findViewById(R.id.customernameEditText);
        customerNameCardView = view.findViewById(R.id.cardviewCustomerName);

        mAuth = FirebaseAuth.getInstance();
        String UID = mAuth.getCurrentUser().getUid();

        ProfileCover = view.findViewById(R.id.coverPic);
        ProfilePic = view.findViewById(R.id.profilePic);
        FullName = view.findViewById(R.id.profileCustomerName);
        MobileNumber = view.findViewById(R.id.profileCustomerMobileNumber);
        isMobileVerifiedText = view.findViewById(R.id.mobileNumberVerificationTV);
        Email = view.findViewById(R.id.profileCustomerEmail);
        isEmailVerifiedText = view.findViewById(R.id.emailVerificationTV);
        Address = view.findViewById(R.id.profileCustomerAddress);

        verifyMobileNumberBtn = view.findViewById(R.id.profileVerifyMobileNumberBtn);
        verifyEmailBtn = view.findViewById(R.id.profileVerifyEmailBtn);
        verifyAddressBtn = view.findViewById(R.id.profileVerifyAddressBtn);

        userDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(UID).child("UserProfileData");

        firebaseFirestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
       // mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        editProfilePicBtn = view.findViewById(R.id.editProfilePicBtn);
        editCoverPicBtn = view.findViewById(R.id.editCoverPicBtn);

        setProfileData(firebaseFirestore);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading");
        progressDialog.setCanceledOnTouchOutside(false);


        return view;
    }

    private void setProfileData(FirebaseFirestore firebaseFirestore) {

        userDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ProfileDataModel profileData = snapshot.getValue(ProfileDataModel.class);

                if (getActivity() == null) {
                    return;
                }

                Glide.with(getContext()).load(profileData.getProfileCoverLink()).apply(new RequestOptions().placeholder(R.drawable.imageplaceholder)).into(ProfileCover);
                Glide.with(getContext()).load(profileData.getProfilePicLink()).apply(new RequestOptions().placeholder(R.drawable.ic_baseline_person_24)).into(ProfilePic);
                FullName.setText(profileData.getFullName());
                MobileNumber.setText(profileData.getMobileNumber());

                if(profileData.getMobileNumber().equals("Not added yet")){
                    isMobileVerifiedText.setVisibility(View.INVISIBLE);
                    verifyMobileNumberBtn.setText("Add");
                    verifyMobileNumberBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //showToast("Add number Dialog");
                           if (isPhoneAdded){
                               Toast.makeText(getActivity(), "Phone Verify Activity", Toast.LENGTH_SHORT).show();
                           }else{
                                final Dialog addNumberDialog = new Dialog(getActivity());
                            addNumberDialog.setContentView(R.layout.mobile_number_authentication_dialog);
                            addNumberDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            addNumberDialog.setCancelable(false);

                            Button cancel, addBtn;
                            EditText phoneNumber;
                            ProgressBar progressBar;
                            cancel = addNumberDialog.findViewById(R.id.dialogEmailCancelBtn);
                            addBtn = addNumberDialog.findViewById(R.id.dialogVerifyEmailBtn);
                            progressBar = addNumberDialog.findViewById(R.id.circular_progress);
                            phoneNumber = addNumberDialog.findViewById(R.id.phoneNumber);

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    addNumberDialog.dismiss();
                                }
                            });
                            addBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //addNumberDialog.dismiss();
                                    if(phoneNumber.getText().toString().isEmpty()){
                                        Toast.makeText(getActivity(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
                                    }else if(phoneNumber.getText().length() != 10){
                                        Toast.makeText(getActivity(), "Enter 10 digit mobile number", Toast.LENGTH_SHORT).show();
                                    }else if(!isPhoneValid(phoneNumber.getText().toString())){
                                        Toast.makeText(getActivity(), "Please enter a valid mobile number", Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                        progressBar.setVisibility(View.VISIBLE);

                                        userDatabaseRef.child("MobileNumber").setValue(phoneNumber.getText().toString())
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            verifyMobileNumberBtn.setText("Verify");
                                                            isPhoneAdded = true;
                                                            verifyMobileNumberBtn.setVisibility(View.VISIBLE);
                                                            progressBar.setVisibility(View.GONE);
                                                            setProfileData(firebaseFirestore);
                                                            addNumberDialog.dismiss();
                                                        }else{
                                                            showToast("Failed! Try Again");
                                                            addNumberDialog.dismiss();
                                                        }
                                                    }
                                                });
                                    }


//                                    firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                                            .collection("User_Data").document("User_Profile_data").update("MobileNumber", phoneNumber.getText().toString())
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                                    if (task.isSuccessful()){
//                                                        verifyMobileNumberBtn.setText("Verify");
//                                                        progressBar.setVisibility(View.GONE);
//                                                        setProfileData(firebaseFirestore);
//                                                        addNumberDialog.dismiss();
//                                                    }else{
//                                                        showToast("Failed! Try Again");
//                                                        addNumberDialog.dismiss();
//                                                    }
//                                                }
//                                            });
                                }
                            });

                            addNumberDialog.show();
                           }
                        }
                    });
                }else{
                    if(profileData.getIsMobileVerified().equals("No")){
                        isMobileVerifiedText.setVisibility(View.VISIBLE);
                        verifyMobileNumberBtn.setText("Verify");
                        verifyMobileNumberBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Verify phone activity", Toast.LENGTH_SHORT).show();
                                onVerifyOtpFromProfileFragment = true;
                                startActivity(new Intent(getActivity(), MainActivity.class)
                                        .putExtra("Fragment", "VerificationFragment")
                                        .putExtra("PhoneNumber", MobileNumber.getText().toString()));
                            }
                        });
                    }else{
                        isMobileVerifiedText.setText("Verified");
                        isMobileVerifiedText.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
                        isMobileVerifiedText.setVisibility(View.VISIBLE);
                        verifyMobileNumberBtn.setVisibility(View.INVISIBLE);
                    }

                }

                Email.setText(profileData.getEmail());

                if(profileData.getEmail().equals("Not added yet")){
                    isEmailVerifiedText.setVisibility(View.INVISIBLE);
                    verifyEmailBtn.setText("Add");
                    verifyEmailBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(isEmailAdded){
                                Toast.makeText(getActivity(), "Verify email activity", Toast.LENGTH_SHORT).show();
                            }else{
                                //showToast("Add email Dialog");
                                final Dialog addEmailDialog = new Dialog(getActivity());
                                addEmailDialog.setContentView(R.layout.add_email_dialog);
                                addEmailDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                addEmailDialog.setCancelable(false);

                                Button cancel, addEmailBtn;
                                EditText newEmail;
                                ProgressBar progressBar;

                                cancel = addEmailDialog.findViewById(R.id.dialogEmailCancelBtn);
                                addEmailBtn = addEmailDialog.findViewById(R.id.dialogVerifyEmailBtn);
                                progressBar = addEmailDialog.findViewById(R.id.circular_progress);
                                newEmail = addEmailDialog.findViewById(R.id.dialogEmailET);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                    linkSentText.setVisibility(View.VISIBLE);
//                                    progressBar.setVisibility(View.GONE);
                                        addEmailDialog.dismiss();
                                    }
                                });

                                addEmailBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        if(newEmail.getText().toString().isEmpty()){
                                            Toast.makeText(getActivity(), "Please enter email address", Toast.LENGTH_SHORT).show();
                                        }else if(!isEmailValid(newEmail.getText().toString())){
                                            Toast.makeText(getActivity(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
                                        }else{
                                            progressBar.setVisibility(View.VISIBLE);

                                            userDatabaseRef.child("Email").setValue(newEmail.getText().toString())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                verifyEmailBtn.setText("Verify");
                                                                isEmailAdded = true;
                                                                verifyEmailBtn.setVisibility(View.VISIBLE);
                                                                progressBar.setVisibility(View.GONE);
                                                                setProfileData(firebaseFirestore);
                                                                addEmailDialog.dismiss();
                                                            }else{
                                                                showToast("Failed! Try Again");
                                                                progressBar.setVisibility(View.GONE);
                                                                addEmailDialog.dismiss();
                                                            }
                                                        }
                                                    });
                                        }

                                    }
                                });

                                addEmailDialog.show();
                            }

                        }
                    });
                }else{
                    if (profileData.getIsEmailVerified().equals("No")){
                        isEmailVerifiedText.setVisibility(View.VISIBLE);
                        verifyEmailBtn.setText("Verify");
                        verifyEmailBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Verify Email activity", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        isEmailVerifiedText.setText("Verified");
                        isEmailVerifiedText.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
                        isEmailVerifiedText.setVisibility(View.VISIBLE);
                        verifyEmailBtn.setVisibility(View.INVISIBLE);
                    }
                }

                Address.setText(profileData.getAddress());

                if(profileData.getAddress().equals("Not added yet")){
                    verifyAddressBtn.setText("Add");
                    verifyAddressBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //showToast("Add address Dialog");
                            editAddress();
                        }
                    });
                }else{
                    verifyAddressBtn.setText("Change");
                    verifyAddressBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editAddress();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//        firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                .collection("User_Data").document("User_Profile_data")
//                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot docSnap = task.getResult();
//
//                    if (getActivity() == null) {
//                        return;
//                    }
//
//                    Glide.with(getContext()).load(docSnap.get("ProfileCoverLink")).apply(new RequestOptions().placeholder(R.drawable.imageplaceholder)).into(ProfileCover);
//                    Glide.with(getContext()).load(docSnap.get("ProfilePicLink")).apply(new RequestOptions().placeholder(R.drawable.imageplaceholder)).into(ProfilePic);
//                    FullName.setText(docSnap.get("Full_Name").toString());
//                    MobileNumber.setText(docSnap.get("MobileNumber").toString());
//
//                    if(!(boolean)docSnap.get("isMobileVerified")){
//                        isMobileVerified.setVisibility(View.VISIBLE);
//                    }else{
//                        isMobileVerified.setText("Verified");
//                        isMobileVerified.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
//                        isMobileVerified.setVisibility(View.VISIBLE);
//                    }
//                    if(docSnap.get("MobileNumber").toString().equals("Not added yet")){
//                        isMobileVerified.setVisibility(View.INVISIBLE);
//                        verifyMobileNumberBtn.setText("Add");
//                        verifyMobileNumberBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //showToast("Add number Dialog");
//                                final Dialog addNumberDialog = new Dialog(getActivity());
//                                addNumberDialog.setContentView(R.layout.mobile_number_authentication_dialog);
//                                addNumberDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                addNumberDialog.setCancelable(false);
//
//                                Button cancel, sendOtp;
//                                EditText phoneNumber;
//                                ProgressBar progressBar;
//                                cancel = addNumberDialog.findViewById(R.id.dialogCancelBtn);
//                                sendOtp = addNumberDialog.findViewById(R.id.verifyEmail);
//                                progressBar = addNumberDialog.findViewById(R.id.circular_progress);
//                                phoneNumber = addNumberDialog.findViewById(R.id.phoneNumber);
//
//                                cancel.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        addNumberDialog.dismiss();
//                                    }
//                                });
//                                sendOtp.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        //addNumberDialog.dismiss();
//                                        progressBar.setVisibility(View.VISIBLE);
//                                        firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                                                .collection("User_Data").document("User_Profile_data").update("MobileNumber", phoneNumber.getText().toString())
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                                        if (task.isSuccessful()){
//                                                            verifyMobileNumberBtn.setText("Verify");
//                                                            progressBar.setVisibility(View.GONE);
//                                                            setProfileData(firebaseFirestore);
//                                                            addNumberDialog.dismiss();
//                                                        }else{
//                                                            showToast("Failed! Try Again");
//                                                            addNumberDialog.dismiss();
//                                                        }
//                                                    }
//                                                });
//                                    }
//                                });
//
//                                addNumberDialog.show();
//                            }
//                        });
//                    }
//
//                    Email.setText(docSnap.get("Email").toString());
//
//                    if(!(boolean)docSnap.get("isEmailVerified")){
//                        isEmailVerified.setVisibility(View.VISIBLE);
//                    }else{
//                        isEmailVerified.setText("Verified");
//                        isEmailVerified.setTextColor(ContextCompat.getColor(getContext(), R.color.teal_200));
//                        isEmailVerified.setVisibility(View.VISIBLE);
//                    }
//                    if(docSnap.get("Email").toString().equals("Not added yet")){
//                        isEmailVerified.setVisibility(View.INVISIBLE);
//                        verifyEmailBtn.setText("Add");
//                        verifyEmailBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //showToast("Add email Dialog");
//                                final Dialog addEmailDialog = new Dialog(getActivity());
//                                addEmailDialog.setContentView(R.layout.add_email_dialog);
//                                addEmailDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                                addEmailDialog.setCancelable(false);
//
//                                Button cancel, sendOtp;
//                                ProgressBar progressBar;
//                                TextView linkSentText;
//                                cancel = addEmailDialog.findViewById(R.id.dialogCancelBtn);
//                                sendOtp = addEmailDialog.findViewById(R.id.verifyEmail);
//                                progressBar = addEmailDialog.findViewById(R.id.circular_progress);
//                                linkSentText = addEmailDialog.findViewById(R.id.verificationLinkSentText);
//
//                                cancel.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        linkSentText.setVisibility(View.VISIBLE);
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                });
//
//                                sendOtp.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        //addNumberDialog.dismiss();
//                                        progressBar.setVisibility(View.VISIBLE);
//                                        linkSentText.setVisibility(View.GONE);
//                                        showToast("link sent");
//                                    }
//                                });
//                                addEmailDialog.show();
//                            }
//                        });
//                    }
//                    Address.setText(docSnap.get("Address").toString());
//                    if(docSnap.get("Address").toString().equals("Not added yet")){
//                        verifyAddressBtn.setText("Add");
//                        verifyAddressBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                //showToast("Add address Dialog");
//                                editAddress();
//                            }
//                        });
//                    }else{
//                        verifyAddressBtn.setText("Change");
//                        verifyAddressBtn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                editAddress();
//                            }
//                        });
//                    }
//                }
//            }
//        });


    }

    private void editAddress() {
        final Dialog addAddress = new Dialog(getActivity());
        addAddress.setContentView(R.layout.ad_new_address_dialog_layout);
        addAddress.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addAddress.setCancelable(false);

        Button cancel, save;
        EditText addressline1, addressline2, city, state, country, postalpin;
        ProgressBar address_progressbar;

        cancel = addAddress.findViewById(R.id.cancel);
        save = addAddress.findViewById(R.id.save);
        addressline1 = addAddress.findViewById(R.id.addressline1);
        addressline2 = addAddress.findViewById(R.id.addressline2);
        city = addAddress.findViewById(R.id.city);
        state = addAddress.findViewById(R.id.state);
        country = addAddress.findViewById(R.id.country);
        postalpin = addAddress.findViewById(R.id.postalPIN);
        address_progressbar = addAddress.findViewById(R.id.address_progressbar);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddress.dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                address_progressbar.setVisibility(View.VISIBLE);
                String add1, add2, cty, stat, coun, pPin;

                add1 = addressline1.getText().toString();
                add2 = addressline2.getText().toString();
                cty = city.getText().toString().trim();
                stat = state.getText().toString().trim();
                coun = country.getText().toString().trim();
                pPin = postalpin.getText().toString().trim();

                if(add1.isEmpty() || add2.isEmpty() || cty.isEmpty() || stat.isEmpty() || coun.isEmpty() || pPin.isEmpty()){
                    showToast("All the fields are mandatory");
                    address_progressbar.setVisibility(View.GONE);
                }else{
                    String address = add1+", "+add2+", "+cty+", "+stat+ " - "+pPin+" (" + coun + ")";

                    userDatabaseRef.child("Address").setValue(address)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        verifyAddressBtn.setText("Change");
                                        setProfileData(firebaseFirestore);
                                        addAddress.dismiss();
                                    }else{
                                        showToast("Failed! Try Again");
                                        addAddress.dismiss();
                                        address_progressbar.setVisibility(View.GONE);
                                    }
                                }
                            });

//                    firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                            .collection("User_Data").document("User_Profile_data").update("Address", address)
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                    if (task.isSuccessful()){
//                                        verifyAddressBtn.setText("Change");
//                                        setProfileData(firebaseFirestore);
//                                        addAddress.dismiss();
//                                    }else{
//                                        showToast("Failed! Try Again");
//                                        addAddress.dismiss();
//                                        address_progressbar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
                }

            }
        });
        addAddress.show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        passwordAndSecurityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showToast("Password and Security");
                startActivity(new Intent(getActivity(), NextToProfileActivity.class).putExtra("setFragment", "PasswordAndSecurity"));
            }
        });

        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showToast("Help");
                startActivity(new Intent(getActivity(), NextToProfileActivity.class).putExtra("setFragment", "Help"));

            }
        });

        aboutUsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showToast("About Us");
                startActivity(new Intent(getActivity(), NextToProfileActivity.class).putExtra("setFragment", "AboutUs"));

            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog logoutDialog = new Dialog(getActivity());
                logoutDialog.setContentView(R.layout.signout_dialog);
                logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                logoutDialog.setCancelable(true);

                Button cancelSignOut, yesSignOut;
                cancelSignOut = logoutDialog.findViewById(R.id.cancel_sign_out);
                yesSignOut = logoutDialog.findViewById(R.id.yes_sign_out);

                cancelSignOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutDialog.dismiss();
                    }
                });

                yesSignOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        logoutDialog.dismiss();
                        signOut();
                    }
                });
                logoutDialog.show();
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDone){
                    isDone = false;
                    editCustomerNameEditText.setText(FullName.getText().toString());
                    customerNameCardView.setVisibility(View.VISIBLE);
                    editCustomerNameEditText.setVisibility(View.VISIBLE);
                    FullName.setVisibility(View.INVISIBLE);
                    editProfileBtn.setImageResource(R.drawable.ic_baseline_done_24);
                }else{
                    isDone = true;
                    userDatabaseRef.child("FullName").setValue(editCustomerNameEditText.getText().toString());
                    customerNameCardView.setVisibility(View.INVISIBLE);
                    editCustomerNameEditText.setVisibility(View.INVISIBLE);
                    FullName.setVisibility(View.VISIBLE);
                    editProfileBtn.setImageResource(R.drawable.ic_baseline_edit_24);
                }
            }
        });

        editCoverPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCover = true;
                pick(409, 180);
            }
        });

        editProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfile = true;
                pick(1,1);
            }
        });
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mAuth.signOut();
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }
                    }
                });
    }

    private void showToast(String str){
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }




    ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
                if (result.getResultCode() == RESULT_OK) {
                    Uri uri = result.getData().getData();
                    // Use the uri to load the image
                    if(setCover) {
                        setCover = false;
                        ProfileCover.setImageURI(uri);
                        uploadCoverPic(uri);
                       // coverPic.setImageURI(uri);
                    }
                    if(setProfile){
                        setProfile = false;
                        ProfilePic.setImageURI(uri);
                        uploadProfilePic(uri);
                       // profilePic.setImageURI(uri);
                    }
                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
                }
            });

    private void pick(int x, int y) {

        ImagePicker.Companion.with(getActivity())
                .crop(x,y)
                //.cropOval()
                .maxResultSize(1080, 1080, true)
                .createIntentFromDialog((Function1) (new Function1() {
                    public Object invoke(Object var1) {
                        this.invoke((Intent) var1);
                        return Unit.INSTANCE;
                    }

                    public final void invoke(@NotNull Intent it) {
                        Intrinsics.checkNotNullParameter(it, "it");
                        launcher.launch(it);
                    }
                }));

    }

    private void uploadProfilePic(Uri uri) {

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference ref = storageReference.child("image").child("Profile Images").child(mAuth.getCurrentUser().getUid());

        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String profilePicUrl = String.valueOf(uri);
                        //progressDialog.dismiss();
                        //Toast.makeText(getContext(), profilePicUrl, Toast.LENGTH_SHORT).show();

                        userDatabaseRef.child("ProfilePicLink").setValue(profilePicUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            //getActivity().finish();
                                        }else{
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

//                        firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                                .collection("User_Data").document("User_Profile_data").update("ProfilePicLink", profilePicUrl)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                        if (task.isSuccessful()){
//                                            progressDialog.dismiss();
//                                            //getActivity().finish();
//                                        }else{
//                                            progressDialog.dismiss();
//                                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading : " + (int) progressPercentage + "%");
            }
        });


    }

    private void uploadCoverPic(Uri uri) {

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference ref = storageReference.child("image").child("Cover Images").child(mAuth.getCurrentUser().getUid());

        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String coverPicUrl = String.valueOf(uri);
                        //progressDialog.dismiss();
                        //Toast.makeText(getContext(), coverPicUrl, Toast.LENGTH_SHORT).show();

                        userDatabaseRef.child("ProfileCoverLink").setValue(coverPicUrl)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            progressDialog.dismiss();
                                            //getActivity().finish();
                                        }else{
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

//                        firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                                .collection("User_Data").document("User_Profile_data").update("ProfileCoverLink", coverPicUrl)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                                        if (task.isSuccessful()){
//                                            progressDialog.dismiss();
//                                            //getActivity().finish();
//                                        }else{
//                                            progressDialog.dismiss();
//                                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploading : " + (int) progressPercentage + "%");
            }
        });
    }

}