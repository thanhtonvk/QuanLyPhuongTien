package com.example.quanlyphuongtien.Activity.Protector.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Protector.Adapter.ListReportAdapter;
import com.example.quanlyphuongtien.Database.TicketDBContext;
import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    EditText edt_search;
    Button btn_search;
    ListView lv_report;
    TicketDBContext dbContext;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadList();
        onClick();
    }

    private void initView(View view) {
        edt_search = view.findViewById(R.id.edt_search);
        btn_search = view.findViewById(R.id.btn_search);
        lv_report = view.findViewById(R.id.lv_ticket);
        dbContext = new TicketDBContext(getContext());
        search = new ArrayList<>();

    }

    private void onClick() {
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.clear();
                if (edt_search.getText().toString().equals("")) {
                    loadList();
                }
                for (Ticket ticket : ticketList
                ) {

                    if (ticket.getReceveDate() != null) {
                        if (ticket.getReceveDate().contains(edt_search.getText().toString()))
                            search.add(ticket);
                    }
                }
                adapter = new ListReportAdapter(getContext(), search);
                lv_report.setAdapter(adapter);

            }
        });
    }

    List<Ticket> ticketList;
    ListReportAdapter adapter;
    List<Ticket> search;

    private void loadList() {
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    ticketList.add(ticket);
                }
                if (getContext() != null) {
                    adapter = new ListReportAdapter(getContext(), ticketList);
                    lv_report.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}