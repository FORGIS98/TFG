package com.example.easyblockchain;

import androidx.annotation.NonNull;

public interface EasyBlockchainListener {
  void onSignTransactionEvent(@NonNull String signedTx);
  void onSendSignedTransactionEvent(@NonNull String transactionHash);
}
