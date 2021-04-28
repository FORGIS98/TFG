package com.example.estublock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

public class LoginFragment extends Fragment {

  // URL de la API
  // String URL = "http://hubble.ls.fi.upm.es:10012";
  String URL = "http://192.168.1.8:10012";
  String URL_DB = "http://192.168.1.8:10011";

  EditText et_password;
  EditText et_email;
  public static final String et_email_key = "com.example.estublock.et_email";

  private String walletPath;
  Activity myActivity;

  public LoginFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    myActivity = this.getActivity();

    View view = inflater.inflate(R.layout.fragment_login, container, false);

    walletPath = getActivity().getApplicationInfo().dataDir;

    et_email = view.findViewById(R.id.et_email);
    et_password = view.findViewById(R.id.et_password);


    Button btn = (Button) view.findViewById(R.id.btn_login);
    btn.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v){
        loadMenu();
      }
    });
    return view;
  }

  private void loadMenu(){
    // TODO: Pillar usuario de manera juanchisima con el email
    try{
      HashMap<String, String> dataMap = new HashMap<>();
      dataMap.put("correo", et_email.getText().toString());
      dataMap.put("password", et_password.getText().toString());
      JSONObject params = new JSONObject(dataMap);

      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
          (URL + "/login"), params,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              getUserCredentials();
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error.networkResponse.statusCode == 404){
            et_email.setError(getString(R.string.error_user_not_found));
            et_password.setError(getString(R.string.error_user_not_found));
          }
          // TODO: Arreglar blockchain?
          if(error.networkResponse.statusCode == 503){
            getUserCredentials();
          }
        }
      });

      RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
      requestQueue.add(jsonObjectRequest);
    } catch(Exception e){
      System.out.println("CATCH CATCH CATCH");
      e.printStackTrace();
      System.out.println("CATCH CATCH CATCH");
    }
  }

  private void getUserCredentials(){
    try{
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (URL_DB + "/usuarios/" + et_email.getText().toString()), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              System.out.println("RESPONSE ------------------ ");
              try {
                JSONObject obj = response.getJSONObject(0);
                String name = obj.getString("Nombre");
                System.out.println(name);
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

      RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
      requestQueue.add(jsonObjectRequest);
    } catch(Exception e){
      System.out.println("CATCH CATCH CATCH");
      e.printStackTrace();
      System.out.println("CATCH CATCH CATCH");
    }
  }

}
