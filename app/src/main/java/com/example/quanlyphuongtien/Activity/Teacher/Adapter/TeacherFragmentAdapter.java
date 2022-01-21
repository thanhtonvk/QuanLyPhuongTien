package com.example.quanlyphuongtien.Activity.Teacher.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.quanlyphuongtien.Activity.Teacher.Fragment.InfoFragment;
import com.example.quanlyphuongtien.Activity.Teacher.Fragment.UpdateFragment;

public class TeacherFragmentAdapter extends FragmentStatePagerAdapter {
    public TeacherFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InfoFragment();
            case 1:
                return new UpdateFragment();
            default:
                return new InfoFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
