package com.example.estublock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Suscripciones extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

  // VARIABLES FLOBALES
  ArrayList <String> temasElegidos = new ArrayList<>();
  HashMap<String, Integer> temas = new HashMap<>();

  RequestQueue requestQueue;
  GlobalState gs;

  Button saveTemas;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_suscripciones);

    gs = (GlobalState) getApplication();
    requestQueue = Volley.newRequestQueue(this);

    // Boton para guardar los temas seleccionados y guardarlos en la BDD
    saveTemas = (Button) findViewById(R.id.saveProceed);
    saveTemas.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v){
        saveTopicsToDatabase();
        Intent intent = new Intent(Suscripciones.this, MenuPageActivity.class);
        startActivity(intent);
      }
    });
    getTemasFromDatabase();
  }

  // Recuperar todos los temas disponibles
  protected void getTemasFromDatabase(){
    try{
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (gs.getDataBase_URL() + "/temas"), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              try {
                for(int i = 0; i < response.length(); i++){
                  temas.put((String) response.getJSONObject(i).get("Nombre"), (int) response.getJSONObject(i).get("TemaId"));
                }
                createListCheckBox(temas);
              } catch (JSONException e) {
                Log.e("Susc.Catch", e.getMessage());
              }
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          Log.e("Susc.Volley", error.getMessage());
        }
      });

      requestQueue.add(jsonObjectRequest);
    } catch(Exception e){
      Log.e("Susc.Catch", e.getMessage());
    }
  }

  // Creamos una lista con todos los temas que no devulve la base de datos
  protected void createListCheckBox(HashMap<String, Integer> temasHashMap){
    LinearLayout checkboxLayout = findViewById(R.id.chkboxlyt);

    for (Map.Entry<String, Integer> entry : temasHashMap.entrySet()) {
      String key = entry.getKey();
      CheckBox checkBox = new CheckBox(this);
      checkBox.setText(key);
      checkBox.setTextSize(27);
      checkBox.setTextColor(Color.rgb(150, 190, 200));
      checkBox.setTypeface(Typeface.MONOSPACE);
      checkBox.setButtonDrawable(R.drawable.checkboxselector);

      checkboxLayout.addView(checkBox);
      checkBox.setOnCheckedChangeListener(this);

    }
  }

  // Añadie o quita del ArrayList los temas seleccionados
  public void onCheckedChanged(CompoundButton checkbox, boolean isChecked){
    String checkedText = checkbox.getText() + "";

    if(isChecked){
      temasElegidos.add(checkedText);
    } else {
      temasElegidos.remove(checkedText);
    }
  }

  // Guarda los temas elegidos en la BDD
  protected void saveTopicsToDatabase(){
    // Creamos el json con un hashmap
    HashMap<String, Object> dataMap = new HashMap<>();
    dataMap.put("correo", gs.getUserEmail());

    // Iteramos en los temas para meterlos de uno en uno
    for (String tema : temasElegidos) {
      try{
        dataMap.put("topic", temas.get(tema));
        JSONObject params = new JSONObject(dataMap);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
            (gs.getMicro_URL() + "/subscription"), params,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
              }
            }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.e("ERROR: ", String.valueOf(error));
          }
        });

        requestQueue.add(jsonObjectRequest);

      } catch(Exception e){
        Log.e("Susc.Catch", e.getMessage());
      }
    } // END - forEach()
  }
}