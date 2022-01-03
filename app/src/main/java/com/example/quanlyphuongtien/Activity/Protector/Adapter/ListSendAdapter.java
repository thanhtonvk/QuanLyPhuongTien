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

public class ListSendAdapter extends ArrayAdapter<Ticket> {
    List<Ticket> ticketList;
    Context context;
    List<Ticket> suggestions;

    public ListSendAdapter(@NonNull Context context, List<Ticket> ticketList) {
        super(context, 0, ticketList);
        this.context = context;
        this.ticketList = ticketList;
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sp_ticket, parent, false);
        }
        TextView tv_id = convertView.findViewById(R.id.id_student);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_date = convertView.findViewById(R.id.tv_dateofbirth);
        TextView tv_plate = convertView.findViewById(R.id.tv_plate);
        TextView tv_vehicle = convertView.findViewById(R.id.tv_vehicle);
        Ticket ticket = ticketList.get(position);
        tv_id.setText(ticket.getIdhs());
        tv_name.setText(ticket.getName());
        tv_date.setText(ticket.getSendDate());
        tv_plate.setText(ticket.getPlate());
        tv_vehicle.setText(ticket.getVehicle());
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
}
