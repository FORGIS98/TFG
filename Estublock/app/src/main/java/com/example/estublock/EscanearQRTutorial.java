package com.example.estublock;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.jetbrains.annotations.NotNull;

public class EscanearQRTutorial extends AppCompatActivity {

  SurfaceView surfaceView;
  TextView txtBarcodeValue;
  Button button;

  private BarcodeDetector barcodeDetector;
  private CameraSource cameraSource;
  private static final int REQUEST_CAMERA_PERMISSION = 201;

  String intentData = "";
  boolean isEmail = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_escanear_qrtutorial);

    initViews();
  }

  private void initViews(){
    txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
    surfaceView = findViewById(R.id.surfaceView);
    button = findViewById(R.id.btnAction);

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if(intentData.length() > 0){
          if(isEmail)
            System.out.println("SE HA ESCANEADO UN CORREO QUE NO VA A PASAR NUNCA");
          else
            System.out.println("SE HA ESCANEADO UN JASON COMO UNA CASA");
        }
      }
    });
  }

  private void initialiseDetectorAndSources(){
    Toast.makeText(getApplicationContext(), "Barcode Scanner Started", Toast.LENGTH_SHORT).show();

    barcodeDetector = new BarcodeDetector.Builder(this)
        .setBarcodeFormats(Barcode.QR_CODE)
        .build();

    cameraSource = new CameraSource.Builder(this, barcodeDetector)
        .setRequestedPreviewSize(1920, 1080)
        .setAutoFocusEnabled(true)
        .build();

    surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
      @Override
      public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        try{
          if(ActivityCompat.checkSelfPermission(EscanearQRTutorial.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            cameraSource.start(surfaceView.getHolder());
          else{
            ActivityCompat.requestPermissions(EscanearQRTutorial.this,
                new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
          }
        } catch (Exception e){
          e.printStackTrace();
        }
      }

      @Override
      public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {

      }

      @Override
      public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        cameraSource.stop();
      }
    });

    barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
      @Override
      public void release() {
        Toast.makeText(getApplicationContext(), "Barcode has been stopped", Toast.LENGTH_SHORT).show();
      }

      @Override
      public void receiveDetections(@NonNull @NotNull Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> barcodes = detections.getDetectedItems();
        if(barcodes.size() != 0){
          txtBarcodeValue.post(new Runnable() {
            @Override
            public void run() {
              if(barcodes.valueAt(0).email != null){
                txtBarcodeValue.removeCallbacks(null);
                intentData = barcodes.valueAt(0).email.address;
                txtBarcodeValue.setText(intentData);
                isEmail = true;
                button.setText("Add content to the email :D");
              } else {
                isEmail = false;
                button.setText("Launch url");
                intentData = barcodes.valueAt(0).displayValue;
                txtBarcodeValue.setText(intentData);
              }
            }
          });
        }
      }
    });
  }

  @Override
  protected void onPause(){
    super.onPause();
    cameraSource.release();
  }

  @Override
  protected void onResume(){
    super.onResume();
    initialiseDetectorAndSources();
  }
}