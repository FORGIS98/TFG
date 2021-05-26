package com.example.estublock;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class MenuPageActivity extends AppCompatActivity {

  // VARIABLES GLOBALES
  GlobalState gs;
  Web3j web3j;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_menu_page);

    gs = (GlobalState) getApplication();

    TextView userName = findViewById(R.id.nombre_logueado);
    userName.setText("");

    // Boton que te lleva a suscribirte a temas como redes, arqui...
    Button btn_suscripciones = findViewById(R.id.mis_asignaturas);
    btn_suscripciones.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        Intent subIntent = new Intent(v.getContext(), UserSuscripciones.class);
        startActivity(subIntent);
      }
    });

    // Boton que lleva a los PROFES a poder crear eventos
    Button btn_eventos = findViewById(R.id.crear_eventos);
    btn_eventos.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        Intent nuevoEvento = new Intent(v.getContext(), SeleccionTema.class);
        startActivity(nuevoEvento);
      }
    });

    // Boton que te lleva a una lista de eventos a los que asistir
    Button btn_asistencia = findViewById(R.id.asistencia);
    btn_asistencia.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        Intent asistencias = new Intent(v.getContext(), AsistenciasTemas.class);
        startActivity(asistencias);
      }
    });

    // Boton que te lleva a una lista de eventos a los que asistir
    Button btn_escanearQR = findViewById(R.id.escanearQR);
    btn_escanearQR.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v){
        Intent asistencias = new Intent(v.getContext(), EscanearQRTutorial.class);
        startActivity(asistencias);
      }
    });
  }

//  protected void sendTransactionThread(){
//    new Thread(new Runnable() {
//      @Override
//      public void run() {
//
//      }
//    }).start();
//  }

} // END - public class MenuPage