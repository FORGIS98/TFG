package com.example.estublock;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import at.favre.lib.crypto.bcrypt.BCrypt;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CallsToAPI {
  OkHttpClient client = new OkHttpClient();
  public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  protected String createUser(String url, String json) throws IOException {

    RequestBody body = RequestBody.create(json, JSON);
    Request request = new Request.Builder()
        .url(url)
        .post(body)
        .build();

    try {
      Response response = client.newCall(request).execute();
      return response.body().string();
    }catch (Exception e){
      e.printStackTrace();
    }
    return null;
  }

  protected String createJson(HashMap<String, String> params){
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
    return json.toString();
  }

  protected String hashPassword(String password){
    return BCrypt.withDefaults().hashToString(12, password.toCharArray());
  }
}
