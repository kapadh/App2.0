package com.example.projectkapadh.DbQueries;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class PushDataToDatabaseQueries {


    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    ////////////////////////////////////////////////////////////////////////////////////////////////


}
