package com.example.quanlyphuongtien.Database;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminDBContext {
    public FirebaseDatabase database;
    public DatabaseReference reference;
    Context context;

    public AdminDBContext(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Admin");
    }


}
