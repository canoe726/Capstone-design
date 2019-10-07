package com.example.myauthentication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

public class FingerprintHandler extends FingerprintManager.AuthenticationCallback{

    private Context context;
    public static final int auth_activity = 1001;

    public FingerprintHandler(Context context) {
        this.context = context;
    }

    public void startAuthentication(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {

        CancellationSignal cancellationSignal = new CancellationSignal();
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationFailed() {
        super.onAuthenticationFailed();
        //Toast.makeText(context, "Fingerprint Authentication failed", Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "지문인식에 실패 하셨습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        super.onAuthenticationSucceeded(result);
        String str = result.toString();
        //Toast.makeText(context, str, Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, AuthCompleteActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("USER_FINGERPRINT", str);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }
}
