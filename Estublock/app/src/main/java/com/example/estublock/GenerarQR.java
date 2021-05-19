package com.example.estublock;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static com.example.estublock.AsistenciasEventos.EVENTO_INFO_COMPLETA;

public class GenerarQR extends AppCompatActivity {

  // VARIABLES GLOBALES
  RequestQueue requestQueue;
  GlobalState gs;
  JSONObject evento;

  public TextView titulo_evento;
  public ImageView qr;
  public Bitmap bitmap;
  public QRGEncoder qrgEncoder;
  public AppCompatActivity activity;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_qr);

    gs = (GlobalState) getApplication();
    requestQueue = Volley.newRequestQueue(this);
    qr = findViewById(R.id.qr_image);
    titulo_evento = findViewById(R.id.titulo_evento);


    Intent intent = getIntent();
    try {
      evento = new JSONObject(intent.getStringExtra(EVENTO_INFO_COMPLETA));
      titulo_evento.setText((String) evento.get("title"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    crearQR();
  }

  public void crearQR(){
    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
    Display display = manager.getDefaultDisplay();
    Point point = new Point();
    display.getSize(point);

    int w = point.x;
    int h = point.y;

    System.out.println("EL PUNTO W ES: " + w);
    System.out.println("EL PUNTO H ES: " + h);

    int smallerDimension = Math.min(w, h);

    qrgEncoder = new QRGEncoder(evento.toString(), null, QRGContents.Type.TEXT, smallerDimension);
    try{
      bitmap = qrgEncoder.getBitmap();
      qr.setImageBitmap(bitmap);
    } catch (Exception e){
      e.printStackTrace();
    }
  }
}