package com.example.quanlyphuongtien.Activity.Protector.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;

import java.util.ArrayList;
import java.util.List;

public class ListReportAdapter extends ArrayAdapter<Ticket> {
    List<Ticket> ticketList;
    Context context;
    List<Ticket> suggestions;

    public ListReportAdapter(@NonNull Context context, List<Ticket> ticketList) {
        super(context, 0, ticketList);
        this.context = context;
        this.ticketList = ticketList;
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        }
        TextView tv_id = convertView.findViewById(R.id.tv_idhs);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_plate = convertView.findViewById(R.id.tv_plate);
        TextView tv_send = convertView.findViewById(R.id.tv_sendtime);
        TextView tv_receive = convertView.findViewById(R.id.tv_receivetime);
        Ticket ticket = ticketList.get(position);
        tv_id.setText(ticket.getIdhs());
        tv_name.setText(ticket.getName());
        tv_plate.setText(ticket.getPlate());
        tv_send.setText(splitString(ticket.getSendDate()));
        tv_receive.setText(splitString(ticket.getReceveDate()));
        return convertView;
    }

    @Override
    public int getCount() {
        return ticketList.size();
    }

    @Nullable
    @Override
    public Ticket getItem(int position) {
        return ticketList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String splitString(String date) {
        if (date != null) {
            return date.split(" ")[1];
        }
        return "";

    }
}
