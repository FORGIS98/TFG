package com.example.estublock;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.HashMap;

public class RegisterFragment extends Fragment {

  EditText et_name;
  public static final String et_name_key = "com.example.estublock.et_name";
  EditText et_email;
  public static final String et_email_key = "com.example.estublock.et_email";

  EditText et_password;
  EditText et_repassword;

  public RegisterFragment() {

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_register, container, false);

    // Esto arregla la guerra con las llamadas a APIs
    // Mi SDK --> 26
    int SDK_INT = android.os.Build.VERSION.SDK_INT;
    if (SDK_INT > 8) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
          .permitAll().build();
      StrictMode.setThreadPolicy(policy);

      et_name = view.findViewById(R.id.et_name);
      et_email = view.findViewById(R.id.et_email);
      et_password = view.findViewById(R.id.et_password);
      et_repassword = view.findViewById(R.id.et_repassword);


      Button btn = (Button) view.findViewById(R.id.btn_register);
      btn.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View v){
          registerUser();
        }
      });
    }
    return view;
  }

  // Called when the user taps the Register Button in "fragment_register.xml"
  public void registerUser(){
    if(!checkDataEntered()){

      try{
        CallsToAPI api = new CallsToAPI();
        String url = "http://192.168.1.7:10010/user";
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("correo", et_email.getText().toString());
        dataMap.put("id_huella", "00000000");
        dataMap.put("matricula", "00000000");
        dataMap.put("password", api.hashPassword(et_password.getText().toString()));
        dataMap.put("apellido1", "ApellidoPeter");
        dataMap.put("apellido2", "ApellidoAnguila");
        dataMap.put("nombre", et_name.getText().toString());

        String json = api.createJson(dataMap);
        String response = api.createUser(url, json);
        System.out.println("DEBUG -- " + response);

      } catch (Exception e){
        e.printStackTrace();
      }
      Intent intent = new Intent(getActivity(), MenuPageActivity.class);
      intent.putExtra(et_name_key, et_name.getText().toString());
      intent.putExtra(et_email_key, et_email.getText().toString());
      startActivity(intent);
      getActivity().finish();
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
}
