package com.example.projectkapadh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.skyfishjy.library.RippleBackground;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import timerx.Stopwatch;
import timerx.StopwatchBuilder;
import timerx.Timer;
import timerx.TimerBuilder;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;


public class RecordingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String title, SpUID;

    private Button bookServiceBtn;
    private ArrayList<String> spinnerList;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private TextInputLayout timingTIL;
    private AutoCompleteTextView timingACTV;
    private ArrayAdapter<String> aaTiming;

    public static String AudioFilePath = null;
    public static TextView recordQueryBtn;
    private TextInputEditText queryIET;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);

        recordQueryBtn = findViewById(R.id.recordQueryBtn);
        queryIET = findViewById(R.id.queryIET);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        storageReference = FirebaseStorage.getInstance().getReference();

        if (getIntent() != null) {
            title = getIntent().getStringExtra("SpName");
            SpUID = getIntent().getStringExtra("SpUID");
            AudioFilePath = getIntent().getStringExtra("AudioFilePath");
        }

        toolbar = findViewById(R.id.recordingToolbar);
        toolbar.setTitle(title);
        setActionBar(toolbar);
        View view = toolbar.getChildAt(1);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform actions
                finish();
            }
        });

        timingTIL = findViewById(R.id.tilTiming);
        timingACTV = findViewById(R.id.actTiming);

        spinnerList = new ArrayList<>();

        DatabaseReference timingRef = databaseReference.child("ServiceProviders").child(SpUID).child("AvailableTimings");
        timingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("AvailabilityStatus").getValue(boolean.class) && ds.child("BookingStatus").getValue(boolean.class)) {
                        spinnerList.add(ds.child("Timing").getValue(String.class));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        aaTiming = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, spinnerList);
        timingACTV.setAdapter(aaTiming);
        timingACTV.setThreshold(1);


        bookServiceBtn = findViewById(R.id.proceedToPayBtn);
        bookServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!timingACTV.getText().toString().isEmpty()){
                    if(!queryIET.getText().toString().isEmpty()){
                        if(!recordQueryBtn.getText().toString().equals("Record")){
                            checkOrderNumber();
                        }else{
                            Toast.makeText(RecordingActivity.this, "Please record query in voice", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RecordingActivity.this, "Please enter your query in text", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RecordingActivity.this, "Please select a timing", Toast.LENGTH_SHORT).show();
                }

            }
        });

        if(AudioFilePath != null){
            recordQueryBtn.setText(AudioFilePath);
        }

        recordQueryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordingActivity.this, RecordVoiceQueryActivity.class)
                        .putExtra("SpName", title)
                        .putExtra("FilePath", getRecordingFilePath()));
            }
        });

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private String getRecordingFilePath() {
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File musicDirectory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        File file = new File(musicDirectory, "Query_to_" + title + ".mp3");
        return file.getPath();
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private void checkOrderNumber(){
        String orderNo = orderNumbers();
        databaseReference.child("OrderNumbers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(orderNo)){
                    checkOrderNumber();
                }else{
                    BookServiceAndUpdateDate(orderNo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void BookServiceAndUpdateDate(String orderNumber){

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Uri uri = Uri.fromFile(new File(getRecordingFilePath()).getAbsoluteFile());

//        int index = timingACTV.getSelectedIndex();
        String Timing = timingACTV.getText().toString();

        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy, hh:mm a");
        String dateToStr = format.format(today);

        Log.d("Date n Time", dateToStr);
        Log.d("Timing", Timing);
        Log.d("Order Status", "pending");
        Log.d("Audio Path", AudioFilePath);

        String UserUID = mAuth.getCurrentUser().getUid();

        String Date = dateToStr;
        String ServiceTiming = Timing;
        String orderStatus = "Pending";
        String SpName = "Pankaj";
        String SpFee = "Rs. 200";
        String SpRatings = "4.6";
        String SpSubCat = "Nothing Yet";
        String filePath = AudioFilePath;

        HashMap<String, Object> OrderDetails = new HashMap<>();
        OrderDetails.put("OrderDateTime", Date);
        OrderDetails.put("OrderStatus", orderStatus);
        OrderDetails.put("SpFee", SpFee);
        OrderDetails.put("SpRating", SpRatings);
        OrderDetails.put("SpSubCategory", SpSubCat);
        OrderDetails.put("SpName", SpName);
        OrderDetails.put("SpTiming", ServiceTiming);
        OrderDetails.put("AudioFilePath", filePath);

        HashMap<String, Object> OrderDetailsSP = new HashMap<>();
        OrderDetailsSP.put("CustomerName", SpName);
        OrderDetailsSP.put("PaymentStatus", "Pending");
        OrderDetailsSP.put("QueryText", queryIET.getText().toString());
        OrderDetailsSP.put("Timing", timingACTV.getText().toString());

        StorageReference reference = storageReference.child("audio").child(mAuth.getCurrentUser().getUid());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg")
                .build();

        reference.child(recordQueryBtn.getText().toString()).putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String audioUrl = String.valueOf(uri);
                            OrderDetails.put("AudioUrl", audioUrl);
                            OrderDetailsSP.put("QueryVoiceUrl", audioUrl);
                            OrderDetailsSP.put("OrderNo", orderNumber);

                            databaseReference.child("ServiceProviders").child(SpUID).child("Orders").child("JobsPending").child(orderNumber).setValue(OrderDetailsSP)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                databaseReference.child("Users").child(UserUID).child("UserOrderHistory").child(orderNumber).setValue(OrderDetails)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    //Toast.makeText(RecordingActivity.this, "Order Placed", Toast.LENGTH_SHORT).show();
                                                                    databaseReference.child("OrderNumbers").child(orderNumber).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if(task.isSuccessful()){
                                                                                progressDialog.dismiss();
                                                                                showOrderPlacedDialog();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                        });
                                            }
                                        }
                                    });


                        }
                    });
                }
            }
        });

    }

    private String orderNumbers(){
        String orderNo;
        orderNo = new DecimalFormat("000000000").format(111111111 + new Random().nextInt(999999999));
        return orderNo;
    }


    private void showOrderPlacedDialog() {

        final Dialog orderPlacedDialog = new Dialog(this);
        orderPlacedDialog.setContentView(R.layout.order_placed_dialog);
        orderPlacedDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        orderPlacedDialog.setCancelable(true);

        CardView backToHomeBtn;
        ImageView orderPlacedGif;

        backToHomeBtn = orderPlacedDialog.findViewById(R.id.backToHomeBtn);
        orderPlacedGif = orderPlacedDialog.findViewById(R.id.orderPlacedGif);

        Glide.with(this).load(R.drawable.orderplaced).into(orderPlacedGif);

        backToHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordingActivity.this, HomeActivity.class));
                finish();
            }
        });
        orderPlacedDialog.show();
    }

}