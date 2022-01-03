package com.example.quanlyphuongtien.Activity.Student.Fragment;

import static com.example.quanlyphuongtien.Entities.Common.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Student.ScanActivity;
import com.example.quanlyphuongtien.Database.TicketDBContext;
import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class SendVehicleFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_vehicle, container, false);
    }

    TextView tv_id, tv_name, tv_plate, tv_vehicle;
    Button btn_confirm, btn_rescan;
    ImageView btn_scan;
    CheckBox cb_othervehicle;
    Spinner sp_vehicle;
    EditText edt_plate;

    LinearLayout layout_other;
    public static TextView tv_qr;
    Random random;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadInfo();
        onClick();
    }

    private void onClick() {
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScanActivity.class));

            }
        });
        btn_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScanActivity.class));
            }
        });
        cb_othervehicle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cb_othervehicle.isChecked()) {
                    cb_othervehicle.setChecked(false);
                    layout_other.setVisibility(View.VISIBLE);
                } else {
                    cb_othervehicle.setChecked(true);
                    layout_other.setVisibility(View.GONE);
                }

                return true;
            }
        });
        sp_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String vehicle = vehicles[position];
                if (vehicle.equalsIgnoreCase("Xe đạp")) edt_plate.setText(student.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketDBContext db = new TicketDBContext(getContext());
                if (Common.contentQR == null) {
                    Toast.makeText(getContext(), "QR không hợp lệ", Toast.LENGTH_LONG).show();
                } else {
                    Ticket ticket = new Ticket();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    ticket.setId(random.nextInt() + "");
                    ticket.setName(student.getName());
                    ticket.setQr(Common.contentQR);
                    ticket.setStatus(false);
                    ticket.setSendDate(formatter.format(date));
                    ticket.setIdhs(student.getId());
                    if (cb_othervehicle.isChecked()) {
                        if (edt_plate.getText().toString().equals("")) {
                            Toast.makeText(getContext(), "Phải nhập biển số", Toast.LENGTH_LONG).show();
                        } else {
                            ticket.setVehicle(sp_vehicle.getSelectedItem().toString());
                            ticket.setPlate(edt_plate.getText().toString());
                            db.addTicket(ticket);
                            Common.contentQR = null;
                        }
                    } else {
                        if (student.getVehicleCategory() != null) {
                            if(student.getNumberPlate()!=null){
                                ticket.setVehicle(student.getVehicleCategory());
                                ticket.setPlate(student.getNumberPlate());
                                db.addTicket(ticket);
                                Common.contentQR = null;
                            }else{
                                Toast.makeText(getContext(),"Vui lòng cập nhật biển số xe",Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(getContext(), "Vui lòng cập nhật loại xe", Toast.LENGTH_LONG).show();
                        }

                    }
                }


            }
        });
    }

    String[] vehicles = new String[]{"Xe máy", "Xe điện", "Xe đạp"};

    private void loadInfo() {
        tv_id.setText("Mã học sinh: " + student.getId());
        tv_name.setText("Họ tên: " + student.getName());
        tv_plate.setText("Biển số: " + student.getNumberPlate());
        tv_vehicle.setText("Loại xe: " + student.getVehicleCategory());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehicles);
        sp_vehicle.setAdapter(adapter);
    }

    private void initView(View view) {
        tv_id = view.findViewById(R.id.tv_id);
        tv_name = view.findViewById(R.id.tv_name);
        tv_plate = view.findViewById(R.id.tv_plate);
        tv_vehicle = view.findViewById(R.id.tv_vehicle);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_rescan = view.findViewById(R.id.btn_rescan);
        btn_scan = view.findViewById(R.id.btn_scan);
        cb_othervehicle = view.findViewById(R.id.cb_other);
        tv_qr = view.findViewById(R.id.tv_qr);
        sp_vehicle = view.findViewById(R.id.sp_vehicle);
        edt_plate = view.findViewById(R.id.edt_plate);
        layout_other = view.findViewById(R.id.layout_other);
        random = new Random();
    }


}