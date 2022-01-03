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

import com.example.quanlyphuongtien.Entities.Protector;
import com.example.quanlyphuongtien.R;

import java.util.ArrayList;
import java.util.List;

public class ProtectorListAdapter extends ArrayAdapter<Protector> {
    List<Protector> ProtectorList;
    Context context;
    List<Protector> suggestions;

    public ProtectorListAdapter(@NonNull Context context, List<Protector> ProtectorList) {
        super(context, 0, ProtectorList);
        this.context = context;
        this.ProtectorList = ProtectorList;
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_protector, parent, false);
        }
        TextView tv_stt = convertView.findViewById(R.id.tv_stt);
        TextView tv_username = convertView.findViewById(R.id.tv_username);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_headclass = convertView.findViewById(R.id.tv_headclass);
        Protector Protector = ProtectorList.get(position);
        tv_stt.setText((position + 1) + "");
        tv_name.setText(Protector.getName());
        tv_username.setText(Protector.getUsername());
        tv_headclass.setText(Protector.getPosition());
        return convertView;
    }

    @Override
    public int getCount() {
        return ProtectorList.size();
    }

    @Nullable
    @Override
    public Protector getItem(int position) {
        return ProtectorList.get(position);
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
                suggestions.addAll(ProtectorList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Protector item : ProtectorList) {
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getDateOfBirth().contains(filterPattern) ||
                            item.getPosition().toLowerCase().contains(filterPattern)) {
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
            return ((Protector) resultValue).getName();
        }
    };
}

