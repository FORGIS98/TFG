package com.example.easyblockchain;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

public class WalletHelper {

  public WalletHelper(){
    workaroundECDA();
  }

  private void workaroundECDA(){
    // See: https://github.com/web3j/web3j/issues/915
    final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    if(provider != null || !provider.getClass().equals(BouncyCastleProvider.class)){
      Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
      Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
  }

  @NonNull
  public String createNewWallet(@NonNull String password, @NonNull File walletDirectory)
      throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
    return WalletUtils.generateLightNewWalletFile(password, walletDirectory);
  }

  @NonNull
  public String createNewWallet(@NonNull String password, @NonNull String walletDirectory)
      throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
    return WalletUtils.generateLightNewWalletFile(password, new File(walletDirectory));
  }


  @NonNull
  public String getAddress(@NonNull String password, @NonNull File keyStoreDirectory)
      throws IOException, CipherException {
    Credentials credentials = WalletUtils.loadCredentials(password, keyStoreDirectory);
    return credentials.getAddress();
  }

  @NonNull
  public String getAddress(@NonNull String password, @NonNull String keyStoreDirectory)
      throws IOException, CipherException {
    Credentials credentials = WalletUtils.loadCredentials(password, keyStoreDirectory);
    return credentials.getAddress();
  }


  @NonNull
  public Credentials getCredentials(@NonNull String password, @NonNull File keyStoreDirectory)
      throws IOException, CipherException {
    return WalletUtils.loadCredentials(password, keyStoreDirectory);
  }

  public void saveInPreferences(@NonNull String id, @NonNull String walletDirectory, @NonNull String prefsName, @NonNull Activity activity){
    SharedPreferences sharedPreferences = activity.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(id, walletDirectory);
    editor.apply();
  }

  @NonNull
  public String getFromPreferences(@NonNull String id, @NonNull String prefsName, @NonNull Activity activity){
    SharedPreferences sharedPreferences = activity.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    return  sharedPreferences.getString(id, null);
  }
}
