package com.example.quanlyphuongtien.Activity.Protector.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.R;

import java.util.ArrayList;
import java.util.List;

public class ListStudentAdapter extends ArrayAdapter<Student> {
    List<Student> studentList;
    Context context;
    List<Student> suggestions;

    public ListStudentAdapter(@NonNull Context context, List<Student> studentList) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_sp_student, parent, false);
        }
        TextView tv_id = convertView.findViewById(R.id.id_student);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_date = convertView.findViewById(R.id.tv_dateofbirth);
        TextView tv_plate = convertView.findViewById(R.id.tv_plate);
        TextView tv_vehicle = convertView.findViewById(R.id.tv_vehicle);
        Student student = studentList.get(position);
        tv_id.setText(student.getId());
        tv_name.setText(student.getName());
        tv_date.setText(student.getDateOfBirth());
        tv_plate.setText(student.getNumberPlate());
        tv_vehicle.setText(student.getVehicleCategory());
        return convertView;
    }

    @Override
    public int getCount() {
        return studentList.size();
    }

    @Nullable
    @Override
    public Student getItem(int position) {
        return studentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}