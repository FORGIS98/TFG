package com.example.estublock;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;

import java.io.File;
import java.security.Provider;
import java.security.Security;
import java.util.HashMap;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterFragment extends Fragment {

  EditText et_name;
  public static final String et_name_key = "com.example.estublock.et_name";
  EditText et_email;
  public static final String et_email_key = "com.example.estublock.et_email";

  public static final String file_directory = "com.example.estublock.file_directory";

  EditText et_password;
  EditText et_repassword;

  // Creamos un progress dialog por si la API tarda más de lo normal
  ProgressDialog progressDialog;

  // Volley RequestQueue
  RequestQueue requestQueue;

  // URL de la API
  // String URL = "http://hubble.ls.fi.upm.es:10012";
  String URL = "http://192.168.1.65:10012";
  String URL_Block = "http://192.168.1.65:8545";

  Web3j web3j;
  protected Quorum quorum;
  protected File walletPath;
  protected File walletDir;

  public RegisterFragment() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_register, container, false);

    et_name = view.findViewById(R.id.et_name);
    et_email = view.findViewById(R.id.et_email);
    et_password = view.findViewById(R.id.et_password);
    et_repassword = view.findViewById(R.id.et_repassword);

    // SDK
    workaroundECDA();
    walletPath = getContext().getFilesDir();

    // Creating Volley newRequestQueue .
    requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

    // Assigning Activity this to progress dialog.
    // progressDialog = new ProgressDialog(getActivity());

    Button btn = (Button) view.findViewById(R.id.btn_register);
    btn.setOnClickListener(new View.OnClickListener(){

      @Override
      public void onClick(View v){
        registerUser();
      }
    });
    return view;
  }

  // Called when the user taps the Register Button in "fragment_register.xml"
  public void registerUser(){
    if(!checkDataEntered()){

      // progressDialog.setMessage("Cargando...");
      // progressDialog.show();

      try{
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("correo", et_email.getText().toString());
        dataMap.put("id_huella", "00000000");
        dataMap.put("matricula", "00000000");
        dataMap.put("password", et_password.getText().toString());
        dataMap.put("apellido1", "Movil");
        dataMap.put("apellido2", "Movil");
        dataMap.put("nombre", et_name.getText().toString());
        dataMap.put("wallet", createWallet(et_password.getText().toString()));

        JSONObject params = new JSONObject(dataMap);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
            (URL + "/user"), params,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
                Intent intent = new Intent(getActivity(), MenuPageActivity.class);
                intent.putExtra(et_name_key, et_name.getText().toString());
                intent.putExtra(et_email_key, et_email.getText().toString());
                intent.putExtra(file_directory, walletDir);
                startActivity(intent);

                getActivity().finish();
              }
            }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {
            Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
          }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
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

  // return 0 ==> Empty email
  // return 1 ==> Email not valid
  // return 2 ==> Good Email
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

  @SuppressLint("CheckResult")
  private String createWallet(String password){
    // web3j = Web3j.build(new HttpService("http://138.100.12.160:22000"));
    web3j = Web3j.build(new HttpService(URL_Block));
    // quorum = Quorum.build(new HttpService("http://deneb.ls.fi.upm.es:22000"));

    // Creamos el wallet al usuario
    try{
      String fileName = WalletUtils.generateLightNewWalletFile(password, new File(walletPath.toString()));
      walletDir = new File(walletPath + "/keystore/" + fileName);
    } catch (Exception e){
      e.printStackTrace();
    }

    return getAddress(password);
  }

  public void toastAsync(String message) {
    getActivity().runOnUiThread(() -> {
      Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    });
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
      toastAsync(e.getMessage());
      return "";
    }
  }
}
