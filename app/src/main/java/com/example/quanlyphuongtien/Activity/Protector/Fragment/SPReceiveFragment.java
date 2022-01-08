package com.example.quanlyphuongtien.Activity.Protector.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Protector.Adapter.ListSendAdapter;
import com.example.quanlyphuongtien.Activity.Protector.FaceRecognitionReceiveActivity;
import com.example.quanlyphuongtien.Database.TicketDBContext;
import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SPReceiveFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_s_p_receive, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        onClick();
        loadDatabase();
    }

    EditText edt_search;
    Button btn_search;
    ListView lv_ticket;
    TicketDBContext dbContext;
    TextView tv_idhs;
    List<Ticket> search = new ArrayList<>();
    Button btn_confirm, btn_face;

    private void initView(View view) {
        edt_search = view.findViewById(R.id.edt_search);
        btn_search = view.findViewById(R.id.btn_search);
        lv_ticket = view.findViewById(R.id.lv_ticket);
        dbContext = new TicketDBContext(getContext());
        tv_idhs = view.findViewById(R.id.tv_idhs);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_face = view.findViewById(R.id.btn_face);
    }


    Ticket ticket;

    private void onClick() {


        btn_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), FaceRecognitionReceiveActivity.class));
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.clear();
                if (edt_search.getText().toString().equals("")) {
                    loadDatabase();
                }
                for (Ticket ticket : ticketList
                ) {
                    if (ticket.getId().equalsIgnoreCase(edt_search.getText().toString())) {
                        search.add(ticket);

                    }
                }
                adapter = new ListSendAdapter(getContext(), search);
                lv_ticket.setAdapter(adapter);

            }
        });
        lv_ticket.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (edt_search.getText().toString().equals("")) {
                    ticket = ticketList.get(position);
                } else {
                    ticket = search.get(position);
                }
                tv_idhs.setText("Học sinh cần hỗ trợ: " + ticket.getIdhs());
            }

        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = new Date();
                TicketDBContext db = new TicketDBContext(getContext());
                if (ticket != null) {
                    ticket.setReceveDate(formatter.format(date));
                    ticket.setStatus(true);
                    db.updateTicket(ticket);
                }
            }
        });
    }

    List<Ticket> ticketList;
    ListSendAdapter adapter;

    private void loadDatabase() {
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    if (!ticket.isStatus()) {
                        ticketList.add(ticket);
                    }

                }
                adapter = new ListSendAdapter(getContext(), ticketList);
                lv_ticket.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}