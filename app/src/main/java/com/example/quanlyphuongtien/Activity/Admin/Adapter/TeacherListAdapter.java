package com.example.quanlyphuongtien.Activity.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quanlyphuongtien.Entities.Teacher;
import com.example.quanlyphuongtien.R;

import java.util.ArrayList;
import java.util.List;

public class TeacherListAdapter extends ArrayAdapter<Teacher> {
    List<Teacher> TeacherList;
    Context context;
    List<Teacher> suggestions;

    public TeacherListAdapter(@NonNull Context context, List<Teacher> TeacherList) {
        super(context, 0, TeacherList);
        this.context = context;
        this.TeacherList = TeacherList;
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_teacher, parent, false);
        }
        TextView tv_stt = convertView.findViewById(R.id.tv_stt);
        TextView tv_username = convertView.findViewById(R.id.tv_username);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_headclass = convertView.findViewById(R.id.tv_headclass);

        Teacher Teacher = TeacherList.get(position);

        tv_stt.setText((position + 1) + "");
        tv_name.setText(Teacher.getName());
        tv_username.setText(Teacher.getUsername());
        tv_headclass.setText(Teacher.getHeadTeacher());
        return convertView;
    }

    @Override
    public int getCount() {
        return TeacherList.size();
    }

    @Nullable
    @Override
    public Teacher getItem(int position) {
        return TeacherList.get(position);
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
                suggestions.addAll(TeacherList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Teacher item : TeacherList) {
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getDateOfBirth().contains(filterPattern) ||
                            item.getHeadTeacher().toLowerCase().contains(filterPattern)) {
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
            return ((Teacher) resultValue).getName();
        }
    };
}

