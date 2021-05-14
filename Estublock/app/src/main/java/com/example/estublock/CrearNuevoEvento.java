package com.example.estublock;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.Credential;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.example.estublock.SeleccionTema.ID_TEMA_ELEGIDO;
import static com.example.estublock.SeleccionTema.STRING_TEMA_ELEGIDO;

public class CrearNuevoEvento extends AppCompatActivity implements View.OnClickListener{

  // VARIABLES GLOBALES
  RequestQueue requestQueue;
  GlobalState gs;
  Web3j web3j;

  TextView titulo_tema;
  EditText titulo_evento, fecha_dia, fecha_hora, numeroCreditos, descripcion;
  Button boton_fecha_dia, boton_fecha_hora;
  Spinner seleccion_tipo_tema;
  String stringTema;

  // Elementos que necesito para llamar al microservicio
  int idTema;
  int idTipoEvento; // Exámen, Práctica, Charla
  int year, month, day, hour, minute;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_crear_nuevo_evento);

    gs = (GlobalState) getApplication();
    requestQueue = Volley.newRequestQueue(this);

    // Recupero el ID y Nombre del tema al que se le va a crear un evento
    Intent intent = getIntent();
    idTema = intent.getIntExtra(ID_TEMA_ELEGIDO, 0);
    stringTema = intent.getStringExtra(STRING_TEMA_ELEGIDO);

    // Ponemos el titulo del tema que ha elegido pues ya lo conocemos
    titulo_tema = findViewById(R.id.titulo_tema);
    titulo_tema.setText(stringTema);

    // "Linkeamos" los objetos con sus respectivos en el front-end
    titulo_evento = (EditText) findViewById(R.id.titulo_evento);
    fecha_dia = (EditText) findViewById(R.id.in_date);
    fecha_hora = (EditText) findViewById(R.id.in_hora);
    numeroCreditos = (EditText) findViewById(R.id.numero_creditos);
    descripcion = (EditText) findViewById(R.id.descripcion);
    boton_fecha_dia = (Button) findViewById(R.id.btn_date);
    boton_fecha_dia.setOnClickListener(this);
    boton_fecha_hora = (Button) findViewById(R.id.btn_hour);
    boton_fecha_hora.setOnClickListener(this);
    seleccion_tipo_tema = (Spinner) findViewById(R.id.seleccion_tipo_tema);

    Button boton_guardar = (Button) findViewById(R.id.button_guardar_evento);
    boton_guardar.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        // Se pide que preparen la transaccion y se firma también
        askForTransaction();
      }
    });

    // El spinner es el dropdown de toda la vida
    // Los parametros se añaden desde esta funcion
    construirSpinner();

  }

  // Se le pide a la API de microservicios que prepare la transaccion
  // la API devuelve una transacción lista para ser firmada.
  public void askForTransaction(){
    try {
      Credentials credentials = WalletUtils.loadCredentials(gs.getUserPassword(), gs.getPathToWallet());
      String address = credentials.getAddress();

      Calendar cal = Calendar.getInstance();
      cal.set(year, month, day, hour, minute);

      // Preparamos el JSON
      HashMap<String, Object> dataMap = new HashMap<>();
      dataMap.put("organizer", address);
      dataMap.put("title", titulo_evento.getText().toString());
      dataMap.put("topic", idTema);
      dataMap.put("date", cal.getTimeInMillis());
      dataMap.put("credits", Integer.valueOf(numeroCreditos.getText().toString()));
      dataMap.put("summarize", descripcion.getText().toString());
      dataMap.put("type", idTipoEvento);

      JSONObject params = new JSONObject(dataMap);
      System.out.println("EL JSON QUE ENVIO ES: ");
      System.out.println(params);

      // Se hace la llamada POST para que nos preparen la transaccion que luego firmaremos
      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
          (gs.getMicro_URL() + "/event"), params,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
              signTransaction(response);
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          error.printStackTrace();
        }
      });

      // Encolamos la llamada
      requestQueue.add(jsonObjectRequest);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  // El usuario firma la transacción con su wallet
  // y se envia a la API para que la mande a la blockchain
  // SDK: Saltarse a la API y enviar directamente la transaccion firmada
  public void signTransaction(JSONObject tx){
    System.out.println("LA TRANSACCION LISTA PARA SER FIRMADA SI ESO ES: ");
    System.out.println(tx.toString());

    try {
      web3j = Web3j.build(new HttpService(gs.getQuorum_RPC()));
      Credentials credentials = WalletUtils.loadCredentials(gs.getUserPassword(), gs.getPathToWallet());

      RawTransaction rawTransaction = RawTransaction.createTransaction(
          (BigInteger) Numeric.toBigInt((String) tx.get("nonce")),
          (BigInteger) Numeric.toBigInt((String) tx.get("gasPrice")),
          (BigInteger) Numeric.toBigInt((String) tx.get("gas")),
          (String) tx.get("to"),
          (String) tx.get("data")
      );

      byte [] signedTx = TransactionEncoder.signMessage(rawTransaction, credentials);
      String hexSignedMessage = Numeric.toHexString(signedTx);

      HashMap<String, String> dataMap = new HashMap<>();
      dataMap.put("tx", hexSignedMessage);

      JSONObject params = new JSONObject(dataMap);

      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
          (gs.getMicro_URL() + "/transaction"), params,
          new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response){
              System.out.println(response);
            }
          }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
          error.printStackTrace();
        }
      });

      // Encolamos la llamada
      requestQueue.add(jsonObjectRequest);
    }catch(Exception e){
      e.printStackTrace();
    }
  }

  // Contruye el dropdown
  // TODO: Pedir los tipos a la base de datos
  // TODO: El hashMap catalogoEventos debería pillar la info
  //  de la base de datos también
  protected void construirSpinner(){
    List<String> listaDeTipos = new ArrayList<>();
    HashMap<String, Integer> catalogoEventos = new HashMap<>();
    listaDeTipos.add("Examen"); catalogoEventos.put(listaDeTipos.get(0), 1);
    listaDeTipos.add("Practica"); catalogoEventos.put(listaDeTipos.get(1), 2);
    listaDeTipos.add("Charla"); catalogoEventos.put(listaDeTipos.get(2), 3);

    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaDeTipos);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    seleccion_tipo_tema.setAdapter(adapter);

    seleccion_tipo_tema.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        idTipoEvento = catalogoEventos.get(listaDeTipos.get(position));
      }

      @Override
      public void onNothingSelected(AdapterView<?> adapterView) {}
    });
  }

  // Para dejar elegir fecha y hora, se crea
  // un calendario y un reloj donde elegir la fecha
  @Override
  public void onClick(View v){

    final Calendar calendar = Calendar.getInstance();

    // Se quiere añadir el DIA
    if(v == boton_fecha_dia){
      year = calendar.get(Calendar.YEAR);
      month = calendar.get(Calendar.MONTH);
      day = calendar.get(Calendar.DAY_OF_MONTH);

      DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int tmpYear, int tmpMonth, int tmpDay) {
          year = tmpYear;
          month = tmpMonth;
          day = tmpDay;

          fecha_dia.setText(tmpDay + "-" + (tmpMonth + 1) + "-" + tmpYear);
        }
      }, year, month, day);
      datePickerDialog.show();
    }


    // Se quiere añadir la HORA
    if(v == boton_fecha_hora){
      hour = calendar.get(Calendar.HOUR_OF_DAY);
      minute = calendar.get(Calendar.MINUTE);

      TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker timePicker, int tmpHour, int tmpMinute) {
          hour = tmpHour;
          minute = tmpMinute;

          fecha_hora.setText(tmpHour + ":" + tmpMinute);
        }
      }, hour, minute, true);
      timePickerDialog.show();
    }
  }
}
