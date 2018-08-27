package com.hec.app.util;

public interface OfflineTransferListener {
    void onConfirmTransfer(String userName, String userPwd, String transferAmount);

    void onSendCaptcha();

    void onTransferComplete(String userName, String userPwd, String transferAmount, boolean hasSMS, String captcha, boolean check30Min);
}
