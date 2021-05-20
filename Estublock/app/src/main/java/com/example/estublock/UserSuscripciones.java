package com.example.estublock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class UserSuscripciones extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

  // VARIABLES GLOBALES
  ArrayList<String> topicsToBeDeleted = new ArrayList<>();
  HashMap<String, Integer> userTopics = new HashMap<>();

  Button addTopics;
  Button deleteTopics;

  // OkHttp no es listo y no entiende un JSONObject, tiene que ser un RequestBody
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  OkHttpClient client;
  RequestQueue requestQueue;
  GlobalState gs;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_suscripciones);

    gs = (GlobalState) getApplication();
    requestQueue = Volley.newRequestQueue(this);
    client = new OkHttpClient();

    addTopics = (Button) findViewById(R.id.addTopics);
    deleteTopics = (Button) findViewById(R.id.delete);

    // Mandar al usuario a Suscripciones para añadir nuevas suscripciones.
    addTopics.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        Intent intent = new Intent(UserSuscripciones.this, Suscripciones.class);
        startActivity(intent);
      }
    });

    // Eliminar suscripciones de la BDD y luego devolverlo al Menu
    deleteTopics.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        updateTopicsDatabase();
        Intent intent = new Intent(UserSuscripciones.this, MenuPageActivity.class);
        startActivity(intent);
      }
    });

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
                createListCheckBox(userTopics);
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

      requestQueue.add(jsonObjectRequest);

    } catch(Exception e){
      e.printStackTrace();
    }
  }

  // Se crea una lista para que el usuario pueda decidir que borrar
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

  public void onCheckedChanged(CompoundButton checkbox, boolean isChecked){
    String checkedText = checkbox.getText() + "";

    if(isChecked){
      topicsToBeDeleted.add(checkedText);
    } else {
      topicsToBeDeleted.remove(checkedText);
    }
  }

  // Aquí se llega cundo el usuario le da a Eliminar
  // En la variable topicsToBeDeleted estan los topics que quiere eliminar
  // Usamos okhttp por que la libreria Volley funciona como y cuando quiere :))
  public void updateTopicsDatabase(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put("correo", gs.getUserEmail());

        for (String tema : topicsToBeDeleted) {
          try{
            dataMap.put("topic", userTopics.get(tema));
            JSONObject params = new JSONObject(dataMap);

            okhttp3.RequestBody body = okhttp3.RequestBody.create(params.toString(), JSON);

            okhttp3.Request request = new okhttp3.Request.Builder()
                .url(gs.getMicro_URL() + "/subscription")
                .delete(body)
                .build();

            okhttp3.Response response = client.newCall(request).execute();
          } catch(Exception e){
            e.printStackTrace();
          }
        } // END - forEach()
      }
    }).start();
  }
}