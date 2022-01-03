package com.example.quanlyphuongtien.Activity.Protector.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.quanlyphuongtien.Activity.Protector.Fragment.InfoProtectorFragment;
import com.example.quanlyphuongtien.Activity.Protector.Fragment.ReportFragment;
import com.example.quanlyphuongtien.Activity.Protector.Fragment.SPReceiveFragment;
import com.example.quanlyphuongtien.Activity.Protector.Fragment.SPSendFragment;


public class ProtectorFragmentAdapter extends FragmentStatePagerAdapter {
    public ProtectorFragmentAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new InfoProtectorFragment();
            case 1:
                return new SPSendFragment();
            case 2:
                return new SPReceiveFragment();
            case 3:
                return new ReportFragment();
            default:
                return new InfoProtectorFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
