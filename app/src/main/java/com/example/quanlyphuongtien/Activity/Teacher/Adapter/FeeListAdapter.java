package com.example.quanlyphuongtien.Activity.Teacher.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quanlyphuongtien.Entities.Fee;
import com.example.quanlyphuongtien.R;

import java.util.ArrayList;
import java.util.List;

public class FeeListAdapter extends ArrayAdapter<Fee> {
    List<Fee> feeList;
    Context context;
    List<Fee> suggestions;

    public FeeListAdapter(@NonNull Context context, List<Fee> feeList) {
        super(context, 0, feeList);
        this.context = context;
        this.feeList = feeList;
        suggestions = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_fee, parent, false);
        }
        TextView tv_stt = convertView.findViewById(R.id.tv_stt);
        TextView tv_id = convertView.findViewById(R.id.tv_id);
        TextView tv_name = convertView.findViewById(R.id.tv_name);
        TextView tv_money = convertView.findViewById(R.id.tv_money);
        CheckBox cb_confirm = convertView.findViewById(R.id.cb_confirm);
        Fee fee = feeList.get(position);
        tv_stt.setText((position + 1) + "");
        tv_id.setText(fee.getId());
        tv_name.setText(fee.getName());
        if (fee.getMoney() == 0) {
            tv_money.setText("");
        } else {
            tv_money.setText(fee.getMoney() + "");
        }
        if (fee.isConfirm()) {
            cb_confirm.setChecked(true);
        } else {
            cb_confirm.setChecked(false);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return feeList.size();
    }

    @Nullable
    @Override
    public Fee getItem(int position) {
        return feeList.get(position);
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
                suggestions.addAll(feeList);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Fee item : feeList) {
                    if (item.getName().toLowerCase().contains(filterPattern) ||
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
            return ((Fee) resultValue).getName();
        }
    };
}
