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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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

import java.time.LocalDate;
import java.time.Period;
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
    int feeMoney = 0;

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
        db.other.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String str = dataSnapshot.getValue(String.class);
                    if (str != null && !str.equals("")) {
                        feeMoney = Integer.parseInt(str);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //set dialog info
    private void setDialogFeeInfo(int postion) {
        Fee fee = feeList.get(postion);
        Student student = getStudent(fee.getIdSV());
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_info_fee);
        dialog.show();
        TextView tv_id, tv_name, tv_vehicle, tv_startDate, tv_endDate;
        EditText tv_Money;
        EditText sp_month = dialog.findViewById(R.id.sp_month);
        tv_id = dialog.findViewById(R.id.tv_id);
        tv_name = dialog.findViewById(R.id.tv_name);
        tv_vehicle = dialog.findViewById(R.id.tv_vehicle);
        tv_startDate = dialog.findViewById(R.id.tv_startdate);
        tv_endDate = dialog.findViewById(R.id.tv_enddate);
        tv_Money = dialog.findViewById(R.id.tv_money);
        Button btn_confirm = dialog.findViewById(R.id.btn_update);
        Button btn_delete = dialog.findViewById(R.id.btn_delete);
        EditText edt_fee = dialog.findViewById(R.id.edt_fee);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteFee(fee.getId());
                dialog.dismiss();
            }
        });
        if (fee.isConfirm()) {
            sp_month.setVisibility(View.GONE);
            btn_confirm.setVisibility(View.GONE);
            edt_fee.setText(feeMoney + "");
            tv_id.setText(fee.getIdSV());
            tv_name.setText(fee.getName());
            tv_vehicle.setText(student.getVehicleCategory());
            tv_startDate.setText(fee.getStartDate());
            tv_endDate.setText(fee.getEndDate());
            tv_Money.setText(fee.getMoney() + "");
        } else {
            edt_fee.setText(feeMoney + "");
            tv_id.setText(fee.getIdSV());
            tv_name.setText(fee.getName());
            tv_vehicle.setText(student.getVehicleCategory());
            tv_startDate.setText(fee.getStartDate());
            tv_startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialogDatePicker(tv_startDate);
                }
            });
            tv_endDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setDialogDatePickerEnd(sp_month, tv_Money, tv_startDate, tv_endDate, edt_fee);

                }
            });
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.other.child("1").setValue(edt_fee.getText().toString());
                    fee.setMoney(Integer.parseInt(tv_Money.getText().toString()));
                    fee.setStartDate(tv_startDate.getText().toString());
                    fee.setEndDate(tv_endDate.getText().toString());
                    fee.setConfirm(true);
                    db.updateFee(fee);
                    dialog.dismiss();
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
                tv_date.setText(String.format("%s-%s-%s", i2, i1 + 1, i));
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    //set datepicker dialog
    private void setDialogDatePickerEnd(EditText sp_month, TextView tv_money, TextView tv_start, TextView tv_date, TextView edt_fee) {
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
                tv_date.setText(String.format("%s-%s-%s", i2, i1 + 1, i));
                setDate(sp_month, tv_money, tv_start, tv_date, edt_fee);
            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, selectedYear, selectedMonth, selectedDay);
        datePickerDialog.show();
    }

    private void setDate(EditText sp_month, TextView tv_money, TextView tv_start, TextView tv_end, TextView edt_fee) {
        String start = tv_start.getText().toString();
        String end = tv_end.getText().toString();
        String[] arrStart = start.split("-");
        String[] arrEnd = end.split("-");
        int startYear = Integer.parseInt(arrStart[2]);
        int endYear = Integer.parseInt(arrEnd[2]);
        int startMonth = Integer.parseInt(arrStart[1]);
        int endMonth = Integer.parseInt(arrEnd[1]);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
            LocalDate endDate = LocalDate.of(endYear, endMonth, 1);
            Period period = Period.between(startDate, endDate);
            int numOfMonth = (period.getYears() * 12) + period.getMonths();
            sp_month.setText(numOfMonth + "");
            int fee = Integer.parseInt(edt_fee.getText().toString());
            tv_money.setText((numOfMonth * fee) + "");
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