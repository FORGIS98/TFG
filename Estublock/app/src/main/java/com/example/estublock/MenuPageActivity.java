package com.example.estublock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.estublock.LoginFragment.name_login;
import static com.example.estublock.RegisterFragment.name_register;

public class MenuPageActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu_page);

    Intent intent = getIntent();
    String name = "";
    if(intent.hasExtra(name_login)){
      name = intent.getStringExtra(name_login);
    }else{
      name = intent.getStringExtra(name_register);
    }

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
    Intent subIntent = new Intent(this, Suscripciones.class);
    startActivity(subIntent);
    this.finish();
  }
} // END - public class MenuPage