package com.example.estublock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.easyblockchain.WalletHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class LoginFragment extends Fragment {

  // VARIABLES GLOBALES
  RequestQueue requestQueue;
  String walletPath;
  GlobalState gs;
  WalletHelper walletHelper;

  // ITEMS DEL LAYOUT
  EditText et_password;
  EditText et_email;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_login, container, false);


    // Recupero el objeto GlobalState
    gs = (GlobalState) this.getActivity().getApplication();
    walletHelper = new WalletHelper();
    // Recupero el directorio con los keystore
    walletPath = getActivity().getApplicationInfo().dataDir;
    walletPath += "/files";
    // Preparamos a Volley para llamadas a la API
    requestQueue = Volley.newRequestQueue(this.getContext());

    et_email = view.findViewById(R.id.et_email);
    et_password = view.findViewById(R.id.et_password);

    // Boton de hacer LOGIN
    Button btn = (Button) view.findViewById(R.id.btn_login);
    btn.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        loadMenu();
      }
    });
    return view;
  }

  protected void loadMenu(){
    try{
      // Creamos el json a partir de un HashMap
      HashMap<String, String> dataMap = new HashMap<>();
      dataMap.put("correo", et_email.getText().toString());
      dataMap.put("password", et_password.getText().toString());
      JSONObject params = new JSONObject(dataMap);

      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
          (gs.getMicro_URL() + "/login"), params,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              // Hasta que la blockchain no deje de devolver 503
              // esto de aquí no se ejecuta, mirar más abajo en el 503
              // Recupero el nombre del usuario
              gs.setUserName("");
              getUserCredentials();
              Intent intent = new Intent(getActivity(), MenuPageActivity.class);
              // Guardamos los datos que nos interesan del usuario
              gs.setUserEmail(et_email.getText().toString());
              gs.setUserPassword(et_password.getText().toString());
              // gs.setPathToWallet(getTheWalletFile());
              gs.setPathToWallet(new File(walletHelper.getFromPreferences(et_email.getText().toString(), gs.getMyPref(), getActivity())));
              startActivity(intent);
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          if(error.networkResponse == null){
            error.printStackTrace();
          }
          else if(error.networkResponse.statusCode == 404){
            // 404 es que el usuario no se encuentra
            et_email.setError(getString(R.string.error_user_not_found));
            et_password.setError(getString(R.string.error_user_not_found));
          }
          else if(error.networkResponse.statusCode == 503){
            // La blockchain devuelve 503 siempre
            // Recupero el nombre del usuario
            String name = getUserCredentials();
            Intent intent = new Intent(getActivity(), MenuPageActivity.class);
            // Guardamos los datos que nos interesan del usuario
            gs.setUserEmail(et_email.getText().toString());
            gs.setUserName(name);
            gs.setUserPassword(et_password.getText().toString());
            // gs.setPathToWallet(getTheWalletFile());
            gs.setPathToWallet(new File(walletHelper.getFromPreferences(et_email.getText().toString(), gs.getMyPref(), getActivity())));
            startActivity(intent);
          }
          else{
            error.printStackTrace();
          }
        }
      });

      // Encolamos la llamada para que se haga de forma async
      requestQueue.add(jsonObjectRequest);
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  // Llamada que devuelve el nombre del usuario que esta haciendo login
  protected String getUserCredentials(){
    final String[] name = {"nombre 404"};
    try{
      // JsonArray porque la API devuelve un array en vez de un Objeto json
      JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
          (gs.getDataBase_URL() + "/usuarios/" + et_email.getText().toString()), null,
          new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
              try {
                JSONObject obj = response.getJSONObject(0);
                name[0] = obj.getString("Nombre");
                gs.setUserName(name[0]);
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

      // Encolamos la llamada para que sea aync
      requestQueue.add(jsonArrayRequest);
    } catch(Exception e){
      e.printStackTrace();
    }

    return name[0];
  }

  // Devuelve el path hasta el keystore, que tiene que existir si o si o peta por NULL
  // TODO: Que no pete por que el archivo no exista
  protected File getTheWalletFile(){
    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(gs.getMyPref(), Context.MODE_PRIVATE);
    File keystore = new File(sharedPreferences.getString(gs.getUserEmail(), null));

    return keystore;
  }
}
