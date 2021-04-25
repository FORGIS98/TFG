package com.example.estublock;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class CallsToAPI {

  String URL = "http://hubble.ls.fi.upm.es:10012";

  protected String createUser(JSONObject params) throws IOException {
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
        (Request.Method.POST, (URL + "/user"), params,

            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {

              }
            }, new Response.ErrorListener() {
          @Override
          public void onErrorResponse(VolleyError error) {

          }
        }); // FIN JsonObjectRequest
    return "";
  }

  protected JSONObject createJson(HashMap<String, String> params){
    JSONObject json = new JSONObject();
    try {
      json.put("email", params.get("email"));
      json.put("id_huella", params.get("id_huella"));
      json.put("matricula", params.get("matricula"));
      json.put("password", params.get("password"));
      json.put("Apellido1", params.get("Apellido1"));
      json.put("Apellido2", params.get("Apellido2"));
      json.put("nombre", params.get("nombre"));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return json;
  }

}
