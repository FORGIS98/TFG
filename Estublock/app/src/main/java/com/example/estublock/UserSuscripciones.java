package com.example.estublock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

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

public class UserSuscripciones extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

  // ----- Variables Globales ----- //
  ArrayList<String> topicsToBeDeleted = new ArrayList<String>();
  HashMap<String, Integer> userTopics = new HashMap<String, Integer>();

  Button addTopics;
  Button deleteTopics;
  GlobalState gs;


  // ----- onCreate() ----- //
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_user_suscripciones);

    gs = (GlobalState) getApplication();

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

  // ----- Pillar las suscripciones de un usuario ----- //
  public void getSuscripcionesUsuario(){
    try{
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (gs.getDataBase_URL() + "/temas"), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              try {
                for(int i = 0; i < response.length(); i++){
                  userTopics.put((String) response.getJSONObject(i).get("Nombre"), (int) response.getJSONObject(i).get("TemaId"));
                }
                createListCheckBox(userTopics, response.length());
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

  protected void createListCheckBox(HashMap<String, Integer> temasHashMap, int amount){
    LinearLayout checkboxLayout = findViewById(R.id.chkboxlyt);
    CheckBox[] dynamicCheckbox = new CheckBox[amount];

    int i = 0;
    for (Map.Entry<String, Integer> entry : temasHashMap.entrySet()) {
      String key = entry.getKey();
      CheckBox checkBox = new CheckBox(this);
      checkBox.setText(key);
      checkBox.setTextSize(27);
      checkBox.setTextColor(Color.rgb(150, 190, 200));
      checkBox.setTypeface(Typeface.MONOSPACE);
      checkBox.setButtonDrawable(R.drawable.checkboxselector);

      dynamicCheckbox[i] = checkBox;
      checkboxLayout.addView(checkBox);
      checkBox.setOnCheckedChangeListener(this);

      i += 1;
    }
  }

  public void onCheckedChanged(CompoundButton checkbox, boolean isChecked){
    String checkedText = checkbox.getText() + "";

    if(isChecked){
      topicsToBeDeleted.add(checkedText);
      Toast.makeText(this, checkbox.getText()+" is checked!!!", Toast.LENGTH_SHORT).show();
    } else {
      topicsToBeDeleted.remove(checkedText);
      Toast.makeText(this, checkbox.getText()+" is not checked!!!", Toast.LENGTH_SHORT).show();
    }
  }

  // Aquí se llega cundo el usuario le da a Eliminar
  // En la variable topicsToBeDeleted estan los topics que quiere eliminar
  public void updateTopicsDatabase(){
    HashMap<String, Object> dataMap = new HashMap<>();
    dataMap.put("correo", gs.getUserEmail());

    for (String tema : topicsToBeDeleted) {
      System.out.println("UPDATE TOPICS DATABASE: ");
      try{
        dataMap.put("topic", userTopics.get(tema));
        JSONObject params = new JSONObject(dataMap);
        System.out.println("UPDATE TOPICS DATABASE: ");
        System.out.println(params);
        System.out.println(gs.getMicro_URL() + "/subscription");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE,
            (gs.getMicro_URL() + "/subscription"), params,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
              }
            }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Log.d("ERROR: ", String.valueOf(error));
          }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
      } catch(Exception e){
        e.printStackTrace();
      }
    } // END - forEach()
  }
}