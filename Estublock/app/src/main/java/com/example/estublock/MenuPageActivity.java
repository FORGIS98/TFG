package com.example.estublock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    userName.setText(gs.getUserName());

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
        experimentos();
      }
    });
  }

  protected void experimentos(){
    try {
      web3j = Web3j.build(new HttpService(gs.getQuorum_RPC()));
      EthBlockNumber result = web3j.ethBlockNumber().sendAsync().get();
      System.out.println("The Block Number is: " + result.getBlockNumber().toString());
      sendTransactionThread();
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  protected void sendTransactionThread(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          Credentials credentials = WalletUtils.loadCredentials(gs.getUserPassword(), gs.getPathToWallet());
          String address = credentials.getAddress();
          System.out.println("Path to wallet: " + gs.getPathToWallet());
          System.out.println("User Address: " + address);

          Function function = new Function("createEvent", Arrays.asList(
              new org.web3j.abi.datatypes.Int(BigInteger.valueOf(100)),
              new org.web3j.abi.datatypes.Utf8String("Evento de Prueba"),
              new org.web3j.abi.datatypes.Utf8String("Redes de Computadores"),
              new org.web3j.abi.datatypes.Int(BigInteger.valueOf(0))
          ), Collections.emptyList());

          String encodedFunction = FunctionEncoder.encode(function);
          TransactionManager txManager = new FastRawTransactionManager(web3j, credentials);
          EthSendTransaction txHash = txManager.sendTransaction(DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT,
              "0x121f8ed9280ff940cd121361f9f3dd177e903817", encodedFunction, BigInteger.ZERO);

          System.out.println("Esta es la transaccion de caca: " + txHash.getRawResponse());

          TransactionReceiptProcessor receiptProcessor =
              new PollingTransactionReceiptProcessor(web3j, TransactionManager.DEFAULT_POLLING_FREQUENCY,
                  TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);

          TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash.getTransactionHash());
          System.out.println("receiptProcessor: " + receiptProcessor.toString());
          System.out.println("txReceipt: " + txReceipt.toString());
        }catch(Exception e){
          e.printStackTrace();
        }
      }
    }).start();
  }
} // END - public class MenuPage