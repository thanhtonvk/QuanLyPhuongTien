package com.example.quanlyphuongtien.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quanlyphuongtien.Entities.Admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
