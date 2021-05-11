package com.example.estublock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import java.io.File;
import java.util.HashMap;

public class LoginFragment extends Fragment {

  // URL de la API
  String URL = "http://hubble.ls.fi.upm.es:10012";
  String URL_DB = "http://hubble.ls.fi.upm.es:10011";

  EditText et_password;
  EditText et_email;
  public static final String email_login = "com.example.estublock.et_email";
  public static final String name_login = "com.example.estublock.et_name";

  private String walletPath;
  Activity myActivity;
  GlobalState gs;

  public LoginFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    myActivity = this.getActivity();

    View view = inflater.inflate(R.layout.fragment_login, container, false);

    walletPath = getActivity().getApplicationInfo().dataDir;
    walletPath += "/files";


    gs = (GlobalState) this.getActivity().getApplication();

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
              String name = getUserCredentials();
              Intent intent = new Intent(getActivity(), MenuPageActivity.class);
              gs.setUserEmail(et_email.getText().toString());
              gs.setUserName(name);
              gs.setUserPassword(et_password.getText().toString());
              gs.setPathToWallet(getTheWalletFile());
              startActivity(intent);

              getActivity().finish();
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error.networkResponse == null){
            Log.d("ERROR: ", String.valueOf(error));
          }
          else if(error.networkResponse.statusCode == 404){
            et_email.setError(getString(R.string.error_user_not_found));
            et_password.setError(getString(R.string.error_user_not_found));
          }
          // TODO: Arreglar blockchain?
          else if(error.networkResponse.statusCode == 503){
            String name = getUserCredentials();
            Intent intent = new Intent(getActivity(), MenuPageActivity.class);
            gs.setUserEmail(et_email.getText().toString());
            gs.setUserName(name);
            gs.setUserPassword(et_password.getText().toString());
            gs.setPathToWallet(getTheWalletFile());
            startActivity(intent);

            getActivity().finish();
          }
          else{
            Log.d("ERROR: ", String.valueOf(error));
          }
        }
      });

      RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
      requestQueue.add(jsonObjectRequest);
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  private String getUserCredentials(){
    final String[] name = {""};
    try{
      JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET,
          (URL_DB + "/usuarios/" + et_email.getText().toString()), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              try {
                JSONObject obj = response.getJSONObject(0);
                name[0] = obj.getString("Nombre");
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
      e.printStackTrace();
    }

    return name[0];
  }

  // TODO: Pillar el archivo donde esta la keystore :D
  protected File getTheWalletFile(){
    System.out.println("El path donde esta el wallet es: " + walletPath);
    File[] listfiles = new File(walletPath).listFiles();
    return listfiles[0];
  }
}
