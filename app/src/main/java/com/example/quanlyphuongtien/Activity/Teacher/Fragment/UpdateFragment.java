package com.example.quanlyphuongtien.Activity.Teacher.Fragment;

import static com.example.quanlyphuongtien.Entities.Common.teacher;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Random;

public class UpdateFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadDataStudent();
        onClick();
    }

    AutoCompleteTextView edt_search;
    Button btn_add;
    TextView tv_nameteacher, tv_sumstudent;
    ListView lv_student;
    StudentListAdapter listAdapter;
    public static List<Student> studentList;
    StudentDBContext db;
    FeeDBContext feeDBContext;
    TextView tv_count;

    private void initView(View view) {
        edt_search = view.findViewById(R.id.edt_search);
        btn_add = view.findViewById(R.id.btn_add);
        tv_nameteacher = view.findViewById(R.id.tv_name);
        tv_sumstudent = view.findViewById(R.id.tv_sumstudent);
        lv_student = view.findViewById(R.id.lv_student);
        db = new StudentDBContext(getContext());
        feeDBContext = new FeeDBContext(getContext());
        tv_count = view.findViewById(R.id.tv_sumstudent);

    }

    //set event when click
    private void onClick() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAddStudentDialog();
            }
        });
        lv_student.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setUpdateStudentDialog(i);
            }
        });
    }

    //load list student from firebase
    private void loadDataStudent() {
        tv_nameteacher.setText("GVCN " + teacher.getName());
        db.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Student student = dataSnapshot.getValue(Student.class);
                    if(student.getClassName().equals(teacher.getHeadTeacher())){
                        studentList.add(student);
                    }

                }
                listAdapter = new StudentListAdapter(getContext(), studentList);
                lv_student.setAdapter(listAdapter);
                edt_search.setAdapter(listAdapter);
                tv_count.setText("Tổng số: " + studentList.size() + " học sinh");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    TextView tv_dateofbirth;
    Random random = new Random();

    //set dialog add and update student
    private void setAddStudentDialog() {
        Button btn_update;
        EditText edt_name;
        TextView edt_classname;
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_student);
        dialog.show();
        TextView tv_noti_name = dialog.findViewById(R.id.tv_noti_name);
        TextView tv_noti_date = dialog.findViewById(R.id.tv_noti_dateofbirth);
        tv_dateofbirth = dialog.findViewById(R.id.edt_dateofbirth);
        edt_name = dialog.findViewById(R.id.edt_name);
        edt_classname = dialog.findViewById(R.id.tv_classname);
        btn_update = dialog.findViewById(R.id.btn_update);

        tv_dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialogDatePicker();
            }
        });
        edt_classname.setText(teacher.getHeadTeacher());
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yearNow = 0, monthNow = 0, dayNow = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    yearNow = java.time.LocalDate.now().getYear();
                    monthNow = java.time.LocalDate.now().getMonthValue();
                    dayNow = java.time.LocalDate.now().getDayOfMonth();
                }
                Student student = new Student();
                Fee fee = new Fee();
                if (!edt_name.getText().toString().equals("") && !tv_dateofbirth.getText().toString().equals("")) {

                    student.setId(edt_classname.getText().toString() + ((studentList.size() + 2000) + 1));
                    student.setName(edt_name.getText().toString());
                    student.setDateOfBirth(tv_dateofbirth.getText().toString());
                    student.setClassName(edt_classname.getText().toString());
                    student.setPassword(student.getId());
                    //set fee
                    fee.setId("F" + random.nextInt());
                    fee.setIdSV(student.getId());
                    fee.setName(student.getName());
                    fee.setConfirm(false);
                    fee.setMoney(0);
                    fee.setStartDate(dayNow + "-" + monthNow + "-" + yearNow);
                    fee.setEndDate("");
                    feeDBContext.updateFee(fee);
                    db.updateStudent(student);
                    dialog.dismiss();
                } else {
                    if (edt_name.getText().toString().equals("")) {
                        tv_noti_name.setText("Tên không được để trống");
                        tv_noti_name.setTextColor(Color.RED);
                    }
                    if (tv_dateofbirth.getText().toString().equals("")) {
                        tv_noti_date.setText("Ngày sinh không được để trống");
                        tv_noti_date.setTextColor(Color.RED);
                    }

                }

            }
        });

    }


    //set dialog when click info student
    private void setUpdateStudentDialog(int positon) {
        Student student = studentList.get(positon);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_update_student);
        dialog.show();
        TextView tv_noti_password = dialog.findViewById(R.id.tv_noti_password);
        TextView tv_noti_name = dialog.findViewById(R.id.tv_noti_name);
        TextView tv_noti_date = dialog.findViewById(R.id.tv_noti_dateofbirth);
        TextView tv_id = dialog.findViewById(R.id.tv_id);
        EditText edt_password = dialog.findViewById(R.id.edt_password);
        EditText edt_name = dialog.findViewById(R.id.edt_name);
        tv_dateofbirth = dialog.findViewById(R.id.edt_dateofbirth);
        TextView tv_classname = dialog.findViewById(R.id.tv_classname);
        TextView tv_numberplate = dialog.findViewById(R.id.tv_numberplate);
        TextView tv_vehicle = dialog.findViewById(R.id.tv_vehiclecategory);
        Button btn_update = dialog.findViewById(R.id.btn_update);
        Button btn_addfee = dialog.findViewById(R.id.btn_addfee);
        //bind infor
        tv_id.setText(student.getId());
        edt_password.setText(student.getPassword());
        edt_name.setText(student.getName());
        tv_dateofbirth.setText(student.getDateOfBirth());
        tv_classname.setText(student.getClassName());
        if (student.getNumberPlate() != null) {
            tv_numberplate.setText(student.getNumberPlate());
        }
        if (student.getVehicleCategory() != null) {
            tv_vehicle.setText(student.getVehicleCategory());
        }
        tv_dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialogDatePicker();
            }
        });

        btn_addfee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int yearNow = 0, monthNow = 0, dayNow = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    yearNow = java.time.LocalDate.now().getYear();
                    monthNow = java.time.LocalDate.now().getMonthValue();
                    dayNow = java.time.LocalDate.now().getDayOfMonth();
                }
                Fee fee = new Fee();
                //set fee
                fee.setId("F" + random.nextInt());
                fee.setIdSV(student.getId());
                fee.setName(student.getName());
                fee.setConfirm(false);
                fee.setMoney(0);
                fee.setStartDate(dayNow + "-" + monthNow + "-" + yearNow);
                fee.setEndDate("");
                feeDBContext.updateFee(fee);
                dialog.dismiss();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_name.getText().toString().equals("") && !tv_dateofbirth.getText().toString().equals("") && !edt_password.getText().toString().equals("")) {
                    student.setName(edt_name.getText().toString());
                    student.setDateOfBirth(tv_dateofbirth.getText().toString());
                    student.setPassword(edt_password.getText().toString());
                    db.updateStudent(student);
                    dialog.dismiss();
                } else {
                    if (edt_name.getText().toString().equals("")) {
                        tv_noti_name.setText("Tên không được để trống");
                        tv_noti_name.setTextColor(Color.RED);
                    }
                    if (tv_dateofbirth.getText().toString().equals("")) {
                        tv_noti_date.setText("Ngày sinh không được để trống");
                        tv_noti_date.setTextColor(Color.RED);
                    }
                    if (edt_password.getText().toString().equals("")) {
                        tv_noti_password.setText("Mật khẩu không được để trống");
                        tv_noti_password.setTextColor(Color.RED);
                    }
                }

            }
        });
        Button btn_delete = dialog.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteStudent(student.getId());
                dialog.dismiss();
            }
        });
    }

    //set datepicker dialog
    private void setDialogDatePicker() {
        int selectedYear = 0;
        int selectedMonth = 0;
        int selectedDay = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            selectedYear = java.time.LocalDate.now().getYear();
            selectedMonth = java.time.LocalDate.now().getMonthValue();
            selectedDay = java.time.LocalDate.now().getDayOfMonth();
        }
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                tv_dateofbirth.setText(String.format("%s/%s/%s", i2, i1 + 1, i));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }
}