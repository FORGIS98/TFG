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

/**
 * WalletHelper es una clase que permite generar y guardar carteras virtuales
 * para poder usarlas en redes blockchain. Utiliza la librería web3j
 * que permite crear carteras virtuales compatibles con redes basadas en Ethereum
 *
 * @author Jorge Sol Gonzalez
 * {@link https://github.com/FORGIS98/}
 */
public class WalletHelper {

  /**
   * Constructor de la clase, llama a la función workaroundECDA()
   */
  public WalletHelper(){
    workaroundECDA();
  }

  /**
   * Método que arregla el problema con el proveedor de seguridad
   * Para saber más ir a este link: {@link https://github.com/web3j/web3j/issues/915}
   */
  private void workaroundECDA(){
    final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
    if(provider != null || !provider.getClass().equals(BouncyCastleProvider.class)){
      Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
      Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
  }

  /**
   * Método que crea una nueva cartera virtual
   * @param password Contraseña de la cartera virtual
   * @param walletDirectory Dirección de la nueva cartera virtual
   * @return Devuelve el nombre del keystore
   */
  @NonNull
  public String createNewWallet(@NonNull String password, @NonNull File walletDirectory)
      throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
    return WalletUtils.generateLightNewWalletFile(password, walletDirectory);
  }

  /**
   * Método que crea una nueva cartera virtual
   * @param password Contraseña de la cartera virtual
   * @param walletDirectory Dirección de la nueva cartera virtual
   * @return Devuelve el nombre del keystore
   */  @NonNull
  public String createNewWallet(@NonNull String password, @NonNull String walletDirectory)
      throws CipherException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
    return WalletUtils.generateLightNewWalletFile(password, new File(walletDirectory));
  }


  /**
   * Método que devuelve el address de una cartera
   * @param password Contraseña de la cartera virtual
   * @param keyStoreDirectory Dirección de la cartera virtual
   */  
  @NonNull
  public String getAddress(@NonNull String password, @NonNull File keyStoreDirectory)
      throws IOException, CipherException {
    Credentials credentials = WalletUtils.loadCredentials(password, keyStoreDirectory);
    return credentials.getAddress();
  }

  /**
   * Devuelve el address de una cartera
   * @param password Contraseña de la cartera virtual
   * @param keyStoreDirectory Dirección de la cartera virtual
   * @return address de la cartera
   */ 
  @NonNull
  public String getAddress(@NonNull String password, @NonNull String keyStoreDirectory)
      throws IOException, CipherException {
    Credentials credentials = WalletUtils.loadCredentials(password, keyStoreDirectory);
    return credentials.getAddress();
  }

  /**
   * Devuelve las credenciales
   * @param password Contraseña de la cartera virtual
   * @param keyStoreDirectory Dirección de la cartera virtual
   * @return credenciales de la cartera virtual
   */ 
  @NonNull
  public Credentials getCredentials(@NonNull String password, @NonNull File keyStoreDirectory)
      throws IOException, CipherException {
    return WalletUtils.loadCredentials(password, keyStoreDirectory);
  }

  /**
   * Guarda en el sharedPreferences de android un identificador y la dirección del wallet
   * @param id id para la dirección del wallet
   * @param walletDirectory Dirección de la cartera virtual
   * @param prefsName nombre de las shared preferences que se quiere usar
   * @param activity Actividad de android para poder cargar las shared preferences
   */ 
  public void saveInPreferences(@NonNull String id, @NonNull String walletDirectory, @NonNull String prefsName, @NonNull Activity activity){
    SharedPreferences sharedPreferences = activity.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString(id, walletDirectory);
    editor.apply();
  }

  /**
   * Devuelve el valor asociado al id
   * @param id id del que se quiere su valor
   * @param prefsName nombre de las shared preferences que se quiere usar
   * @param activity Actividad de android para poder cargar las shared preferences
   * @return Devuelve la dirección donde esta guardar la cartera virtual de la persona
   */ 
  @NonNull
  public String getFromPreferences(@NonNull String id, @NonNull String prefsName, @NonNull Activity activity){
    SharedPreferences sharedPreferences = activity.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    return  sharedPreferences.getString(id, null);
  }
}
