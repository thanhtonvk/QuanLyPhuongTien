package com.example.quanlyphuongtien.Activity.Protector.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Protector.Adapter.ListReportAdapter;
import com.example.quanlyphuongtien.Database.TicketDBContext;
import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    TextView tv_date;
    String now;

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
        btn_report = view.findViewById(R.id.btn_report);
        tv_date = view.findViewById(R.id.tv_date);

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
                if (getContext() != null) {
                    adapter = new ListReportAdapter(getContext(), search);
                    lv_report.setAdapter(adapter);
                }


            }
        });
        btn_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.clear();
                for (Ticket ticket : ticketList
                ) {
                    if (ticket.getReceveDate() != null) {
                        if (!edt_search.getText().toString().equals("")) {
                            if (ticket.getReceveDate().contains(edt_search.getText().toString())) {
                                search.add(ticket);
                            }
                        } else {
                            if (ticket.getReceveDate().contains(now)) {
                                search.add(ticket);
                            }
                        }

                    }

                }
                WriteFile(search);
                sendFile();

            }
        });
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        now = formatter.format(date);
        tv_date.setText("Ngày: " + now);
    }

    public void sendFile() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        File link = getContext().getExternalCacheDir();
        File file = new File(link, "guixe.csv");
        if (file.exists()) {
            Uri path = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
            if (path != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/csv");
                intent.putExtra(Intent.EXTRA_STREAM, path);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                startActivity(Intent.createChooser(intent, "Send email..."));
            }

        }
    }

    public void WriteFile(List<Ticket> ticketList) {
        File path = getContext().getExternalCacheDir();
        File file = new File(path, "guixe.csv");
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("Mã học sinh,Họ và tên,Biển số,Giờ gửi,Giờ nhận\n");
            for (Ticket ticket : ticketList) {
                String text = ticket.getIdhs() + "," + ticket.getName() + "," + ticket.getPlate() + "," + ticket.getSendDate() + "," + ticket.getReceveDate() + "\n";
                fileWriter.write(text);
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    List<Ticket> ticketList;
    ListReportAdapter adapter;
    List<Ticket> search;
    Button btn_report;

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
                    List<Ticket> tmp = new ArrayList<>();
                    for (Ticket ticket : ticketList
                    ) {
                        if (ticket.getSendDate().contains(now)) {
                            tmp.add(ticket);
                        }
                    }
                    adapter = new ListReportAdapter(getContext(), tmp);
                    lv_report.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}