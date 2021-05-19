package com.example.estublock;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatActivity;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class EscanearQRTutorial extends AppCompatActivity {

  // VARIABLES GLOBALES
  RequestQueue requestQueue;
  GlobalState gs;
  Web3j web3j;

  SurfaceView surfaceView;
  TextView txtBarcodeValue;
  Button button;

  private BarcodeDetector barcodeDetector;
  private CameraSource cameraSource;
  private static final int REQUEST_CAMERA_PERMISSION = 201;

  String intentData = "";
  String oldIntentData = "";
  boolean isEmail = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_escanear_qrtutorial);

    gs = (GlobalState) getApplication();
    requestQueue = Volley.newRequestQueue(this);

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
          System.out.println("EL JASON QUE HEMOS LIDO ES: ");
          System.out.println(intentData);
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
              button.setText("LISTO");
              intentData = barcodes.valueAt(0).displayValue;
              if(!intentData.equals(oldIntentData)){
                oldIntentData = intentData;
                askForTransaction(intentData);
              }
            }
          });
        }
      }
    }); // END - new Detector.Processor
  } // END - initialiseDetectorAndSources


  protected void askForTransaction(String intentData){
    try {

      System.out.println("EL JSON QUE ESTOY CREANDO ES EL SIGUIENTE: ");
      System.out.println(intentData);
      JSONObject data = new JSONObject(intentData);

      Credentials credentials = WalletUtils.loadCredentials(gs.getUserPassword(), gs.getPathToWallet());
      String address = credentials.getAddress();

      // Preparamos el JSON
      HashMap<String, Object> dataMap = new HashMap<>();
      dataMap.put("validator", address);
      dataMap.put("account", data.getString("UserWalletAddress"));

      JSONObject params = new JSONObject(dataMap);
      System.out.println("EL JSON QUE ENVIO ES: ");
      System.out.println(params);

      // Se hace la llamada POST para que nos preparen la transaccion que luego firmaremos
      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
          (gs.getMicro_URL() + "/event/" + data.getInt("id") + "/attendance"), params,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              sendTransactionThread(response);
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          error.printStackTrace();
        }
      });

      // Encolamos la llamada
      requestQueue.add(jsonObjectRequest);
    }catch(Exception e){
      e.printStackTrace();
    }
  }


  protected void sendTransactionThread(JSONObject tx){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          web3j = Web3j.build(new HttpService(gs.getQuorum_RPC()));
          Credentials credentials = WalletUtils.loadCredentials(gs.getUserPassword(), gs.getPathToWallet());


          EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
          BigInteger nonce = ethGetTransactionCount.getTransactionCount();

          RawTransaction rawTransaction = RawTransaction.createTransaction(
              nonce,
              (BigInteger) Numeric.toBigInt((String) tx.get("gasPrice")),
              (BigInteger) Numeric.toBigInt((String) tx.get("gas")),
              (String) tx.get("to"),
              (String) tx.get("data")
          );

          byte [] signedTx = TransactionEncoder.signMessage(rawTransaction, credentials);
          String hexSignedMessage = Numeric.toHexString(signedTx);

          EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexSignedMessage).send();
          String transactionHash = ethSendTransaction.getTransactionHash();
          System.out.println("LA TRANSACCIONES QUE SE HA ENVIADO TIENE ESTE HASH:");
          System.out.println(transactionHash);



        } catch (IOException e) {
          e.printStackTrace();
        } catch (CipherException e) {
          e.printStackTrace();
        } catch (JSONException e) {
          e.printStackTrace();
        }

      }
    }).start();
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