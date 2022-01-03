package com.example.quanlyphuongtien.Activity.Student.Fragment;

import static com.example.quanlyphuongtien.Entities.Common.student;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ReceiveVehicleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_receive_vehicle, container, false);
    }

    TextView tv_id, tv_name, tv_plate, tv_vehicle, tv_date;
    Button btn_confirm, btn_rescan;
    ImageView btn_scan;

    Random random;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        onClick();
    }

    private void onClick() {
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScanActivity.class));
                loadInfoStudent();
            }
        });
        btn_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScanActivity.class));
                loadInfoStudent();
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketDBContext db = new TicketDBContext(getContext());
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                if (Common.contentQR != null) {
                    rs.setStatus(true);
                    rs.setReceveDate(formatter.format(date));
                    db.updateTicket(rs);
                } else {
                    Toast.makeText(getContext(), "QR code không hợp lệ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initView(View view) {
        tv_id = view.findViewById(R.id.tv_id);
        tv_name = view.findViewById(R.id.tv_name);
        tv_plate = view.findViewById(R.id.tv_plate);
        tv_vehicle = view.findViewById(R.id.tv_vehicle);
        btn_confirm = view.findViewById(R.id.btn_receive);
        btn_rescan = view.findViewById(R.id.btn_rescan);
        btn_scan = view.findViewById(R.id.btn_scan);
        tv_date = view.findViewById(R.id.tv_sendtime);
        random = new Random();
    }

    Ticket rs;

    private void loadInfoStudent() {
        TicketDBContext db = new TicketDBContext(getContext());
        db.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    if (!ticket.isStatus() && ticket.getIdhs().equals(student.getId())) {
                        tv_id.setText("Mã học sinh: " + ticket.getIdhs());
                        tv_name.setText("Họ tên: " + ticket.getName());
                        tv_plate.setText("Biển số: " + ticket.getPlate());
                        tv_vehicle.setText("Loại xe: " + ticket.getVehicle());
                        tv_date.setText("Giờ gửi: " + ticket.getSendDate());
                        rs = ticket;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}