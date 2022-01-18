package com.example.quanlyphuongtien.Activity.Protector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.quanlyphuongtien.Activity.Protector.Adapter.ProtectorFragmentAdapter;
import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.MainActivity;
import com.example.quanlyphuongtien.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProtectorActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protector);
        if (Common.protector == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        initView();
        setViewPager();
        getCurrentLocation();
    }

    private void initView() {
        bottomNav = findViewById(R.id.nav_bottom);
        viewPager = findViewById(R.id.viewpager);
    }

    private FusedLocationProviderClient locationProviderClient;
    private Geocoder geocoder;
    private List<Address> addresses;

    private void getCurrentLocation() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(ProtectorActivity.this, Locale.getDefault());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Common.lStudent = new com.example.quanlyphuongtien.Entities.Location(location.getLatitude(), location.getLongitude());
                    try {
                        Common.lStudent = new com.example.quanlyphuongtien.Entities.Location(location.getLatitude(), location.getLongitude());
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        Toast.makeText(ProtectorActivity.this, "Bạn đang ở " + addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

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

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}