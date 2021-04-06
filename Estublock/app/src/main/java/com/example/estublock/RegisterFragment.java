package com.example.estublock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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

    return inflater.inflate(R.layout.fragment_register, container, false);
  }

  // Called when the user taps the Register Button in "fragment_register.xml"
  public void registerUser(View view){
    et_name = view.findViewById(R.id.et_name);
    et_email = view.findViewById(R.id.et_email);
    et_password = view.findViewById(R.id.et_password);
    et_repassword = view.findViewById(R.id.et_repassword);
    if(!checkDataEntered(view)){
      Intent intent = new Intent(getActivity(), MenuPageActivity.class);
      intent.putExtra(et_name_key, et_name.getText().toString());
      intent.putExtra(et_email_key, et_email.getText().toString());
      startActivity(intent);
      getActivity().finish();
    }
  }

  private boolean checkDataEntered(View view){
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
    if(!et_password.equals(et_repassword)){
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
