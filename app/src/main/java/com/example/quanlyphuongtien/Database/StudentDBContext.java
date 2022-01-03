package com.example.quanlyphuongtien.Database;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.quanlyphuongtien.Entities.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StudentDBContext {
    public FirebaseDatabase database;
    public DatabaseReference reference;
    Context context;

    public StudentDBContext(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Student");
    }

    public void updateStudent(Student student) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Vui lòng chờ...");
        dialog.show();
        reference.child(student.getId()).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    public void deleteStudent(String id) {
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
