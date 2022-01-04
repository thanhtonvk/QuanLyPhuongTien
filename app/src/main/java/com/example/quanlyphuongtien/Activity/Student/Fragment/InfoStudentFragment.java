package com.example.quanlyphuongtien.Activity.Student.Fragment;

import static com.example.quanlyphuongtien.Entities.Common.student;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Database.StudentDBContext;
import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.MainActivity;
import com.example.quanlyphuongtien.R;

public class InfoStudentFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_info_student, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadInfo();
        onClick();
        if (student.getVehicleCategory() == null || student.getNumberPlate() == null) {
            setUpdateStudentDialog();
        }
    }

    TextView tv_name, tv_dateofbirth, tv_idgv, tv_classname;
    Button btn_update, btn_logout;
    StudentDBContext db;

    private void initView(View view) {
        tv_name = view.findViewById(R.id.tv_name);
        tv_dateofbirth = view.findViewById(R.id.tv_dateofbirth);
        tv_idgv = view.findViewById(R.id.tv_id);
        btn_update = view.findViewById(R.id.btn_update);
        btn_logout = view.findViewById(R.id.btn_logout);
        tv_classname = view.findViewById(R.id.tv_classname);
        db = new StudentDBContext(getContext());
    }

    private void loadInfo() {
        if (student != null) {
            tv_name.setText("Họ và tên: " + student.getName());
            tv_dateofbirth.setText("Ngày sinh: " + student.getDateOfBirth());
            tv_idgv.setText("Mã học sinh: " + student.getId());
            tv_classname.setText("Lớp: " + student.getClassName());
        }
    }

    private void onClick() {
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUpdateStudentDialog();
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                student = null;
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });
    }

    //set dialog when click info student
    private void setUpdateStudentDialog() {
        Student student = Common.student;
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_info_student);
        dialog.show();
        TextView tv_noti_password = dialog.findViewById(R.id.tv_noti_password);
        TextView tv_noti_name = dialog.findViewById(R.id.tv_noti_name);
        TextView tv_noti_date = dialog.findViewById(R.id.tv_noti_dateofbirth);
        TextView tv_id = dialog.findViewById(R.id.tv_id);
        TextView tv_noti_classname = dialog.findViewById(R.id.tv_noti_classname);
        TextView tv_noti_plate = dialog.findViewById(R.id.tv_noti_numplate);
        TextView tv_noti_vehicle = dialog.findViewById(R.id.tv_noti_vehicle);
        tv_noti_classname.setText("Lớp");
        EditText edt_password = dialog.findViewById(R.id.edt_password);
        EditText edt_name = dialog.findViewById(R.id.edt_name);
        tv_dateofbirth = dialog.findViewById(R.id.edt_dateofbirth);
        EditText tv_classname = dialog.findViewById(R.id.tv_classname);
        tv_classname.setEnabled(false);
        Button btn_update = dialog.findViewById(R.id.btn_update);
        EditText edt_numberplate = dialog.findViewById(R.id.edt_numplate);
        EditText edt_vehicle = dialog.findViewById(R.id.edt_vehicle);
        //bind infor
        edt_numberplate.setText(student.getId());
        tv_id.setText(student.getId());
        edt_password.setText(student.getPassword());
        edt_name.setText(student.getName());
        tv_dateofbirth.setText(student.getDateOfBirth());
        tv_classname.setText(student.getClassName());
        tv_dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialogDatePicker();
            }
        });
        if (student.getNumberPlate() != null) {
            edt_numberplate.setText(student.getNumberPlate());
        }
        if (student.getVehicleCategory() != null) {
            edt_vehicle.setText(student.getVehicleCategory());
        }

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_name.getText().toString().equals("") && !tv_dateofbirth.getText().toString().equals("") && !edt_password.getText().toString().equals("")
                        && !edt_vehicle.getText().toString().equals("") && !edt_numberplate.getText().toString().equals("")) {
                    student.setName(edt_name.getText().toString());
                    student.setDateOfBirth(tv_dateofbirth.getText().toString());
                    student.setNumberPlate(edt_numberplate.getText().toString());
                    student.setVehicleCategory(edt_vehicle.getText().toString());
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
                    if (edt_numberplate.getText().toString().equals("")) {
                        tv_noti_plate.setText("Biển số không được để trống");
                        tv_noti_plate.setTextColor(Color.RED);
                    }
                    if (edt_vehicle.getText().toString().equals("")) {
                        tv_noti_vehicle.setText("Loại xe không được để trống");
                        tv_noti_vehicle.setTextColor(Color.RED);
                    }
                }

            }
        });
        Button btn_delete = dialog.findViewById(R.id.btn_delete);
        btn_delete.setVisibility(View.GONE);
        edt_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialog.dismiss();
                changePassword();
                return false;
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
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_NoActionBar, dateSetListener, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    //set dialog changes password
    private void changePassword() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_change_password);
        dialog.show();
        EditText edt_old = dialog.findViewById(R.id.edt_oldpassword);
        EditText edt_new = dialog.findViewById(R.id.edt_newpassword);
        EditText edt_rep = dialog.findViewById(R.id.edt_replypassword);
        TextView tv_old = dialog.findViewById(R.id.tv_noti_old);
        TextView tv_new = dialog.findViewById(R.id.tv_noti_new);
        TextView tv_rep = dialog.findViewById(R.id.tv_noti_rep);
        Button btn_update = dialog.findViewById(R.id.btn_update);
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_old.getText().toString().equals(student.getPassword())) {
                    if (tv_new.getText().length() < 6) {
                        tv_new.setText("Mật khẩu phải có độ dài lớn hơn 6");
                    } else {
                        if (!edt_rep.getText().toString().equals(edt_new.getText().toString())) {
                            tv_rep.setText("Mật khẩu không khớp");
                        } else {
                            student.setPassword(edt_old.getText().toString());
                            db.updateStudent(student);
                        }
                    }
                } else {
                    tv_old.setText("Mật khẩu không chính xác");
                }
            }
        });

    }
}