package com.example.quanlyphuongtien.Activity.Admin.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.quanlyphuongtien.Activity.Admin.Fragment.ProtectorFragment;
import com.example.quanlyphuongtien.Activity.Admin.Fragment.TeacherFragment;

public class AdminFragmentAdapter extends FragmentStatePagerAdapter {
    public AdminFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TeacherFragment();
            case 1:
                return new ProtectorFragment();
            default:
                return new TeacherFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
