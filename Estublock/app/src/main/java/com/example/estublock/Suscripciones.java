package com.example.estublock;

import android.graphics.Color;
import android.graphics.Typeface;
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

public class Suscripciones extends AppCompatActivity {

  String URL_micro = "http://192.168.1.8:10012";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_suscripciones);

    getTemasFromDatabase();
  }

  protected void getTemasFromDatabase(){
    try{
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (URL_micro + "/temas"), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              System.out.println("RESPONSE ------------------ ");
              try {
                JSONObject obj = response.getJSONObject(0);
                createListCheckBox();
              } catch (JSONException e) {
                e.printStackTrace();
              }
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          // TODO: Eliminar esto cuando funque la API
          createListCheckBox();
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

  protected void createListCheckBox(){
    System.out.println("HOLA ESTOY EN EL PUTO CREATE LIST CHECKBOX");

    CheckBox [] temas = new CheckBox[5];
    LinearLayout checkboxLayout = findViewById(R.id.chkboxlyt);
    CheckBox checkBox = new CheckBox(this);

    checkBox.setText("Scalable Systems");
    checkBox.setTextSize(10);
    checkBox.setTextColor(Color.rgb(150, 190, 200));
    checkBox.setTypeface(Typeface.MONOSPACE);
    checkBox.setButtonDrawable(R.drawable.checkboxselector);
    temas[0] = checkBox;
    checkBox.setText("Programming Project");
    checkBox.setTextSize(10);
    checkBox.setTextColor(Color.rgb(150, 190, 200));
    checkBox.setTypeface(Typeface.MONOSPACE);
    checkBox.setButtonDrawable(R.drawable.checkboxselector);
    temas[1] = checkBox;

    checkboxLayout.addView(checkBox);
    checkBox.setOnCheckedChangeListener(this::onCheckedChanged);
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