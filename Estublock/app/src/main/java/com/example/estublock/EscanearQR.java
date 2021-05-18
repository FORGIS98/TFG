package com.example.estublock;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EscanearQR extends AppCompatActivity {

  // VARIABLES GLOBALES
  GlobalState gs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_escanear_qr);

    gs = (GlobalState) getApplication();
  }
}