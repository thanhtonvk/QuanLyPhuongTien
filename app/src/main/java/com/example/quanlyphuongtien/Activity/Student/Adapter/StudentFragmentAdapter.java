package com.example.quanlyphuongtien.Activity.Student.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.quanlyphuongtien.Activity.Student.Fragment.InfoStudentFragment;
import com.example.quanlyphuongtien.Activity.Student.Fragment.ReceiveVehicleFragment;
import com.example.quanlyphuongtien.Activity.Student.Fragment.SendVehicleFragment;

public class StudentFragmentAdapter extends FragmentStatePagerAdapter {
    public StudentFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InfoStudentFragment();
            case 1:
                return new SendVehicleFragment();
            case 2:
                return new ReceiveVehicleFragment();
            default:
                return new InfoStudentFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}