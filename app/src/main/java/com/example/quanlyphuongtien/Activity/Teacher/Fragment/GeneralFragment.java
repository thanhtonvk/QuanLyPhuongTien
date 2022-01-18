package com.example.quanlyphuongtien.Activity.Teacher.Fragment;

import static com.example.quanlyphuongtien.Activity.Teacher.Fragment.UpdateFragment.studentList;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Teacher.Adapter.StudentListAdapter;
import com.example.quanlyphuongtien.Database.FeeDBContext;
import com.example.quanlyphuongtien.Database.StudentDBContext;
import com.example.quanlyphuongtien.Entities.Check;
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
import java.time.LocalDate;
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
        view.findViewById(R.id.btn_chuanopduphi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadListChuaNopDu();
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

    //kiểm tra nộp tiền đầy đủ
    public boolean checkComplete(String startDate, String endDate) {
        int monthNow = 0, yearNow = 0;
        int monthSchool = 8, yearSchool = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            monthNow = LocalDate.now().getMonthValue();
            yearNow = LocalDate.now().getYear();
        }
        if (monthSchool > monthNow) {
            yearSchool = yearNow - 1;
        } else {
            yearSchool = yearNow;
        }
        String[] start = startDate.split("-");
        String[] end = endDate.split("-");
        boolean check = true;
        if (yearNow == Integer.parseInt(end[2]) && Integer.parseInt(end[1]) < monthNow) {
            check = false;
        }
        if (yearNow > Integer.parseInt(end[2])) {
            check = false;
        }
        if (Integer.parseInt(start[1]) > monthSchool && yearSchool == Integer.parseInt(start[2])) {
            check = false;
        }
        if (Integer.parseInt(start[1]) < monthNow && yearSchool == Integer.parseInt(start[2])) {
            check = false;
        }
        return check;

    }

    private void loadListChuaNopDu() {
        FeeDBContext dbContext = new FeeDBContext(getContext());
        Dialog dialog = new Dialog(getContext());
        dialog.show();
        dialog.setContentView(R.layout.dialog_list_registered);
        TextView tv_mess = dialog.findViewById(R.id.tv_message);
        tv_mess.setText("DANH SÁCH CHƯA ĐÓNG ĐỦ PHÍ GỬI XE");
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
                feeList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Fee fee = dataSnapshot.getValue(Fee.class);
                    if (fee.isConfirm()) {
                        if (!checkComplete(fee.getStartDate(), fee.getEndDate())) {
                            if (getStudent(fee.getIdSV()).getClassName() != null) {
                                if (getStudent(fee.getIdSV()).getClassName().equals(Common.teacher.getHeadTeacher())) {
                                    studentList.add(getStudent(fee.getIdSV()));
                                    feeList.add(fee);
                                }
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
        lv_student.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setDialogMonth(position);
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
//                    if (fee.isConfirm()) {
//                        if (!checkComplete(fee.getStartDate(), fee.getEndDate())) {
//                            if (getStudent(fee.getIdSV()).getClassName() != null) {
//                                if (getStudent(fee.getIdSV()).getClassName().equals(Common.teacher.getHeadTeacher())) {
//                                    studentList.add(getStudent(fee.getIdSV()));
//                                    feeList.add(fee);
//                                }
//                            }
//                        }
//                    }
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
                feeList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Fee fee = dataSnapshot.getValue(Fee.class);
                    if (fee.isConfirm()) {
//                        if (checkComplete(fee.getStartDate(), fee.getEndDate())) {
                        if (getStudent(fee.getIdSV()).getClassName() != null) {
                            if (getStudent(fee.getIdSV()).getClassName().equals(Common.teacher.getHeadTeacher())) {
                                studentList.add(getStudent(fee.getIdSV()));
                                feeList.add(fee);
                            }
                        }
//
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
        lv_student.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setDialogMonth(position);
            }
        });
    }

    List<Fee> feeList;
    CheckBox cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9, cb10, cb11, cb12;
    Button btn_chuadongdu;

    private void setDialogMonth(int position) {
        setCheck();
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_showmonth);
        dialog.show();
        cb1 = dialog.findViewById(R.id.cb_1);
        cb2 = dialog.findViewById(R.id.cb_2);
        cb3 = dialog.findViewById(R.id.cb_3);
        cb4 = dialog.findViewById(R.id.cb_4);
        cb5 = dialog.findViewById(R.id.cb_5);
        cb6 = dialog.findViewById(R.id.cb_6);
        cb7 = dialog.findViewById(R.id.cb_7);
        cb8 = dialog.findViewById(R.id.cb_8);
        cb9 = dialog.findViewById(R.id.cb_9);
        cb10 = dialog.findViewById(R.id.cb_10);
        cb11 = dialog.findViewById(R.id.cb_11);
        cb12 = dialog.findViewById(R.id.cb_12);
        Fee fee = feeList.get(position);
        String startDate = fee.getStartDate();
        String endDate = fee.getEndDate();
        if (endDate.equals("")) {
            cb1.setChecked(false);
            cb2.setChecked(false);
            cb3.setChecked(false);
            cb4.setChecked(false);
            cb5.setChecked(false);
            cb6.setChecked(false);
            cb7.setChecked(false);
            cb8.setChecked(false);
            cb9.setChecked(false);
            cb10.setChecked(false);
            cb11.setChecked(false);
            cb12.setChecked(false);
        } else {
            int monthNow = 0, yearNow = 0;
            int monthSchool = 8, yearSchool = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                monthNow = LocalDate.now().getMonthValue();
                yearNow = LocalDate.now().getYear();
            }
            if (monthSchool > monthNow) {
                yearSchool = yearNow - 1;
            } else {
                yearSchool = yearNow;
            }
            String[] start = startDate.split("-");
            String[] end = endDate.split("-");
            int monthStart = Integer.parseInt(start[1]);
            int monthEnd = Integer.parseInt(end[1]);
            if ((monthEnd >= 8 && yearNow == yearSchool) || (monthEnd < 8 && yearNow - 1 == yearSchool)) {
                if (monthStart < monthEnd) {
                    for (int i = monthStart - 1; i < monthEnd; i++) {
                        checkList.get(i).setKt(true);
                    }
                } else if (monthStart > monthEnd) {
                    for (int i = monthStart - 1; i < 12; i++) {
                        checkList.get(i).setKt(true);
                    }
                    for (int i = 0; i < monthEnd; i++) {
                        checkList.get(i).setKt(true);
                    }
                }
            }


            if (checkList.get(0).isKt()) {
                cb1.setChecked(true);
            }
            if (checkList.get(1).isKt()) {
                cb2.setChecked(true);
            }
            if (checkList.get(2).isKt()) {
                cb3.setChecked(true);
            }
            if (checkList.get(3).isKt()) {
                cb4.setChecked(true);
            }
            if (checkList.get(4).isKt()) {
                cb5.setChecked(true);
            }
            if (checkList.get(5).isKt()) {
                cb6.setChecked(true);
            }
            if (checkList.get(6).isKt()) {
                cb7.setChecked(true);
            }
            if (checkList.get(7).isKt()) {
                cb8.setChecked(true);
            }
            if (checkList.get(8).isKt()) {
                cb9.setChecked(true);
            }
            if (checkList.get(9).isKt()) {
                cb10.setChecked(true);
            }
            if (checkList.get(10).isKt()) {
                cb11.setChecked(true);
            }
            if (checkList.get(11).isKt()) {
                cb12.setChecked(true);
            }

        }


    }

    List<Check> checkList;

    private void setCheck() {
        checkList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            checkList.add(new Check(false));
        }
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