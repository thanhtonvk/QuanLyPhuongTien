package com.example.quanlyphuongtien;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlyphuongtien.Activity.Admin.AdminActivity;
import com.example.quanlyphuongtien.Activity.Protector.ProtectorActivity;
import com.example.quanlyphuongtien.Activity.Student.StudentActivity;
import com.example.quanlyphuongtien.Activity.Teacher.TeacherActivity;
import com.example.quanlyphuongtien.Database.AdminDBContext;
import com.example.quanlyphuongtien.Database.ProtectorDBContext;
import com.example.quanlyphuongtien.Database.StudentDBContext;
import com.example.quanlyphuongtien.Database.TeacherDBContext;
import com.example.quanlyphuongtien.Entities.Admin;
import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.Entities.Protector;
import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.Entities.Teacher;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    EditText edt_username, edt_password;
    RadioButton rd_student, rd_teacher, rd_protector, rd_admin;
    Button btn_login;
    CheckBox cb_remember;
    Button btn_show;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edt_password.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);


            }
        });
    }


    public void initView() {
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        rd_student = findViewById(R.id.rd_student);
        rd_teacher = findViewById(R.id.rd_teacher);
        rd_protector = findViewById(R.id.rd_protector);
        rd_admin = findViewById(R.id.rd_admin);
        btn_login = findViewById(R.id.btn_login);
        cb_remember = findViewById(R.id.cb_remember);
        btn_show = findViewById(R.id.btn_show_password);
    }

    List<Teacher> teacherList;

    public void Login() {
        String user = edt_username.getText().toString();
        String pass = edt_password.getText().toString();
        Log.d("user", user);
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Đang đăng nhập");
        dialog.show();
        if (rd_student.isChecked()) {
            StudentDBContext dbContext = new StudentDBContext(MainActivity.this);
            dbContext.reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Student student = dataSnapshot.getValue(Student.class);
                        if (student.getId().equals(edt_username.getText().toString()) && student.getPassword().equals(edt_password.getText().toString())) {
                            Common.student = student;
                        }
                    }
                    if (Common.student != null) {
                        startActivity(new Intent(getApplicationContext(), StudentActivity.class));
                        dialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (rd_teacher.isChecked()) {
            TeacherDBContext dbContext = new TeacherDBContext(MainActivity.this);
            dbContext.reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    teacherList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        if (teacher.getUsername().equals(user) && teacher.getPassword().equals(pass)) {
                            Common.teacher = teacher;
                        }
                    }

                    if (Common.teacher != null) {
                        startActivity(new Intent(MainActivity.this, TeacherActivity.class));
                        dialog.dismiss();
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        if (rd_admin.isChecked()) {
            AdminDBContext dbContext = new AdminDBContext(MainActivity.this);
            dbContext.reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean check = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Admin admin = dataSnapshot.getValue(Admin.class);
                        if (admin.getId().equals(edt_username.getText().toString()) && admin.getPassword().equals(edt_password.getText().toString())) {
                            check = true;
                        }
                    }
                    if (check) {
                        dialog.dismiss();
                        startActivity(new Intent(MainActivity.this, AdminActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        if (rd_protector.isChecked()) {
            ProtectorDBContext dbContext = new ProtectorDBContext(MainActivity.this);
            dbContext.reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Protector protector = dataSnapshot.getValue(Protector.class);
                        if (protector.getUsername().equals(edt_username.getText().toString()) && protector.getPassword().equals(edt_password.getText().toString())) {
                            Common.protector = protector;
                        }
                    }
                    if (Common.protector != null) {
                        dialog.dismiss();
                        startActivity(new Intent(MainActivity.this, ProtectorActivity.class));
                    } else {
                        Toast.makeText(MainActivity.this, "Tài khoản hoặc mật khẩu không chính xác", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}