package com.example.estublock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.estublock.AsistenciasTemas.ID_TEMA_ELEGIDO_ASISTENCIAS;
import static com.example.estublock.SeleccionTema.ID_TEMA_ELEGIDO;

public class AsistenciasEventos extends AppCompatActivity implements View.OnClickListener {

  // VARIABLES GLOBALES
  public static final String EVENTO_INFO_COMPLETA = "com.exmaple.estublock.info.completa";
  HashMap<Integer, JSONObject> eventsFullInfo = new HashMap<>();
  RequestQueue requestQueue;
  GlobalState gs;
  int idTema = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_asistencias_eventos);

    gs = (GlobalState) getApplication();
    requestQueue = Volley.newRequestQueue(this);

    Intent intent = getIntent();
    idTema = intent.getIntExtra(ID_TEMA_ELEGIDO_ASISTENCIAS, -1);

    getEventosTema();
  }

  public void getEventosTema() {
    try {
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (gs.getMicro_URL() + "/topic/" + idTema + "/event"), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              try {
                for(int i = 0; i < response.length(); i++){
                  eventsFullInfo.put((Integer) response.getJSONObject(i).get("id"), (JSONObject) response.getJSONObject(i));
                }
                createButtonList(eventsFullInfo);
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
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  // Creacion dinamica de botones
  public void createButtonList(HashMap<Integer, JSONObject> buttonList){
    // Pillamos al Layout donde se van a poner los botones
    LinearLayout buttonLayout = findViewById(R.id.btnlyt);

    // Recorro todos los temas a los que esta suscrito el usuario y los añado a botones
    try {
      for (Map.Entry<Integer, JSONObject> entry : buttonList.entrySet()) {
        Integer id = entry.getKey();
        JSONObject value = entry.getValue();

        String textView = (String) value.get("title");
        textView += String.valueOf(value.get("date"));

        Button button = new Button(this);
        button.setText(textView);
        button.setTextSize(27);
        button.setTextColor(Color.rgb(150, 190, 200));
        button.setTypeface(Typeface.MONOSPACE);
        button.setTag(id);

        buttonLayout.addView(button);
        button.setOnClickListener(this);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  @Override
  public void onClick(View view) {
    Intent intent = new Intent(view.getContext(), GenerarQR.class);
    intent.putExtra(EVENTO_INFO_COMPLETA, eventsFullInfo.get(view.getTag()).toString());
    startActivity(intent);
  }
}