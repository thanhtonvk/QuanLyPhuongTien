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

import com.example.quanlyphuongtien.Activity.Admin.Adapter.ProtectorListAdapter;
import com.example.quanlyphuongtien.Database.ProtectorDBContext;
import com.example.quanlyphuongtien.Entities.Protector;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProtectorFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_protector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadListProtector();
        onClick();
    }

    AutoCompleteTextView edt_search;
    Button btn_add;
    ListView lv_protector;
    ProtectorDBContext db;
    List<Protector> protectorList;
    ProtectorListAdapter adapter;

    private void initView(View view) {
        edt_search = view.findViewById(R.id.edt_search);
        btn_add = view.findViewById(R.id.btn_add);
        lv_protector = view.findViewById(R.id.lv_protector);
        db = new ProtectorDBContext(getContext());
        protectorList = new ArrayList<>();
    }

    private void onClick() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAddProtectorDialog();
            }
        });
        lv_protector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setUpdateProtectorDialog(position);
            }
        });
    }

    private void loadListProtector() {
        db.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                protectorList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Protector protector = dataSnapshot.getValue(Protector.class);
                    protectorList.add(protector);
                }
                adapter = new ProtectorListAdapter(getContext(), protectorList);
                lv_protector.setAdapter(adapter);
                edt_search.setAdapter(adapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    TextView tv_dateofbirth;

    //set dialog add and update protector
    private void setAddProtectorDialog() {
        Button btn_update;
        EditText edt_name;
        EditText edt_classname;
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_protector);
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
                int yearNow = 0, monthNow = 0, dayNow = 0;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    yearNow = java.time.LocalDate.now().getYear();
                    monthNow = java.time.LocalDate.now().getMonthValue();
                    dayNow = java.time.LocalDate.now().getDayOfMonth();
                }
                Protector protector = new Protector();
                if (!edt_name.getText().toString().equals("") && !tv_dateofbirth.getText().toString().equals("")) {
                    protector.setUsername("BV" + ((protectorList.size() + 2000) + 1));
                    protector.setName(edt_name.getText().toString());
                    protector.setDateOfBirth(tv_dateofbirth.getText().toString());
                    protector.setPosition(edt_classname.getText().toString());
                    protector.setPassword(protector.getUsername());
                    //set fee
                    db.updateProtector(protector);
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

    //set dialog when click info protector
    private void setUpdateProtectorDialog(int positon) {
        Protector protector = protectorList.get(positon);
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_update_protector);
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
        tv_id.setText(protector.getUsername());
        edt_password.setText(protector.getPassword());
        edt_name.setText(protector.getName());
        tv_dateofbirth.setText(protector.getDateOfBirth());
        tv_classname.setText(protector.getPosition());
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
                    protector.setName(edt_name.getText().toString());
                    protector.setDateOfBirth(tv_dateofbirth.getText().toString());
                    protector.setPassword(edt_password.getText().toString());
                    protector.setPosition(tv_classname.getText().toString());
                    db.updateProtector(protector);
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
                db.deleteProtector(protector.getUsername());
                dialog.dismiss();
            }
        });
    }


}