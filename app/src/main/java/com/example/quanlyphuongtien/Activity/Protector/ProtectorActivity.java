package com.example.quanlyphuongtien.Activity.Protector;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.quanlyphuongtien.Activity.Protector.Adapter.ProtectorFragmentAdapter;
import com.example.quanlyphuongtien.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProtectorActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protector);
        initView();
        setViewPager();
    }

    private void initView() {
        bottomNav = findViewById(R.id.nav_bottom);
        viewPager = findViewById(R.id.viewpager);
    }

    //set view pager
    private void setViewPager() {
        ProtectorFragmentAdapter fragmentAdapter = new ProtectorFragmentAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNav.getMenu().findItem(R.id.menu_info_protector).setChecked(true);
                        break;
                    case 1:
                        bottomNav.getMenu().findItem(R.id.menu_spsend_protector).setChecked(true);
                        break;
                    case 2:
                        bottomNav.getMenu().findItem(R.id.menu_spreceive_protector).setChecked(true);
                        break;
                    case 3:
                        bottomNav.getMenu().findItem(R.id.menu_report_protector).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_info_protector:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.menu_spsend_protector:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.menu_spreceive_protector:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.menu_report_protector:
                        viewPager.setCurrentItem(3);
                        break;
                }
                return true;
            }
        });
    }
}