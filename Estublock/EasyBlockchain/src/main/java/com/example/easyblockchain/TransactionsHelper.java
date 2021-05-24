package com.example.easyblockchain;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

/**
 * TransactionsHelper es una clase que permite firmar y enviar transacciones
 * a una red blockchain basada en Ethereum. Utiliza la librería web3j,
 * por lo que cualquier red blockchain que disponga de comunicación con JSON-RPC 
 * permite el uso de esta clase.
 *
 * @author Jorge Sol Gonzalez
 * {@link https://github.com/FORGIS98/}
 */
public class TransactionsHelper {

  Web3j web3j;

  /**
   * Constructor de la clase.
   * @param blockchainURL La URL y puerto al nodo de la red blockchain con la que se
   * quiere comunicar. 
   */
  public TransactionsHelper(@NonNull String blockchainURL){
    web3j = Web3j.build(new HttpService(blockchainURL));
  }

  /**
   * Método que firma una transacción.
   * @param credentials Las credenciales de un usuario, viene a ser su keystore
   * @param gasPrice Cantidad que quieres pagar por unidad de gas como tarifa al minero
   * @param gasLimit Limite máximo que estas dispuesto a pagar por la transacción
   * @param to Address del SMART CONTRACT de destino
   * @param data Información que se va a mandar al smart contract
   * @param listener Callback de la llamada a sobreescribir
   */
  public void signTransaction(@NonNull Credentials credentials, @NonNull String gasPrice, @NonNull String gasLimit, @NonNull String to, @NonNull String data, @NonNull EasyBlockchainListener listener){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          EthGetTransactionCount ethGetTransactionCount = null;
          ethGetTransactionCount = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).send();
          BigInteger nonce = ethGetTransactionCount.getTransactionCount();
          RawTransaction rawTransaction = RawTransaction.createTransaction(
              nonce,
              Numeric.toBigInt(gasPrice),
              Numeric.toBigInt(gasLimit),
              to,
              data
          );
          byte [] signedTx = TransactionEncoder.signMessage(rawTransaction, credentials);
          listener.onSignTransactionEvent(Numeric.toHexString(signedTx));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  /**
   * Método que envía una transacción ya firmada con listener.
   * @param signedMessage Transacción firmada lista para ser enviada
   * @param listener Callback de la llamada a sobreescribir
   */
  public void sendSignedTransaction(@NonNull String signedMessage, @NonNull EasyBlockchainListener listener){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(signedMessage).send();
          listener.onSendSignedTransactionEvent(ethSendTransaction.getTransactionHash());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  /**
   * Método que envía una transacción ya firmada sin listener.
   * @param signedMessage Transacción firmada lista para ser enviada
   */
  public void sendSignedTransaction(@NonNull String signedMessage){
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          web3j.ethSendRawTransaction(signedMessage).send();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }).start();
  }

  /**
   * Método que firma y envía una transacción.
   * @param credentials Las credenciales de un usuario, viene a ser su keystore
   * @param gasPrice Cantidad que quieres pagar por unidad de gas como tarifa al minero
   * @param gasLimit Limite máximo que estas dispuesto a pagar por la transacción
   * @param to Address del SMART CONTRACT de destino
   * @param data Información que se va a mandar al smart contract
   * @param listener Callback de la llamada a sobreescribir
   */  
  public void signAndSendTransaction(@NonNull Credentials credentials, @NonNull String gasPrice, @NonNull String gasLimit, @NonNull String to, @NonNull String data){
    this.signTransaction(credentials, gasPrice, gasLimit, to, data, new EasyBlockchainListener() {
      @Override
      public void onSignTransactionEvent(@NonNull @NotNull String signedTx) {
        sendSignedTransaction(signedTx);
      }

      @Override
      public void onSendSignedTransactionEvent(@NonNull @NotNull String transactionHash) {

      }
    });
  }
}
