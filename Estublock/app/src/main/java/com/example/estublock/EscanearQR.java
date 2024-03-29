package com.example.estublock;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EscanearQR extends AppCompatActivity {

  // VARIABLES GLOBALES
  GlobalState gs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_escanear_qr);

    gs = (GlobalState) getApplication();

    Button button = findViewById(R.id.escanear_codigo_qr);
    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), EscanearQRTutorial.class);
        startActivity(intent);
      }
    });
  }
}

