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

public class TransactionsHelper {

  Web3j web3j;

  public TransactionsHelper(@NonNull String blockchainURL){
    web3j = Web3j.build(new HttpService(blockchainURL));
  }

  public void signTransaction(@NonNull Credentials credentials, @NonNull String gasPrice, @NonNull String gas, @NonNull String to, @NonNull String data, @NonNull EasyBlockchainListener listener){
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
              Numeric.toBigInt(gas),
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

  public void signAndSendTransaction(@NonNull Credentials credentials, @NonNull String gasPrice, @NonNull String gas, @NonNull String to, @NonNull String data){
    this.signTransaction(credentials, gasPrice, gas, to, data, new EasyBlockchainListener() {
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
