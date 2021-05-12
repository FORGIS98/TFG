package com.example.estublock;

import android.app.Application;

import java.io.File;

public class GlobalState extends Application {
  private File pathToWallet;
  private String userEmail;
  private String userName;
  private String userPassword;

  private String quorum_RPC = "http://138.100.10.226:22000"; // Dejarlo con IP en vez de dominio, no se conecta a la blockchain con el dominio :/
  private String blockchain_URL = "http://hubble.ls.fi.upm.es:10010";
  private String dataBase_URL = "http://hubble.ls.fi.upm.es:10011";
  private String micro_URL = "http://hubble.ls.fi.upm.es:10012";

  public void setPathToWallet(File pathToWallet) { this.pathToWallet = pathToWallet; }
  public File getPathToWallet(){ return this.pathToWallet; }

  public void setUserEmail(String userEmail){ this.userEmail = userEmail; }
  public String getUserEmail(){ return this.userEmail; }

  public void setUserName(String userName){ this.userName = userName; }
  public String getUserName(){ return this.userName; }

  public void setUserPassword(String userPassword){ this.userPassword = userPassword; }
  public String getUserPassword(){ return this.userPassword; }

  public String getBlockchain_URL(){ return this.blockchain_URL; }
  public String getDataBase_URL(){ return this.dataBase_URL; }
  public String getMicro_URL(){ return this.micro_URL; }
  public String getQuorum_RPC(){ return this.quorum_RPC; }

}
