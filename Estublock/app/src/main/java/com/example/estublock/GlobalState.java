package com.example.estublock;

import android.app.Application;

public class GlobalState extends Application {
  public String pathToWallet;
  public String userEmail;
  public String userName;

  public void setPathToWallet(String pathToWallet) { this.pathToWallet = pathToWallet; }
  public String getPathToWallet(){ return this.pathToWallet; }

  public void setUserEmail(String userEmail){ this.userEmail = userEmail; }
  public String getUserEmail(){ return this.userEmail; }

  public void setUserName(String userName){ this.userName = userName; }
  public String getUserName(){ return this.userName; }

}
