package com.example.quanlyphuongtien.Activity.Protector;

import static com.example.quanlyphuongtien.Activity.Teacher.Fragment.UpdateFragment.studentList;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.quanlyphuongtien.Activity.Protector.Camera.CameraSourcePreview;
import com.example.quanlyphuongtien.Activity.Protector.Camera.GraphicFaceTrackerFactory;
import com.example.quanlyphuongtien.Activity.Protector.Camera.GraphicOverlay;
import com.example.quanlyphuongtien.Database.StudentDBContext;
import com.example.quanlyphuongtien.Database.TicketDBContext;
import com.example.quanlyphuongtien.Entities.Common;
import com.example.quanlyphuongtien.Entities.Student;
import com.example.quanlyphuongtien.Entities.Ticket;
import com.example.quanlyphuongtien.R;
import com.example.quanlyphuongtien.ml.Model;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

public class FaceRecognitionReceiveActivity extends AppCompatActivity {

    Button btn_open, btn_confirm;
    ImageView img;
    TextView tv_id, tv_name;
    int request = 123;
    Bitmap bitmap;
    ByteBuffer imgData;
    int check = 0;
    FloatingActionButton btn_capture;
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    float acc = 0;
    TextView tv_plate, tv_vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_recognition_receive);
        initView();
        onClick();
        loadListStudent();
        loadListTicket();
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }


    }

    private List<String> GetLabels() {
        List<String> labels = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("label.txt")));

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

    int DIM_BATCH_SIZE = 1;
    int DIM_IMG_SIZE_X = 224;
    int DIM_IMG_SIZE_Y = 224;
    int DIM_PIXEL_SIZE = 3;

    private void onClick() {
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, request);
            }
        });
        imgData = ByteBuffer.allocateDirect(DIM_BATCH_SIZE * DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y * DIM_PIXEL_SIZE); // now buffer size and input size match
        imgData.order(ByteOrder.nativeOrder());
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acc >= 0.3) {
                    if (Common.checkLocation() <= 500) {
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        TicketDBContext db = new TicketDBContext(FaceRecognitionReceiveActivity.this);
                        if (getTicket(Common.student.getId()) != null) {
                            Ticket ticket = getTicket(Common.student.getId());
                            ticket.setReceveDate(formatter.format(date));
                            ticket.setStatus(true);
                            db.updateTicket(ticket);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Bạn đang không ở trường", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Khuôn mặt không hợp lệ", Toast.LENGTH_LONG).show();
                }
            }
        });
        btn_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_id.setText("");
                tv_name.setText("");
                tv_plate.setText("");
                tv_vehicle.setText("");
                if (check == 0) {
                    check = 1;
                    mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(@NonNull byte[] bytes) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            img.setVisibility(View.VISIBLE);
                            mPreview.setVisibility(View.GONE);
                            img.setImageBitmap(bitmap);
                            try {
                                Model model = Model.newInstance(FaceRecognitionReceiveActivity.this);

                                // Creates inputs for reference.
                                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                                ByteBuffer bytebuff = comvertBitmapToByteBuffer(detectFace(bitmap));
                                inputFeature0.loadBuffer(bytebuff);

                                // Runs model inference and gets result.
                                Model.Outputs outputs = model.process(inputFeature0);
                                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                                int max = getMax(outputFeature0.getFloatArray());
                                acc = outputFeature0.getFloatArray()[max];
                                if (acc > 0.3) {
                                    tv_id.setText("Mã học sinh" + GetLabels().get(max));
                                    if (getStudent(GetLabels().get(max)).getName() != null) {
                                        tv_name.setText("Họ tên: " + getStudent(GetLabels().get(max)).getName());
                                        Common.student = getStudent(GetLabels().get(max));
                                        if (getTicket(Common.student.getId()) != null) {
                                            Ticket ticket = getTicket(Common.student.getId());
                                            tv_plate.setText("Biển số: " + ticket.getPlate());
                                            tv_vehicle.setText("Loại xe: " + ticket.getVehicle());
                                        }
                                    }
                                    Common.idStudent = GetLabels().get(max);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Không nhận diên được khuôn mặt, thử lại", Toast.LENGTH_LONG).show();
                                }

                                // Releases model resources if no longer used.
                                model.close();
                            } catch (IOException e) {
                                // TODO Handle the exception
                            }
                        }
                    });
                } else {
                    check = 0;
                    img.setVisibility(View.GONE);
                    mPreview.setVisibility(View.VISIBLE);
                }

            }
        });
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            img.setVisibility(View.VISIBLE);
            mPreview.setVisibility(View.GONE);
            img.setImageURI(data.getData());
            img.setImageURI(data.getData());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                try {
                    Model model = Model.newInstance(FaceRecognitionReceiveActivity.this);

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                    ByteBuffer bytebuff = comvertBitmapToByteBuffer(detectFace(bitmap));
                    inputFeature0.loadBuffer(bytebuff);

                    // Runs model inference and gets result.
                    Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
                    int max = getMax(outputFeature0.getFloatArray());
                    tv_id.setText("Mã học sinh: " + GetLabels().get(max));
                    if (getStudent(GetLabels().get(max)).getName() != null) {
                        tv_name.setText("Họ tên: " + getStudent(GetLabels().get(max)).getName());
                        Common.student = getStudent(GetLabels().get(max));
                        if (getTicket(Common.student.getId()) != null) {
                            Ticket ticket = getTicket(Common.student.getId());
                            tv_plate.setText("Biển số: " + ticket.getPlate());
                            tv_vehicle.setText("Loại xe: " + ticket.getVehicle());
                        }
                    }
                    Common.idStudent = GetLabels().get(max);
                    // Releases model resources if no longer used.
                    model.close();
                } catch (IOException e) {
                    // TODO Handle the exception
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        btn_open = findViewById(R.id.btn_open);
        btn_confirm = findViewById(R.id.btn_confirm);
        img = findViewById(R.id.img);
        tv_name = findViewById(R.id.tv_name);
        tv_id = findViewById(R.id.tv_id);
        btn_capture = findViewById(R.id.btn_capture);
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        tv_plate = findViewById(R.id.tv_plate);
        tv_vehicle = findViewById(R.id.tv_vehicle);
    }

    private int getMax(float[] arr) {
        int index = 0;
        float min = 0.0f;
        for (int i = 0; i < 100; i++) {
            if (arr[i] > min) {
                index = i;
                min = arr[i];
            }
        }
        return index;
    }

    private Student getStudent(String ID) {
        Student rs = new Student();
        for (Student student : studentList
        ) {
            if (student.getId().equals(ID)) rs = student;
        }
        return rs;
    }

    private Ticket getTicket(String IDHS) {
        Ticket rs = new Ticket();
        for (Ticket ticket : ticketList) {
            if (ticket.getIdhs().equals(IDHS) && !ticket.isStatus()) rs = ticket;
        }
        return rs;
    }


    private void loadListStudent() {
        StudentDBContext dbContext = new StudentDBContext(FaceRecognitionReceiveActivity.this);
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                studentList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Student student = dataSnapshot.getValue(Student.class);
                    studentList.add(student);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

        FaceDetector faceDetector = new FaceDetector.Builder(FaceRecognitionReceiveActivity.this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();
        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(FaceRecognitionReceiveActivity.this)
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
            Bitmap result = Bitmap.createBitmap(bitmap, (int) left, (int) top, (int) right - (int) left, (int) bottom - (int) top);
            img.setImageDrawable(new BitmapDrawable(getResources(), result));
            return result;
        }
    }

    List<Ticket> ticketList;

    private void loadListTicket() {
        TicketDBContext dbContext = new TicketDBContext(FaceRecognitionReceiveActivity.this);
        dbContext.reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ticketList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()
                ) {
                    Ticket ticket = dataSnapshot.getValue(Ticket.class);
                    if (!ticket.isStatus()) {
                        ticketList.add(ticket);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.ACCURATE_MODE)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory(mGraphicOverlay, FaceRecognitionReceiveActivity.this))
                        .build());
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(1024, 1280)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(30.0f)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            createCameraSource();
            return;
        }

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }


    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
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
}