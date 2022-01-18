package com.example.quanlyphuongtien;

import android.Manifest;
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
import androidx.core.app.ActivityCompat;

import com.example.quanlyphuongtien.Activity.Admin.AdminActivity;
import com.example.quanlyphuongtien.Activity.Protector.ProtectorActivity;
import com.example.quanlyphuongtien.Activity.Student.StudentActivity;
import com.example.quanlyphuongtien.Activity.Teacher.TeacherActivity;
import com.example.quanlyphuongtien.Database.AdminDBContext;
import com.example.quanlyphuongtien.Database.ProtectorDBContext;
import com.example.quanlyphuongtien.Database.Remember;
import com.example.quanlyphuongtien.Database.SQLiteHelper;
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
    SQLiteHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.LOCATION_HARDWARE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);

        initView();
        db = new SQLiteHelper(MainActivity.this);
        Remember remember = db.GetRemember();
        loadRemember(remember);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Remember remember = new Remember();
                if (cb_remember.isChecked()) {
                    db.Delete();
                    if (rd_student.isChecked()) {
                        remember = new Remember(edt_username.getText().toString(), edt_password.getText().toString(), 0, 1);
                    }
                    if (rd_teacher.isChecked()) {
                        remember = new Remember(edt_username.getText().toString(), edt_password.getText().toString(), 1, 1);
                    }
                    if (rd_protector.isChecked()) {
                        remember = new Remember(edt_username.getText().toString(), edt_password.getText().toString(), 2, 1);
                    }
                    if (rd_admin.isChecked()) {
                        remember = new Remember(edt_username.getText().toString(), edt_password.getText().toString(), 3, 1);
                    }
                    db.Add(remember);
                } else {
                    db.Delete();
                }
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

    public void loadRemember(Remember remember) {
        if (remember != null) {
            edt_username.setText(remember.getUsername());
            edt_password.setText(remember.getPassword());
            if (remember.getCheck() == 0) {
                rd_student.setChecked(true);
            }
            if (remember.getCheck() == 1) {
                rd_teacher.setChecked(true);
            }
            if (remember.getCheck() == 2) {
                rd_protector.setChecked(true);
            }
            if (remember.getCheck() == 3) {
                rd_admin.setChecked(true);
            }
            cb_remember.setChecked(true);
        }

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
                        if (student.getId().trim().equalsIgnoreCase(edt_username.getText().toString().trim()) && student.getPassword().trim().equalsIgnoreCase(edt_password.getText().toString().trim())) {
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
            TeacherDBContext dbContext = new TeacherDBContext(getApplicationContext());
            dbContext.reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    teacherList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Teacher teacher = dataSnapshot.getValue(Teacher.class);
                        if (teacher.getUsername().trim().equalsIgnoreCase(user.trim()) && teacher.getPassword().trim().equalsIgnoreCase(pass.trim())) {
                            Common.teacher = teacher;
                            dbContext.reference.onDisconnect();
                        }
                    }

                    if (Common.teacher != null) {
                        if (!Common.flagTeacher) {
                            startActivity(new Intent(MainActivity.this, TeacherActivity.class));
                            Common.flagTeacher = true;
                            dialog.dismiss();
                            finish();
                        }

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
                        if (admin.getId().trim().equalsIgnoreCase(edt_username.getText().toString().trim()) && admin.getPassword().trim().equalsIgnoreCase(edt_password.getText().toString().trim())) {
                            check = true;
                        }
                    }
                    if (check) {

                        startActivity(new Intent(MainActivity.this, AdminActivity.class));
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
        if (rd_protector.isChecked()) {
            ProtectorDBContext dbContext = new ProtectorDBContext(MainActivity.this);
            dbContext.reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()
                    ) {
                        Protector protector = dataSnapshot.getValue(Protector.class);
                        if (protector.getUsername().trim().equalsIgnoreCase(edt_username.getText().toString().trim()) && protector.getPassword().trim().equalsIgnoreCase(edt_password.getText().toString().trim())) {
                            Common.protector = protector;
                            dbContext.reference.onDisconnect();
                        }
                    }
                    if (Common.protector != null) {
                        if (!Common.flagProtector) {
                            startActivity(new Intent(MainActivity.this, ProtectorActivity.class));
                            Common.flagProtector = true;
                            dialog.dismiss();
                            finish();
                        }

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