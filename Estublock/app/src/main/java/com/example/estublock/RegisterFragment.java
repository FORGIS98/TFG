package com.example.estublock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import java.io.File;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterFragment extends Fragment {

  // VARIABLES GLOBALES
  RequestQueue requestQueue;
  GlobalState gs;
  Web3j web3j;

  String walletPath;
  File walletDir;

  EditText et_name;
  EditText et_email;
  EditText et_password;
  EditText et_repassword;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {


    View view = inflater.inflate(R.layout.fragment_register, container, false);

    et_name = view.findViewById(R.id.et_name);
    et_email = view.findViewById(R.id.et_email);
    et_password = view.findViewById(R.id.et_password);
    et_repassword = view.findViewById(R.id.et_repassword);

    gs = (GlobalState) this.getActivity().getApplication();

    workaroundECDA();
    walletPath = getContext().getFilesDir().getAbsolutePath();
    walletDir = new File(walletPath);

    requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

    // Boton para registrar al usuario en la BDD
    Button btn = (Button) view.findViewById(R.id.btn_register);
    btn.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v){
        registerUser();
      }
    });
    return view;
  }

  public void registerUser(){
    // Si la info metida esta bien (osea, correo de la uni, password repetido...)
    if(!checkDataEntered()){
      try{
        // Preparamos el json con un HashMap
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("correo", et_email.getText().toString());
        dataMap.put("id_huella", "00000000");
        dataMap.put("matricula", "00000000");
        dataMap.put("password", hashPassword(et_password.getText().toString()));
        dataMap.put("apellido1", "Movil");
        dataMap.put("apellido2", "Movil");
        dataMap.put("nombre", et_name.getText().toString());
        dataMap.put("wallet", createWallet(et_password.getText().toString()));

        JSONObject params = new JSONObject(dataMap);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
            (gs.getMicro_URL() + "/user"), params,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {

                gs.setUserEmail(et_email.getText().toString());
                gs.setUserName(et_name.getText().toString());
                gs.setUserPassword(et_password.getText().toString());
                gs.setPathToWallet(walletDir);

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(gs.getMyPref(), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(et_email.getText().toString(), walletDir.toString());
                editor.commit();
                Toast.makeText(getContext(),"Thanks",Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getActivity(), MenuPageActivity.class);
                startActivity(intent);
              }
            }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            error.printStackTrace();
          }
        });

        requestQueue.add(jsonObjectRequest);
      } catch (Exception e){
        e.printStackTrace();
      }
    }
  }

  private boolean checkDataEntered(){
    boolean error = false;
    if(isEmpty(et_name)){
      et_name.setError(getString(R.string.empty_name));
      error = true;
    }
    switch(isEmail(et_email)){
      case 0:
        et_email.setError(getString(R.string.empty_email));
        error = true;
        break;
      case 1:
        et_email.setError(getString(R.string.not_valid_email));
        error = true;
        break;
    }
    if(!et_password.getText().toString().equals(et_repassword.getText().toString()) && !isEmpty(et_password)){
      et_repassword.setError(getString(R.string.password_missmatch));
      error = true;
    }
    return error;
  }

  // return 0 ==> Email vacio
  // return 1 ==> Email no valido (por regex)
  // return 2 ==> Buen email
  private int isEmail(EditText email){
    CharSequence str = email.getText().toString();
    String regex = "^[-a-z0-9~!$%^&*_=+}{'?]+(\\.[-a-z0-9~!$%^&*_=+}{'?]+)*" +
        "@(alumnos.upm.es|fi.upm.es|upm.es)$";
    if(TextUtils.isEmpty(str))
      return 0;
    else if(!str.toString().matches(regex))
      return 1;
    else
      return 2;
  }

  private boolean isEmpty(EditText txt){
    CharSequence str = txt.getText().toString();
    return TextUtils.isEmpty(str);
  }

  private String createWallet(String password){
    web3j = Web3j.build(new HttpService(gs.getQuorum_RPC()));
    try{
      // Cremos el wallet en la carpeta files que hemos definido en la variable walletDir
      String fileName = WalletUtils.generateLightNewWalletFile(password, walletDir);
      walletDir = new File(walletPath + "/" + fileName);
    } catch (Exception e){
      Log.e("ReFra.Catch", e.getMessage());
    }
    // Recuperamos el address del wallet para la BDD
    return getAddress(password);
  }

  // https://github.com/web3j/web3j/issues/915
  private void workaroundECDA(){
    final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    if(provider != null || !provider.getClass().equals(BouncyCastleProvider.class)){
      Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
      Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
  }

  public String getAddress(String password){
    try {
      Credentials credentials = WalletUtils.loadCredentials(password, walletDir);
      return credentials.getAddress();
    }
    catch (Exception e){
      Log.e("LoFra.Catch", e.getMessage());
      return "";
    }
  }

  // Hasheamos el password con la libreria BCrypt
  protected String hashPassword(String password){
    return BCrypt.withDefaults().hashToString(10, password.toCharArray());
  }
}
