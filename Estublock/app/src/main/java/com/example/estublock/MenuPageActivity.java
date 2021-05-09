package com.example.estublock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuPageActivity extends AppCompatActivity {

  GlobalState gs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu_page);

    gs = (GlobalState) getApplication();
    String name = gs.getUserName();
    TextView userName = findViewById(R.id.nombre_logueado);
    userName.setText(name);

    Button btn = findViewById(R.id.mis_asignaturas);
    btn.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        loadSuscripciones();
      }
    });
  }

  protected void loadSuscripciones(){
    Intent subIntent = new Intent(this, UserSuscripciones.class);
    startActivity(subIntent);
    this.finish();
  }
} // END - public class MenuPage