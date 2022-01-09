package com.example.quanlyphuongtien.Activity.Teacher.Fragment;

import static com.example.quanlyphuongtien.Activity.Teacher.Fragment.UpdateFragment.studentList;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Teacher.Adapter.StudentListAdapter;
import com.example.quanlyphuongtien.Database.FeeDBContext;
import com.example.quanlyphuongtien.Database.StudentDBContext;
import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.Entities.Fee;
import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeneralFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_genaral, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn_dsdangky).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadListRegistered();
            }
        });
        view.findViewById(R.id.btn_chuanopphi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadListNonPay();
            }
        });
        view.findViewById(R.id.btn_danopphi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadListPaid();
            }
        });
    }

    private void loadListRegistered() {
        StudentDBContext dbContext = new StudentDBContext(getContext());
        Dialog dialog = new Dialog(getContext());
        dialog.show();
        dialog.setContentView(R.layout.dialog_list_registered);
        TextView tv_mess = dialog.findViewById(R.id.tv_message);
        tv_mess.setText("DANH SÁCH ĐÃ ĐĂNG KÝ GỬI XE");
        List<Student> studentList = new ArrayList<>();
        ListView lv_student = dialog.findViewById(R.id.lv_student);
        Button btn_report = dialog.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteFile(studentList);
                sendFile();
            }
        });
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if (student.getClassName().equals(Common.teacher.getHeadTeacher())) {
                        studentList.add(student);
                    }

                }
                if (getContext() != null) {
                    StudentListAdapter listAdapter = new StudentListAdapter(getContext(), studentList);
                    lv_student.setAdapter(listAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void sendFile() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File link = getContext().getExternalFilesDir("").getParentFile();
        File file = new File(link, "report.pdf");
        if (file.exists()) {
            Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
            if (path != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/csv");
                intent.putExtra(Intent.EXTRA_STREAM, path);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(intent, "Send email..."));
            }

        }

    }

    public void WriteFile(List<Student> studentList) {
        File link = getContext().getExternalFilesDir("").getParentFile();
        File file = new File(link, "report.pdf");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("STT,Mã học sinh,Họ và tên,Ngày,Lớp\n");
            int index = 1;
            for (Student student : studentList) {
                String text = index + "," + student.getId() + "," + student.getName() + "," + student.getDateOfBirth() + "," + student.getClassName() + "\n";
                fileWriter.write(text);
                index++;
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadListNonPay() {
        FeeDBContext dbContext = new FeeDBContext(getContext());
        Dialog dialog = new Dialog(getContext());
        dialog.show();
        dialog.setContentView(R.layout.dialog_list_registered);
        TextView tv_mess = dialog.findViewById(R.id.tv_message);
        tv_mess.setText("DANH SÁCH CHƯA ĐÓNG PHÍ GỬI XE");
        List<Student> studentList = new ArrayList<>();
        ListView lv_student = dialog.findViewById(R.id.lv_student);
        Button btn_report = dialog.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteFile(studentList);
                sendFile();
            }
        });
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Fee fee = dataSnapshot.getValue(Fee.class);
                    if (!fee.isConfirm()) {
                        if (getStudent(fee.getIdSV()).getClassName() != null) {
                            if (getStudent(fee.getIdSV()).getClassName().equals(Common.teacher.getHeadTeacher())) {
                                studentList.add(getStudent(fee.getIdSV()));
                            }
                        }

                    }
                }
                if (getContext() != null) {
                    StudentListAdapter listAdapter = new StudentListAdapter(getContext(), studentList);
                    lv_student.setAdapter(listAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadListPaid() {
        FeeDBContext dbContext = new FeeDBContext(getContext());
        Dialog dialog = new Dialog(getContext());
        dialog.show();
        dialog.setContentView(R.layout.dialog_list_registered);
        TextView tv_mess = dialog.findViewById(R.id.tv_message);
        tv_mess.setText("DANH SÁCH ĐÃ ĐÓNG PHÍ GỬI XE");
        List<Student> studentList = new ArrayList<>();
        ListView lv_student = dialog.findViewById(R.id.lv_student);
        Button btn_report = dialog.findViewById(R.id.btn_report);
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteFile(studentList);
                sendFile();
            }
        });
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Fee fee = dataSnapshot.getValue(Fee.class);
                    if (fee.isConfirm()) {
                        if (getStudent(fee.getIdSV()).getClassName() != null) {
                            if (getStudent(fee.getIdSV()).getClassName().equals(Common.teacher.getHeadTeacher())) {
                                studentList.add(getStudent(fee.getIdSV()));
                            }
                        }
                    }
                }
                if (getContext() != null) {
                    StudentListAdapter listAdapter = new StudentListAdapter(getContext(), studentList);
                    lv_student.setAdapter(listAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private Student getStudent(String ID) {
        Student rs = new Student();
        for (Student student : studentList
        ) {
            if (student.getId().equals(ID)) rs = student;
        }
        return rs;
    }
}