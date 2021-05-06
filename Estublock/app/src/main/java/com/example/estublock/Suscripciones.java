package com.example.estublock;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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

public class Suscripciones extends AppCompatActivity {

  String URL_mic = "http://hubble.ls.fi.upm.es:10012";
  String URL_bdd = "http://hubble.ls.fi.upm.es:10011";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_suscripciones);

    getTemasFromDatabase();
  }

  protected void getTemasFromDatabase(){
    try{
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (URL_bdd + "/temas"), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              System.out.println("RESPONSE ------------------ ");
              try {
                HashMap<Integer, String> temas = new HashMap<>();
                for(int i = 0; i < response.length(); i++){
                  temas.put((int) response.getJSONObject(i).get("TemaId"), (String) response.getJSONObject(i).get("Nombre"));
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
      System.out.println("CATCH CATCH CATCH");
      e.printStackTrace();
      System.out.println("CATCH CATCH CATCH");
    }
  }

  protected void createListCheckBox(HashMap<Integer, String> temasHashMap, int amount){
    System.out.println("Generando CHECKBOXES");

    LinearLayout checkboxLayout = findViewById(R.id.chkboxlyt);
    CheckBox [] dynamicCheckbox = new CheckBox[amount];

    int i = 0;
    for (Map.Entry<Integer, String> entry : temasHashMap.entrySet()) {
      Integer key = entry.getKey();
      String value = entry.getValue();
      CheckBox checkBox = new CheckBox(this);
      checkBox.setText(value);
      checkBox.setTextSize(10);
      checkBox.setTextColor(Color.rgb(150, 190, 200));
      checkBox.setTypeface(Typeface.MONOSPACE);
      checkBox.setButtonDrawable(R.drawable.checkboxselector);
      dynamicCheckbox[i] = checkBox;
      checkboxLayout.addView(checkBox);
      checkBox.setOnCheckedChangeListener(this::onCheckedChanged);

      i += 1;
    }
  }

  public void onCheckedChanged(CompoundButton cb, boolean isChecked){
    String checkedText = cb.getText()+"";

    if(isChecked){
      Toast.makeText(this, cb.getText()+" is checked!!!", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(this, cb.getText()+" is not checked!!!", Toast.LENGTH_SHORT).show();
    }
  }
}