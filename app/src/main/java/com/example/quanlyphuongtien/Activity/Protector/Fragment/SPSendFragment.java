package com.example.quanlyphuongtien.Activity.Protector.Fragment;

import static com.example.quanlyphuongtien.Entities.Common.student;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Protector.Adapter.ListStudentAdapter;
import com.example.quanlyphuongtien.Database.StudentDBContext;
import com.example.quanlyphuongtien.Database.TicketDBContext;
import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class SPSendFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_s_p_send, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadInfo();
        onClick();
        loadDatabase();
    }

    EditText edt_search;
    Button btn_search;
    ListView lv_student;
    StudentDBContext dbContext;
    CheckBox cb_othervehicle;
    Spinner sp_vehicle;
    EditText edt_plate;
    LinearLayout layout_other;
    TextView tv_idhs;
    List<Student> search = new ArrayList<>();
    Button btn_confirm;
    Random random = new Random();

    private void initView(View view) {
        edt_search = view.findViewById(R.id.edt_search);
        btn_search = view.findViewById(R.id.btn_search);
        lv_student = view.findViewById(R.id.lv_student);
        sp_vehicle = view.findViewById(R.id.sp_vehicle);
        edt_plate = view.findViewById(R.id.edt_plate);
        layout_other = view.findViewById(R.id.layout_other);
        cb_othervehicle = view.findViewById(R.id.cb_other);
        dbContext = new StudentDBContext(getContext());
        tv_idhs = view.findViewById(R.id.tv_idhs);
        btn_confirm = view.findViewById(R.id.btn_confirm);
    }

    private void loadInfo() {
        String[] vehicles = new String[]{"Xe máy", "Xe điện", "Xe đạp"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehicles);
        sp_vehicle.setAdapter(adapter);
    }

    private void onClick() {
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

                return false;
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.clear();
                for (Student student : studentList
                ) {
                    if (edt_search.getText().toString().equals("")) {
                        loadDatabase();
                    } else {
                        if (student.getId().equalsIgnoreCase(edt_search.getText().toString())) {
                            search.add(student);
                        }
                    }
                }
                adapter = new ListStudentAdapter(getContext(), search);
                lv_student.setAdapter(adapter);

            }
        });
        lv_student.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (edt_search.getText().toString().equals("")) {
                    student = studentList.get(position);
                } else {
                    student = search.get(position);
                }
                tv_idhs.setText("Học sinh cần hỗ trợ: " + student.getId());
                if (sp_vehicle.getSelectedItem().toString().equalsIgnoreCase("Xe đạp")) {
                    edt_plate.setText(student.getId());
                }
            }

        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (student != null) {
                    TicketDBContext db = new TicketDBContext(getContext());
                    Ticket ticket = new Ticket();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    ticket.setId(random.nextInt() + "");
                    ticket.setName(student.getName());
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
                        }
                    } else {
                        ticket.setVehicle(student.getVehicleCategory());
                        ticket.setPlate(student.getNumberPlate());
                        db.addTicket(ticket);
                    }
                }


            }
        });
    }

    List<Student> studentList;
    ListStudentAdapter adapter;

    private void loadDatabase() {
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Student student = dataSnapshot.getValue(Student.class);
                    studentList.add(student);
                }
                adapter = new ListStudentAdapter(getContext(), studentList);
                lv_student.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}