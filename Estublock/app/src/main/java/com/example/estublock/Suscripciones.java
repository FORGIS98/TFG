package com.example.estublock;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class Suscripciones extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

  int flag = 0;
  ArrayList <String> temasElegidos = new ArrayList<String>();
  ArrayList <String> recuperarTemasElegidos;
  Button saveTemas;
  public static final String temas_elegidos = "com.example.estublock.temas_elegidos";
  GlobalState gs;
  // La variable temas para todos los temas que devuelve la BDD
  HashMap<String, Integer> temas = new HashMap<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_suscripciones);

    gs = (GlobalState) getApplication();

    if(savedInstanceState != null){
      recuperarTemasElegidos = savedInstanceState.getStringArrayList(temas_elegidos);
      flag = savedInstanceState.getInt("savedFlag");
    }

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
                createListCheckBox(temas, response.length());
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
    CheckBox [] dynamicCheckbox = new CheckBox[amount];

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

    if(flag != 0){
      for(CheckBox checkBox: dynamicCheckbox){
        for(i = 0; i < recuperarTemasElegidos.size(); i++){
          if((checkBox.getText() + "").equals(recuperarTemasElegidos.get(i))){
            checkBox.toggle();
          }
        }
      }
    }
  }

  public void onCheckedChanged(CompoundButton checkbox, boolean isChecked){
    String checkedText = checkbox.getText() + "";

    if(isChecked){
      temasElegidos.add(checkedText);
      Toast.makeText(this, checkbox.getText()+" is checked!!!", Toast.LENGTH_SHORT).show();
    } else {
      temasElegidos.remove(checkedText);
      Toast.makeText(this, checkbox.getText()+" is not checked!!!", Toast.LENGTH_SHORT).show();
    }
  }

  public void onSaveInstanceState(Bundle savedState){
    super.onSaveInstanceState(savedState);
    flag = 1;
    savedState.putStringArrayList(temas_elegidos, temasElegidos);
    savedState.putInt("savedFlag", flag);
  }

  protected void saveTopicsToDatabase(){
    HashMap<String, Object> dataMap = new HashMap<>();
    dataMap.put("correo", gs.getUserEmail());

    RequestQueue requestQueue = Volley.newRequestQueue(this);
    for (String tema : temasElegidos) {
      try{
        dataMap.put("topic", temas.get(tema));
        JSONObject params = new JSONObject(dataMap);

        System.out.println(" ----- Suscripciones.saveTopicsToDatabase ----- ");
        System.out.println(params);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
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

        requestQueue.add(jsonObjectRequest);
      } catch(Exception e){
        e.printStackTrace();
      }
    } // END - forEach()
  }
}