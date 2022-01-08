package com.example.quanlyphuongtien.Activity.Teacher.Fragment;

import static com.example.quanlyphuongtien.Activity.Teacher.Fragment.UpdateFragment.studentList;
import static com.example.quanlyphuongtien.Entities.Common.teacher;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Teacher.Adapter.FeeListAdapter;
import com.example.quanlyphuongtien.Database.FeeDBContext;
import com.example.quanlyphuongtien.Entities.Fee;
import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConfirmFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm, container, false);
    }


    FeeDBContext db;
    AutoCompleteTextView edt_search;
    ListView lv_fee;
    FeeListAdapter adapter;
    List<Fee> feeList;
    TextView tv_sum;
    TextView tv_nameteacher;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadListFee();
        onClick();
    }

    private void onClick() {
        lv_fee.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setDialogFeeInfo(i);
            }
        });
    }

    private void initView(View view) {
        db = new FeeDBContext(getContext());
        edt_search = view.findViewById(R.id.edt_search);
        lv_fee = view.findViewById(R.id.lv_fee);
        feeList = new ArrayList<>();
        tv_sum = view.findViewById(R.id.tv_sumstudent);
        tv_nameteacher = view.findViewById(R.id.tv_name);
    }

    //Lấy danh sách nộp phí
    private void loadListFee() {
        tv_nameteacher.setText("GVCN " + teacher.getName());
        db.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feeList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {

                    Fee fee = dataSnapshot.getValue(Fee.class);
                    if (getStudent(fee.getIdSV()).getClassName() != null) {
                        if (getStudent(fee.getIdSV()).getClassName().equals(teacher.getHeadTeacher())) {
                            feeList.add(fee);
                        }

                    }

                }
                if (getContext() != null) {
                    adapter = new FeeListAdapter(getContext(), feeList);
                    lv_fee.setAdapter(adapter);
                    tv_sum.setText("Danh sách nộp: " + feeList.size());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ArrayAdapter<String> sp_adapter;

    //set dialog info
    private void setDialogFeeInfo(int postion) {
        String month[] = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
        sp_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, month);
        Fee fee = feeList.get(postion);
        Student student = getStudent(fee.getIdSV());
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_info_fee);
        dialog.show();
        TextView tv_id, tv_name, tv_vehicle, tv_startDate, tv_endDate;
        EditText tv_Money;
        Spinner sp_month = dialog.findViewById(R.id.sp_month);
        tv_id = dialog.findViewById(R.id.tv_id);
        tv_name = dialog.findViewById(R.id.tv_name);
        tv_vehicle = dialog.findViewById(R.id.tv_vehicle);
        tv_startDate = dialog.findViewById(R.id.tv_startdate);
        tv_endDate = dialog.findViewById(R.id.tv_enddate);
        tv_Money = dialog.findViewById(R.id.tv_money);
        Button btn_confirm = dialog.findViewById(R.id.btn_update);
        Button btn_delete = dialog.findViewById(R.id.btn_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteFee(fee.getId());
                dialog.dismiss();
            }
        });
        sp_month.setAdapter(sp_adapter);
        if (fee.isConfirm()) {
            btn_confirm.setVisibility(View.GONE);
            tv_id.setText(fee.getIdSV());
            tv_name.setText(fee.getName());
            tv_vehicle.setText(student.getVehicleCategory());
            tv_startDate.setText(fee.getStartDate());
            tv_endDate.setText(fee.getEndDate());
            tv_Money.setText(fee.getMoney() + "");
            sp_month.setEnabled(false);
            if (fee.getMoney() / 15000 == 1) {
                sp_month.setTop(0);
            } else if (fee.getMoney() / 15000 == 3) {
                sp_month.setTop(1);
            } else if (fee.getMoney() / 15000 == 6) {
                sp_month.setTop(2);
            } else {
                sp_month.setTop(3);
            }
        } else {
            tv_id.setText(fee.getIdSV());
            tv_name.setText(fee.getName());
            tv_vehicle.setText(student.getVehicleCategory());
            tv_startDate.setText(fee.getStartDate());
            sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    setDate(month[i], tv_Money);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            tv_startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialogDatePicker(tv_startDate);
                }
            });
            tv_endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialogDatePicker(tv_endDate);
                }
            });
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fee.setMoney(Integer.parseInt(tv_Money.getText().toString()));
                    fee.setEndDate(tv_endDate.getText().toString());
                    fee.setConfirm(true);
                    db.updateFee(fee);
                }
            });
        }
    }

    //set datepicker dialog
    private void setDialogDatePicker(TextView tv_date) {
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
                tv_date.setText(String.format("%s/%s/%s", i2, i1 + 1, i));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    private void setDate(String month, TextView tv_money) {
        tv_money.setText("" + (Integer.parseInt(month) * 15000));
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