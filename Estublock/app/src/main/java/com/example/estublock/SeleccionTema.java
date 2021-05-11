package com.example.estublock;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.web3j.abi.datatypes.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeleccionTema extends AppCompatActivity implements View.OnClickListener {

  // ----- Variables Globales ----- //
  HashMap<String, Integer> userTopics = new HashMap<String, Integer>();
  HashMap<Integer, String> botonSeleccionado = new HashMap<Integer, String>();
  GlobalState gs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_seleccion_tema);

    gs = (GlobalState) getApplication();

    getSuscripcionesUsuario();
  }

  // ----- Pillar las suscripciones de un usuario ----- //
  public void getSuscripcionesUsuario(){
    try{
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (gs.getMicro_URL() + "/user/" + gs.getUserEmail() + "/subscription"), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              try {
                for(int i = 0; i < response.length(); i++){
                  userTopics.put((String) response.getJSONObject(i).get("Nombre"), (int) response.getJSONObject(i).get("TemaId"));
                }
                createButtonList(userTopics, response.length());
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          System.out.println(error);
        }
      });

      RequestQueue requestQueue = Volley.newRequestQueue(this);
      requestQueue.add(jsonObjectRequest);

    } catch(Exception e){
      e.printStackTrace();
    }
  }

  public void createButtonList(HashMap<String, Integer> buttonList, int amount){
    LinearLayout buttonLayout = findViewById(R.id.btnlyt);
    System.out.println("----- SeleccionTema.createButtonlist() -----");

    int i = 0;
    for (Map.Entry<String, Integer> entry : buttonList.entrySet()) {
      String key = entry.getKey();
      System.out.println(key);

      Button button = new Button(this);
      button.setText(key);
      button.setTextSize(27);
      button.setTextColor(Color.rgb(150, 190, 200));
      button.setTypeface(Typeface.MONOSPACE);
      botonSeleccionado.put(button.getId(), key);

      buttonLayout.addView(button);
      button.setOnClickListener(this);

      i += 1;
    }
  }

  @Override
  public void onClick(View view) {
    System.out.println(" ----- SeleccionTema.onClick() -----");
    System.out.println(botonSeleccionado.get(view.getId()));
  }
}