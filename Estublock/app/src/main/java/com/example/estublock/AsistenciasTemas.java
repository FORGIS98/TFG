package com.example.estublock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
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

public class AsistenciasTemas extends AppCompatActivity implements View.OnClickListener {

  // VARIABLES GLOBALES
  public static final String ID_TEMA_ELEGIDO_ASISTENCIAS = "com.exmaple.estublock.idTemaElegido.asistencias";
  public static final String STRING_TEMA_ELEGIDO_ASISTENCIAS = "com.exmaple.estublock.stringTemaElegido.asistencias";
  HashMap<String, Integer> userTopics = new HashMap<>();
  RequestQueue requestQueue;
  GlobalState gs;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_asistencias_temas);

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
                e.printStackTrace();
              }
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          error.printStackTrace();
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

      Button button = new Button(this);
      button.setText(key);
      button.setTextSize(27);
      button.setTextColor(Color.rgb(150, 190, 200));
      button.setTypeface(Typeface.MONOSPACE);
      button.setTag(key);

      buttonLayout.addView(button);
      button.setOnClickListener(this);
    }
  }

  @Override
  public void onClick(View view) {
    Intent intent = new Intent(view.getContext(), AsistenciasEventos.class);
    intent.putExtra(ID_TEMA_ELEGIDO_ASISTENCIAS, userTopics.get(view.getTag().toString()));
    intent.putExtra(STRING_TEMA_ELEGIDO_ASISTENCIAS, view.getTag().toString());
    startActivity(intent);
  }
}