package com.example.estublock;

import android.app.Application;

public class GlobalState extends Application {
  private String pathToWallet;
  private String userEmail;
  private String userName;
  private String blockchain_URL = "http://hubble.ls.fi.upm.es:10010";
  private String dataBase_URL = "http://hubble.ls.fi.upm.es:10011";
  private String micro_URL = "http://hubble.ls.fi.upm.es:10012";

  public void setPathToWallet(String pathToWallet) { this.pathToWallet = pathToWallet; }
  public String getPathToWallet(){ return this.pathToWallet; }

  public void setUserEmail(String userEmail){ this.userEmail = userEmail; }
  public String getUserEmail(){ return this.userEmail; }

  public void setUserName(String userName){ this.userName = userName; }
  public String getUserName(){ return this.userName; }

  public String getBlockchain_URL(){ return this.blockchain_URL; }
  public String getDataBase_URL(){ return this.dataBase_URL; }
  public String getMicro_URL(){ return this.micro_URL; }

}
