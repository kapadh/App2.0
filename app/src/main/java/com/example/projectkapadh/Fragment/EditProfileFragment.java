//package com.example.projectkapadh.Fragment;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.res.Resources;
//import android.net.Uri;
//import android.os.Bundle;
//
//import androidx.activity.result.ActivityResult;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//
//import android.util.DisplayMetrics;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.example.projectkapadh.R;
//import com.github.drjacky.imagepicker.ImagePicker;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.gms.tasks.Task;
//import com.google.android.material.snackbar.Snackbar;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
////import com.google.firebase.storage.FirebaseStorage;
////import com.google.firebase.storage.OnProgressListener;
////import com.google.firebase.storage.StorageReference;
////import com.google.firebase.storage.UploadTask;
//import com.makeramen.roundedimageview.RoundedImageView;
//
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.UUID;
//
//import kotlin.Unit;
//import kotlin.jvm.functions.Function1;
//import kotlin.jvm.internal.Intrinsics;
//
//import static android.app.Activity.RESULT_OK;
//
//
///**
// * A simple {@link Fragment} subclass.
// * Use the {@link EditProfileFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class EditProfileFragment extends Fragment {
//
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public EditProfileFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment EditProfileFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static EditProfileFragment newInstance(String param1, String param2) {
//        EditProfileFragment fragment = new EditProfileFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
//
//    //////////////////////////////////////////////////////////////////////////
//
//    private RoundedImageView profilePic;
//    private ImageView coverPic;
//    private ImageButton editProfile, editCover;
//    private EditText customerDisplayName;
//    private Button saveProfile;
//    private boolean setCover = false, setProfile = false;
//
//    private FirebaseAuth mAuth;
//    private FirebaseStorage storage;
//    private StorageReference storageReference;
//    private FirebaseFirestore firebaseFirestore;
//
//    private String coverPicUrl, profilePicUrl;
//    private ProgressDialog progressDialog;
//
//    //////////////////////////////////////////////////////////////////////////
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
//
//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setTitle("Uploading");
//        progressDialog.setCanceledOnTouchOutside(false);
//
//        storage = FirebaseStorage.getInstance();
//        storageReference = storage.getReference();
//        mAuth = FirebaseAuth.getInstance();
//        firebaseFirestore = FirebaseFirestore.getInstance();
//
//        profilePic = view.findViewById(R.id.profilePic);
//        coverPic = view.findViewById(R.id.coverPic);
//        editProfile = view.findViewById(R.id.editProfilePic);
//        editCover = view.findViewById(R.id.editCoverPhoto);
//        customerDisplayName = view.findViewById(R.id.editCustomerNameEditText);
//        saveProfile = view.findViewById(R.id.saveProfile);
//
//
//
//
//        return view;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        editCover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setCover = true;
//                pick(409, 180);
//            }
//        });
//        editProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                setProfile = true;
//                pick(1,1);
//            }
//        });
//        saveProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadLinks();
//            }
//        });
//    }
//
//    ActivityResultLauncher<Intent> launcher =
//            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (ActivityResult result) -> {
//                if (result.getResultCode() == RESULT_OK) {
//                    Uri uri = result.getData().getData();
//                    // Use the uri to load the image
//                    if(setCover) {
//                        setCover = false;
//                        uploadCoverPic(uri);
//                        coverPic.setImageURI(uri);
//                    }
//                    if(setProfile){
//                        setProfile = false;
//                        uploadProfilePic(uri);
//                        profilePic.setImageURI(uri);
//                    }
//                } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
//                    // Use ImagePicker.Companion.getError(result.getData()) to show an error
//                }
//            });
//
//    private void pick(int x, int y) {
//
//        ImagePicker.Companion.with(getActivity())
//                .crop(x,y)
//                //.cropOval()
//                .maxResultSize(1080, 1080, true)
//                .createIntentFromDialog((Function1) (new Function1() {
//                    public Object invoke(Object var1) {
//                        this.invoke((Intent) var1);
//                        return Unit.INSTANCE;
//                    }
//
//                    public final void invoke(@NotNull Intent it) {
//                        Intrinsics.checkNotNullParameter(it, "it");
//                        launcher.launch(it);
//                    }
//                }));
//
//    }
//
//    private void uploadProfilePic(Uri uri) {
//
//        progressDialog.show();
//
//        StorageReference ref = storageReference.child("image").child("Profile Images").child(mAuth.getCurrentUser().getUid());
//
//        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        profilePicUrl = String.valueOf(uri);
//                        progressDialog.dismiss();
//                        Toast.makeText(getContext(), profilePicUrl, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//            progressDialog.dismiss();
//                Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                progressDialog.setMessage("Progress: " + (int) progressPercentage + "%");
//            }
//        });
//
//
//    }
//
//    private void uploadCoverPic(Uri uri) {
//
//        progressDialog.show();
//
//        StorageReference ref = storageReference.child("image").child("Cover Images").child(mAuth.getCurrentUser().getUid());
//
//        ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        coverPicUrl = String.valueOf(uri);
//                        progressDialog.dismiss();
//                        Toast.makeText(getContext(), coverPicUrl, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(getContext(), "Failed to upload", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                double progressPercentage = (100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
//                progressDialog.setMessage("Progress: " + (int) progressPercentage + "%");
//            }
//        });
//    }
//
//    private void uploadLinks(){
//
//        progressDialog.setMessage("Please Wait...");
//        progressDialog.show();
//
//        String customerName = customerDisplayName.getText().toString();
//        String cover = coverPicUrl;
//        String profile = profilePicUrl;
//
//        firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                .collection("User_Data").document("User_Profile_data").update("Full_Name", customerName)
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                        firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
//                                .collection("User_Data").document("User_Profile_data").update("ProfilePicLink", profile)
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
////                                        firebaseFirestore.collection("USERS").document(mAuth.getCurrentUser().getUid())
////                                                .collection("User_Data").document("User_Profile_data").update("ProfileCoverLink", cover)
////                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
////                                                    @Override
////                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
////                                                        if (task.isSuccessful()){
////                                                            progressDialog.dismiss();
////                                                            getActivity().finish();
////                                                        }else{
////                                                            progressDialog.dismiss();
////                                                            Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
////                                                        }
////                                                    }
////                                                });
//                                    }
//                                });
//                    }
//                });
//    }
//
//}