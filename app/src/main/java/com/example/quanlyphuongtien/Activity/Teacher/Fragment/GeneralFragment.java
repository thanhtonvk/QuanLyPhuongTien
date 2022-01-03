package com.example.quanlyphuongtien.Activity.Teacher.Fragment;

import static com.example.quanlyphuongtien.Activity.Teacher.Fragment.UpdateFragment.studentList;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Teacher.Adapter.StudentListAdapter;
import com.example.quanlyphuongtien.Database.FeeDBContext;
import com.example.quanlyphuongtien.Database.StudentDBContext;
import com.example.quanlyphuongtien.Entities.Fee;
import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Student student = dataSnapshot.getValue(Student.class);
                    studentList.add(student);
                }
                StudentListAdapter listAdapter = new StudentListAdapter(getContext(), studentList);
                lv_student.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Fee fee = dataSnapshot.getValue(Fee.class);
                    if(!fee.isConfirm()){
                        studentList.add(getStudent(fee.getIdSV()));
                    }


                }
                StudentListAdapter listAdapter = new StudentListAdapter(getContext(), studentList);
                lv_student.setAdapter(listAdapter);
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
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Fee fee = dataSnapshot.getValue(Fee.class);
                    if(fee.isConfirm()){
                        studentList.add(getStudent(fee.getIdSV()));
                    }


                }
                StudentListAdapter listAdapter = new StudentListAdapter(getContext(), studentList);
                lv_student.setAdapter(listAdapter);
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