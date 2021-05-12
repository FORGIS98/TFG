package com.example.estublock;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class SeleccionTema extends AppCompatActivity implements View.OnClickListener {

  // VARIABLES GLOBALES
  HashMap<String, Integer> userTopics = new HashMap<>();
  HashMap<Integer, String> botonSeleccionado = new HashMap<>();
  GlobalState gs;
  RequestQueue requestQueue;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_seleccion_tema);

    gs = (GlobalState) getApplication();
    requestQueue = Volley.newRequestQueue(this);

    getSuscripcionesUsuario();
  }

  // Pillar las suscripciones de un usuario
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
                createButtonList(userTopics);
              } catch (JSONException e) {
                Log.e("SeTem.Catch", e.getMessage());
              }
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          Log.e("SeTem.Volley", error.getMessage());
        }
      });

      // Encolamos las llamadas a la API
      requestQueue.add(jsonObjectRequest);
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  // Creacion dinamica de botones
  public void createButtonList(HashMap<String, Integer> buttonList){
    // Pillamos al Layout donde se van a poner los botones
    LinearLayout buttonLayout = findViewById(R.id.btnlyt);

    // Recorro todos los temas a los que esta suscrito el usuario y los añado a botones
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
    }
  }

  @Override
  public void onClick(View view) {
    Log.d("SeTem.Boton", botonSeleccionado.get(view.getId()));
  }
}