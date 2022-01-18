package com.example.quanlyphuongtien.Activity.Admin.Fragment;

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

import com.example.quanlyphuongtien.Activity.Admin.Adapter.TeacherListAdapter;
import com.example.quanlyphuongtien.Database.TeacherDBContext;
import com.example.quanlyphuongtien.Entities.Teacher;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadListTeacher();
        onClick();
    }

    AutoCompleteTextView edt_search;
    Button btn_add;
    ListView lv_teacher;
    TeacherDBContext db;
    List<Teacher> teacherList;
    TeacherListAdapter adapter;

    private void initView(View view) {
        edt_search = view.findViewById(R.id.edt_search);
        btn_add = view.findViewById(R.id.btn_add);
        lv_teacher = view.findViewById(R.id.lv_teacher);
        db = new TeacherDBContext(getContext());
        teacherList = new ArrayList<>();
    }

    private void onClick() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddTeacherDialog();
            }
        });
        lv_teacher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setUpdateTeacherDialog(position);
            }
        });
    }

    //lấy về danh sách giáo viên trên csdl
    private void loadListTeacher() {
        db.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teacherList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Teacher teacher = dataSnapshot.getValue(Teacher.class);
                    teacherList.add(teacher);
                }
                if(getContext()!=null){
                    adapter = new TeacherListAdapter(getContext(), teacherList);
                    lv_teacher.setAdapter(adapter);
                    edt_search.setAdapter(adapter);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    TextView tv_dateofbirth;

    //set dialog add and update teacher
    private void setAddTeacherDialog() {
        Button btn_update;
        EditText edt_name;
        EditText edt_classname;
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_teacher);
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

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Teacher teacher = new Teacher();
                if (!edt_name.getText().toString().equals("") && !tv_dateofbirth.getText().toString().equals("")) {
                    teacher.setUsername("GV" + ((teacherList.size() + 1000) + 1));
                    teacher.setName(edt_name.getText().toString());
                    teacher.setDateOfBirth(tv_dateofbirth.getText().toString());
                    teacher.setHeadTeacher(edt_classname.getText().toString());
                    teacher.setPassword(teacher.getUsername());
                    db.updateTeacher(teacher);
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

    //set dialog when click info teacher
    private void setUpdateTeacherDialog(int positon) {
        Teacher teacher = teacherList.get(positon);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_update_teacher);
        dialog.show();
        TextView tv_noti_password = dialog.findViewById(R.id.tv_noti_password);
        TextView tv_noti_name = dialog.findViewById(R.id.tv_noti_name);
        TextView tv_noti_date = dialog.findViewById(R.id.tv_noti_dateofbirth);
        TextView tv_id = dialog.findViewById(R.id.tv_id);
        EditText edt_password = dialog.findViewById(R.id.edt_password);
        EditText edt_name = dialog.findViewById(R.id.edt_name);
        tv_dateofbirth = dialog.findViewById(R.id.edt_dateofbirth);
        EditText tv_classname = dialog.findViewById(R.id.tv_classname);
        Button btn_update = dialog.findViewById(R.id.btn_update);
        //bind infor
        tv_id.setText(teacher.getUsername());
        edt_password.setText(teacher.getPassword());
        edt_name.setText(teacher.getName());
        tv_dateofbirth.setText(teacher.getDateOfBirth());
        tv_classname.setText(teacher.getHeadTeacher());
        tv_dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDialogDatePicker();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edt_name.getText().toString().equals("") && !tv_dateofbirth.getText().toString().equals("") && !edt_password.getText().toString().equals("")) {
                    teacher.setName(edt_name.getText().toString());
                    teacher.setDateOfBirth(tv_dateofbirth.getText().toString());
                    teacher.setPassword(edt_password.getText().toString());
                    teacher.setHeadTeacher(tv_classname.getText().toString());
                    db.updateTeacher(teacher);
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
                db.deleteTeacher(teacher.getUsername());
                dialog.dismiss();
            }
        });
    }


}