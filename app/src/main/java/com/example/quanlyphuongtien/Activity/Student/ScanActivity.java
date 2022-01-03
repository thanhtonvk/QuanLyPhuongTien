package com.example.quanlyphuongtien.Activity.Student;

import static com.example.quanlyphuongtien.Activity.Student.Fragment.SendVehicleFragment.tv_qr;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanActivity extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        scanCode();
    }

    private void scanCode() {
        IntentIntegrator intentIntegrator = new IntentIntegrator(ScanActivity.this);

        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("Quét mã QR");
        intentIntegrator.setCaptureActivity(CaptureAct.class);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        intentIntegrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult.getContents() != null) {
            if (intentResult.getContents().equals("test12345")) {
                Common.contentQR = intentResult.getContents();
                tv_qr.setText("ID gửi: " + Common.contentQR);
                finish();
            }
        }
    }
}