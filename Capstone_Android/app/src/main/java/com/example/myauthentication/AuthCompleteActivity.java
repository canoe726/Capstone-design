package com.example.myauthentication;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class AuthCompleteActivity extends AppCompatActivity {

    String TAG = "AuthCompleteActivityTAG";
    Activity activity = AuthCompleteActivity.this;
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authcomplete);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String fingerprint = bundle.getString("USER_FINGERPRINT");
        fingerprint = fingerprint.substring(69);
        Toast.makeText(this, fingerprint, Toast.LENGTH_LONG).show();

        String phoneNumber = "";

        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        } else {
            phoneNumber = getPhone();
            if (phoneNumber.startsWith("+82")) {
                phoneNumber = phoneNumber.replace("+82", "0");
            }
            Log.d(TAG, "Phone number: " + getPhone());
        }

        //Toast.makeText(this, "Finger print show : " + fingerprint, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Phone Number show : " + phoneNumber, Toast.LENGTH_SHORT).show();

        insertoToDatabase(fingerprint, phoneNumber);
    }

    private void requestPermission(String permission){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            //Toast.makeText(activity, "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
            Toast.makeText(activity, "전화 상태 권한 기능을 허용해야합니다.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(activity, new String[]{permission},PERMISSION_REQUEST_CODE);
    }

    private String getPhone() {
        TelephonyManager phoneMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, wantPermission) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return phoneMgr.getLine1Number();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Phone number: " + getPhone());
                } else {
                    //Toast.makeText(activity,"Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
                    Toast.makeText(activity,"권한이 거부되어 휴대폰 번호를 얻을 수 없습니다.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkPermission(String permission){
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(activity, permission);
            if (result == PackageManager.PERMISSION_GRANTED){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void insertoToDatabase(String fingerprint, String phoneNumber) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AuthCompleteActivity.this, "잠시만 기다려주세요", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String fingerprint = (String) params[0];
                    String phoneNumber = (String) params[1];

                    String link = "http://123.109.137.37/Capstone/userAuth_check.php";
                    String data = URLEncoder.encode("fingerprint", "UTF-8") + "=" + URLEncoder.encode(fingerprint, "UTF-8");
                    data += "&" + URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(phoneNumber, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }

        InsertData task = new InsertData();
        task.execute(fingerprint, phoneNumber);
    }
}
