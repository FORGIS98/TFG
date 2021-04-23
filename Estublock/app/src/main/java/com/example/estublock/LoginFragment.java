package com.example.estublock;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class LoginFragment extends Fragment {

  EditText et_password;
  EditText et_email;
  public static final String et_email_key = "com.example.estublock.et_email";

  public LoginFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_login, container, false);

    // Esto arregla la guerra con las llamadas a APIs
    // Mi SDK --> 26
    int SDK_INT = android.os.Build.VERSION.SDK_INT;
    if (SDK_INT > 8) {
      // StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
      //     .permitAll().build();
      // StrictMode.setThreadPolicy(policy);

      et_email = view.findViewById(R.id.et_email);
      et_password = view.findViewById(R.id.et_password);


      Button btn = (Button) view.findViewById(R.id.btn_login);
      btn.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View v){
          loadMenu();
        }
      });
    }
    return view;
  }

  private void loadMenu(){
    if(!checkUser()){
      Intent intent = new Intent(getActivity(), MenuPageActivity.class);
      // TODO: intent.putExtra(et_name_key, et_name.getText().toString());
      intent.putExtra(et_email_key, et_email.getText().toString());
      startActivity(intent);
      getActivity().finish();
    }
  }

  private boolean checkUser(){
    // TODO: Pillar usuario de manera juanchisima con el email
    // TODO: Crear una lista de cosas que cambiar en el server por que es una pedazo de mierda :)
    return false;
  }
}
