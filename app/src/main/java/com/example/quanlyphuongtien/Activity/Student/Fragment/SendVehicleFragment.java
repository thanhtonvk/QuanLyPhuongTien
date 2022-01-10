package com.example.quanlyphuongtien.Activity.Student.Fragment;

import static com.example.quanlyphuongtien.Entities.Common.student;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.quanlyphuongtien.Activity.Protector.Camera.CameraSourcePreview;
import com.example.quanlyphuongtien.Activity.Protector.Camera.GraphicFaceTrackerFactory;
import com.example.quanlyphuongtien.Activity.Protector.Camera.GraphicOverlay;
import com.example.quanlyphuongtien.Activity.Student.ScanActivity;
import com.example.quanlyphuongtien.Database.TicketDBContext;
import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;
import com.example.quanlyphuongtien.ml.Model10a1;
import com.example.quanlyphuongtien.ml.Model10a2;
import com.example.quanlyphuongtien.ml.Model10a81;
import com.example.quanlyphuongtien.ml.Model10a82;
import com.example.quanlyphuongtien.ml.Model10a83;
import com.example.quanlyphuongtien.ml.Model10a84;
import com.example.quanlyphuongtien.ml.Model11a51;
import com.example.quanlyphuongtien.ml.Model11a52;
import com.example.quanlyphuongtien.ml.Model11a53;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class SendVehicleFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_vehicle, container, false);
    }

    TextView tv_id, tv_name, tv_plate, tv_vehicle;
    Button btn_confirm, btn_rescan;
    ImageView btn_scan;
    CheckBox cb_othervehicle;
    Spinner sp_vehicle;
    EditText edt_plate;
    ImageView img;
    LinearLayout layout_other;
    public static TextView tv_qr;
    Random random;
    FloatingActionButton btn_capture;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    int check = 0;
    boolean checkIDHS = false;
    int flag = 0;
    Model10a1 model10a1;
    Model10a2 model10a2;
    Model10a81 model10a81;
    Model10a82 model10a82;
    Model10a83 model10a83;
    Model10a84 model10a84;
    Model11a51 model11a51;
    Model11a52 model11a52;
    Model11a53 model11a53;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        loadInfo();
        onClick();
        int rc = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        }
        initialize();

    }

    //Khởi tạo model
    private void initialize() {
        try {
            model10a1 = Model10a1.newInstance(getContext());
            model10a2 = Model10a2.newInstance(getContext());
            model10a81 = Model10a81.newInstance(getContext());
            model10a82 = Model10a82.newInstance(getContext());
            model10a83 = Model10a83.newInstance(getContext());
            model10a84 = Model10a84.newInstance(getContext());
            model11a51 = Model11a51.newInstance(getContext());
            model11a52 = Model11a52.newInstance(getContext());
            model11a53 = Model11a53.newInstance(getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<String> GetLabels(String fileName) {
        List<String> labels = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getContext().getAssets().open(fileName+".txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                labels.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        return labels;
    }

    private Bitmap detectFace(Bitmap bitmap) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inMutable = true;
        Bitmap defaultBitmap = bitmap;
        Paint rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.CYAN);
        rectPaint.setStyle(Paint.Style.STROKE);

        Bitmap temporaryBitmap = Bitmap.createBitmap(defaultBitmap.getWidth(), defaultBitmap
                .getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(temporaryBitmap);
        canvas.drawBitmap(defaultBitmap, 0, 0, null);

        FaceDetector faceDetector = new FaceDetector.Builder(getContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(getContext())
                    .setMessage("Face Detector could not be set up on your device :(")
                    .show();
        }

        Frame frame = new Frame.Builder().setBitmap(defaultBitmap).build();
        SparseArray<Face> sparseArray = faceDetector.detect(frame);
        float left = 0, right = 0, top = 0, bottom = 0;
        for (int i = 0; i < sparseArray.size(); i++) {
            Face face = sparseArray.valueAt(i);
            left = face.getPosition().x;
            top = face.getPosition().y;
            right = left + face.getWidth();
            bottom = top + face.getHeight();
            float cornerRadius = 2.0f;
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, rectPaint);
        }
        faceDetector.release();
        if (left < 0 || top < 0 || bottom > bitmap.getHeight() || right > bitmap.getWidth() || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            img.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
            return bitmap;
        } else {
            if (right - left <= 0 || bottom - top <= 0) {
                img.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                return bitmap;
            } else {
                Bitmap result = Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) right - (int) left, (int) bottom - (int) top);
                img.setImageDrawable(new BitmapDrawable(getResources(), result));
                return result;
            }
        }

    }

    private static final float IMAGE_MEAN = 127.5f;
    private static final float IMAGE_STD = 127.5f;

    private ByteBuffer comvertBitmapToByteBuffer(Bitmap bitmap) {

        bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, false);


        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
        byteBuffer.order(ByteOrder.nativeOrder());
        int[] intValues = new int[224 * 224];


        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        int pixel = 0;
        for (int i = 0; i < 224; i++) {
            for (int j = 0; j < 224; j++) {
                int input = intValues[pixel++];

                byteBuffer.putFloat((((input >> 16 & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                byteBuffer.putFloat((((input >> 8 & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
                byteBuffer.putFloat((((input & 0xFF) - IMAGE_MEAN) / IMAGE_STD));
            }
        }
        return byteBuffer;
    }

    private void predict(Bitmap bitmap, Dialog dialog) {
        initialize();
        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
        // Creates inputs for reference.
        ByteBuffer bytebuff = comvertBitmapToByteBuffer(detectFace(bitmap));
        inputFeature0.loadBuffer(bytebuff);
        if (student.getClassName().equalsIgnoreCase("10A1")) {


            // Runs model inference and gets result.
            Model10a1.Outputs outputs = model10a1.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                Log.e("acc", GetLabels("labels_10A1").get(i) + ":" + outputFeature0.getFloatArray()[i]);
            }
            int max = getMax(outputFeature0.getFloatArray(),"labels_10A1");
            if (student.getId().equals(GetLabels("labels_10A1").get(max))) {
                checkIDHS = true;
                Common.idStudent = GetLabels("labels_10A1").get(max);
                dialog.dismiss();

            } else {
                checkIDHS = false;
                dialog.dismiss();
                Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
            }


            // Releases model resources if no longer used.
            model10a1.close();
        }
        if (student.getClassName().equalsIgnoreCase("10A2")) {


            // Runs model inference and gets result.
            Model10a2.Outputs outputs = model10a2.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                Log.e("acc", GetLabels("labels_10A2").get(i) + ":" + outputFeature0.getFloatArray()[i]);
            }
            int max = getMax(outputFeature0.getFloatArray(),"labels_10A2");
            if (student.getId().equals(GetLabels("labels_10A2").get(max))) {
                checkIDHS = true;
                Common.idStudent = GetLabels("labels_10A2").get(max);
                dialog.dismiss();

            } else {
                checkIDHS = false;
                dialog.dismiss();
                Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
            }
            // Releases model resources if no longer used.
            model10a2.close();
        }
        if (student.getClassName().equalsIgnoreCase("10A8")) {
            int endID = Integer.parseInt(student.getId().split("")[6] + student.getId().split("")[7]);
            if (endID <= 11) {
                // Runs model inference and gets result.
                Model10a81.Outputs outputs = model10a81.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                    Log.e("acc", GetLabels("labels_10A8_1").get(i) + ":" + outputFeature0.getFloatArray()[i]);
                }
                int max = getMax(outputFeature0.getFloatArray(),"labels_10A8_1");
                if (student.getId().equals(GetLabels("labels_10A8_1").get(max))) {
                    checkIDHS = true;
                    Common.idStudent = GetLabels("labels_10A8_1").get(max);
                    dialog.dismiss();

                } else {
                    checkIDHS = false;
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                }
                // Releases model resources if no longer used.
                model10a81.close();
            }
            if (endID > 11 && endID <= 22) {
                // Runs model inference and gets result.
                Model10a82.Outputs outputs = model10a82.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                    Log.e("acc", GetLabels("labels_10A8_2").get(i) + ":" + outputFeature0.getFloatArray()[i]);
                }
                int max = getMax(outputFeature0.getFloatArray(),"labels_10A8_2");
                if (student.getId().equals(GetLabels("labels_10A8_2").get(max))) {
                    checkIDHS = true;
                    Common.idStudent = GetLabels("labels_10A8_2").get(max);
                    dialog.dismiss();

                } else {
                    checkIDHS = false;
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                }
                // Releases model resources if no longer used.
                model10a82.close();
            }
            if (endID > 22 && endID <= 33) {
                // Runs model inference and gets result.
                Model10a83.Outputs outputs = model10a83.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                    Log.e("acc", GetLabels("labels_10A8_3").get(i) + ":" + outputFeature0.getFloatArray()[i]);
                }
                int max = getMax(outputFeature0.getFloatArray(),"labels_10A8_3");
                if (student.getId().equals(GetLabels("labels_10A8_3").get(max))) {
                    checkIDHS = true;
                    Common.idStudent = GetLabels("labels_10A8_3").get(max);
                    dialog.dismiss();

                } else {
                    checkIDHS = false;
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                }
                // Releases model resources if no longer used.
                model10a83.close();
            }
            if (endID > 33 && endID <= 45) {
                // Runs model inference and gets result.
                Model10a84.Outputs outputs = model10a84.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                    Log.e("acc", GetLabels("labels_10A8_4").get(i) + ":" + outputFeature0.getFloatArray()[i]);
                }
                int max = getMax(outputFeature0.getFloatArray(),"labels_10A8_4");
                if (student.getId().equals(GetLabels("labels_10A8_4").get(max))) {
                    checkIDHS = true;
                    Common.idStudent = GetLabels("labels_10A8_4").get(max);
                    dialog.dismiss();

                } else {
                    checkIDHS = false;
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                }
                // Releases model resources if no longer used.
                model10a84.close();
            }
        }
        if (student.getClassName().equalsIgnoreCase("11A5")) {
            int endID = Integer.parseInt(student.getId().split("")[6] + student.getId().split("")[7]);
            if (endID <= 10) {
                // Runs model inference and gets result.
                Model11a51.Outputs outputs = model11a51.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                    Log.e("acc", GetLabels("labels_11A5_1").get(i) + ":" + outputFeature0.getFloatArray()[i]);
                }
                int max = getMax(outputFeature0.getFloatArray(),"labels_11A5_1");
                if (student.getId().equals(GetLabels("labels_11A5_1").get(max))) {
                    checkIDHS = true;
                    Common.idStudent = GetLabels("labels_11A5_1").get(max);
                    dialog.dismiss();

                } else {
                    checkIDHS = false;
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                }
                // Releases model resources if no longer used.
                model11a51.close();
            }
            if (endID > 10 && endID <= 20) {
                // Runs model inference and gets result.
                Model11a52.Outputs outputs = model11a52.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                    Log.e("acc", GetLabels("labels_11A5_2").get(i) + ":" + outputFeature0.getFloatArray()[i]);
                }
                int max = getMax(outputFeature0.getFloatArray(),"labels_11A5_2");
                if (student.getId().equals(GetLabels("labels_11A5_2").get(max))) {
                    checkIDHS = true;
                    Common.idStudent = GetLabels("labels_11A5_2").get(max);
                    dialog.dismiss();

                } else {
                    checkIDHS = false;
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                }
                // Releases model resources if no longer used.
                model11a52.close();
            }
            if (endID > 20 && endID <= 30) {
                // Runs model inference and gets result.
                Model11a53.Outputs outputs = model11a53.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                for (int i = 0; i < outputFeature0.getFloatArray().length; i++) {
                    Log.e("acc", GetLabels("labels_11A5_3").get(i) + ":" + outputFeature0.getFloatArray()[i]);
                }
                int max = getMax(outputFeature0.getFloatArray(),"labels_11A5_3");
                if (student.getId().equals(GetLabels("labels_11A5_3").get(max))) {
                    checkIDHS = true;
                    Common.idStudent = GetLabels("labels_11A5_3").get(max);
                    dialog.dismiss();

                } else {
                    checkIDHS = false;
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                }
                // Releases model resources if no longer used.
                model11a53.close();
            }
        }

    }

    private void onClick() {
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ScanActivity.class));
                flag = 1;
                check = 0;
            }
        });
        btn_rescan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setVisibility(View.GONE);
                mPreview.setVisibility(View.VISIBLE);
            }
        });
        cb_othervehicle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (cb_othervehicle.isChecked()) {
                    cb_othervehicle.setChecked(false);
                    layout_other.setVisibility(View.VISIBLE);
                } else {
                    cb_othervehicle.setChecked(true);
                    layout_other.setVisibility(View.GONE);
                }

                return false;
            }
        });
        sp_vehicle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String vehicle = vehicles[position];
                if (vehicle.equalsIgnoreCase("Xe đạp")) edt_plate.setText(student.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Đang quét");
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                if (check == 1) {
                    dialog.show();
                    check = 0;
                    mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(@NonNull byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            img.setVisibility(View.VISIBLE);
                            mPreview.setVisibility(View.GONE);
                            img.setImageBitmap(bitmap);
                            predict(bitmap, dialog);
                        }
                    });
                } else {
                    check = 1;
                    img.setVisibility(View.GONE);
                    mPreview.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketDBContext db = new TicketDBContext(getContext());
                if (Common.checkLocation() <= 500) {
                    if (flag == 0) {
                        Toast.makeText(getContext(), "Phải quét khuôn mặt hoặc QR", Toast.LENGTH_LONG).show();
                    } else {
                        if (flag == 2) {
                            if (checkIDHS) {
                                Ticket ticket = new Ticket();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                ticket.setId(random.nextInt() + "");
                                ticket.setName(student.getName());
                                ticket.setStatus(false);
                                ticket.setSendDate(formatter.format(date));
                                ticket.setIdhs(student.getId());
                                if (cb_othervehicle.isChecked()) {
                                    if (edt_plate.getText().toString().equals("")) {
                                        Toast.makeText(getContext(), "Phải nhập biển số", Toast.LENGTH_LONG).show();
                                    } else {
                                        ticket.setVehicle(sp_vehicle.getSelectedItem().toString());
                                        ticket.setPlate(edt_plate.getText().toString());
                                        db.addTicket(ticket);
                                    }
                                } else {
                                    if (student.getVehicleCategory() != null) {
                                        if (student.getNumberPlate() != null) {
                                            ticket.setVehicle(student.getVehicleCategory());
                                            ticket.setPlate(student.getNumberPlate());
                                            db.addTicket(ticket);
                                        } else {
                                            Toast.makeText(getContext(), "Vui lòng cập nhật biển số xe", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Toast.makeText(getContext(), "Vui lòng cập nhật loại xe", Toast.LENGTH_LONG).show();
                                    }

                                }
                            } else {
                                Toast.makeText(getContext(), "Khuôn mặt không khớp, quét lại", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            if (Common.contentQR == null) {
                                Toast.makeText(getContext(), "QR không hợp lệ", Toast.LENGTH_LONG).show();
                            } else {
                                Ticket ticket = new Ticket();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                ticket.setId(random.nextInt() + "");
                                ticket.setName(student.getName());
                                ticket.setQr(Common.contentQR);
                                ticket.setStatus(false);
                                ticket.setSendDate(formatter.format(date));
                                ticket.setIdhs(student.getId());
                                if (cb_othervehicle.isChecked()) {
                                    if (edt_plate.getText().toString().equals("")) {
                                        Toast.makeText(getContext(), "Phải nhập biển số", Toast.LENGTH_LONG).show();
                                    } else {
                                        ticket.setVehicle(sp_vehicle.getSelectedItem().toString());
                                        ticket.setPlate(edt_plate.getText().toString());
                                        db.addTicket(ticket);
                                        Common.contentQR = null;
                                    }
                                } else {
                                    if (student.getVehicleCategory() != null) {
                                        if (student.getNumberPlate() != null) {
                                            ticket.setVehicle(student.getVehicleCategory());
                                            ticket.setPlate(student.getNumberPlate());
                                            db.addTicket(ticket);
                                            Common.contentQR = null;
                                        } else {
                                            Toast.makeText(getContext(), "Vui lòng cập nhật biển số xe", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Toast.makeText(getContext(), "Vui lòng cập nhật loại xe", Toast.LENGTH_LONG).show();
                                    }

                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Bạn đang không ở trường, vui lòng đến trường gửi xe", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    String[] vehicles = new String[]{"Xe máy", "Xe điện", "Xe đạp"};

    private void loadInfo() {
        tv_id.setText("Mã học sinh: " + student.getId());
        tv_name.setText("Họ tên: " + student.getName());
        tv_plate.setText("Biển số: " + student.getNumberPlate());
        tv_vehicle.setText("Loại xe: " + student.getVehicleCategory());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, vehicles);
        sp_vehicle.setAdapter(adapter);
    }

    private int getMax(float[] arr,String fileName) {
        int index = 0;
        float min = 0.0f;
        for (int i = 0; i < GetLabels(fileName).size(); i++) {
            if (arr[i] > min) {
                index = i;
                min = arr[i];
            }
        }
        return index;
    }


    private void createCameraSource() {
        Context context = getContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory(mGraphicOverlay, getContext()))
                        .build());
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(1024, 1280)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

    }


    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    private void initView(View view) {
        tv_id = view.findViewById(R.id.tv_id);
        tv_name = view.findViewById(R.id.tv_name);
        tv_plate = view.findViewById(R.id.tv_plate);
        tv_vehicle = view.findViewById(R.id.tv_vehicle);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_rescan = view.findViewById(R.id.btn_chuplai);
        btn_scan = view.findViewById(R.id.btn_scan);
        cb_othervehicle = view.findViewById(R.id.cb_other);
        tv_qr = view.findViewById(R.id.tv_qr);
        sp_vehicle = view.findViewById(R.id.sp_vehicle);
        edt_plate = view.findViewById(R.id.edt_plate);
        layout_other = view.findViewById(R.id.layout_other);
        random = new Random();
        mPreview = view.findViewById(R.id.preview);
        mGraphicOverlay = view.findViewById(R.id.faceOverlay);
        btn_capture = view.findViewById(R.id.btn_capture);
        img = view.findViewById(R.id.img);

    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }
}