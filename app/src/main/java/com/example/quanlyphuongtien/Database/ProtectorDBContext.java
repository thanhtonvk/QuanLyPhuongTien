package com.example.quanlyphuongtien.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quanlyphuongtien.Entities.Protector;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProtectorDBContext {
    public FirebaseDatabase database;
    public DatabaseReference reference;
    Context context;

    public ProtectorDBContext(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Protector");
    }

    public void updateProtector(Protector Protector) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Vui lòng chờ...");
        dialog.show();
        reference.child(Protector.getUsername()).setValue(Protector).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Lỗi, kiểm tra lại", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }

    public void deleteProtector(String id) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Vui lòng chờ...");
        dialog.show();
        reference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Lỗi, kiểm tra lại", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
    }
}
