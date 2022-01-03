package com.example.quanlyphuongtien.Activity.Teacher.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.R;

import java.util.ArrayList;
import java.util.List;

public class StudentListAdapter extends ArrayAdapter<Student> {
    List<Student> studentList;
    Context context;
    List<Student> suggestions;

    public StudentListAdapter(@NonNull Context context, List<Student> studentList) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        }
        TextView tv_stt = convertView.findViewById(R.id.tv_stt);
        TextView tv_id = convertView.findViewById(R.id.tv_id);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_dateofbirth = convertView.findViewById(R.id.tv_dateofbirth);
        TextView tv_classname = convertView.findViewById(R.id.tv_classname);
        Student student = studentList.get(position);
        tv_stt.setText((position +1)+ "");
        tv_id.setText(student.getId());
        tv_name.setText(student.getName());
        tv_dateofbirth.setText(student.getDateOfBirth());
        tv_classname.setText(student.getClassName());
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

    @NonNull
    @Override
    public Filter getFilter() {
        return userFilter;
    }

    private Filter userFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults results = new FilterResults();
            suggestions.clear();
            if (charSequence == null || charSequence.length() == 0 || charSequence.equals("")) {
                suggestions.addAll(studentList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Student item : studentList) {
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getDateOfBirth().contains(filterPattern) ||
                            item.getId().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }


        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            clear();
            addAll((List) filterResults.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Student) resultValue).getName();
        }
    };
}
