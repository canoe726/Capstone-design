package com.example.myauthentication;

import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AuthActivity extends AppCompatActivity {

    private KeyStore keyStore;
    KeyGenerator keyGenerator = null;
    private  static final String KEY_NAME="FingerAuth";
    private Cipher cipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();

            if (uri != null) {
                String when = uri.getQueryParameter("when");
                String message = uri.getQueryParameter("message");

                Log.d("MyTag", "when : " + when + " , message : " + message);
            }
        }

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        if(!fingerprintManager.isHardwareDetected()) {
            //Toast.makeText(this, "Fingerprint authentication permission not enable", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "지문인식 기능 요청을 거부하셨습니다.", Toast.LENGTH_SHORT).show();
        }
        else {
            if(!fingerprintManager.hasEnrolledFingerprints()) {
                //Toast.makeText(this, "Register at least one fingerprint in Setting", Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "설정에서 지문을 등록해주세요.", Toast.LENGTH_SHORT).show();
            }
            else {
                if(!keyguardManager.isKeyguardSecure()) {
                    //Toast.makeText(this, "Lock screen security not enable in Setting", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "설정에서 잠금 화면 보안 기능을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
                else {
                    genKey();
                }

                if(cipherInit()) {
                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                    FingerprintHandler helper = new FingerprintHandler(this);
                    helper.startAuthentication(fingerprintManager, cryptoObject);
                }
            }
        }

    }

    private boolean cipherInit() {

        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+ KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            //Toast.makeText(this, "NoSuchAlgorithmException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        } catch (NoSuchPaddingException e) {
            //Toast.makeText(this, "NoSuchPaddingException", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            return false;
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (CertificateException e1) {
            e1.printStackTrace();
            return false;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return false;
        } catch (UnrecoverableKeyException e1) {
            e1.printStackTrace();
            return false;
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
            return false;
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
            return false;
        }
    }

    private void genKey() {
        try {
            keyStore = keyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }

        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
                   );

            keyGenerator.generateKey();

        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e)
        {
            e.printStackTrace();
        }
    }
}










