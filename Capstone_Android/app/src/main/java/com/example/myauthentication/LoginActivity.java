package com.example.myauthentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {

    private static String TAG = "php_LoginActivity";

    Intent intent;
    EditText sid_editText, spw_editText;
    String mJsonString;
    String sid, spw, sha_sid, sha_spw;
    String db_id, db_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final CheckBox auto_login_box = (CheckBox)findViewById(R.id.auto_login_box);

        sid_editText = (EditText) findViewById(R.id.sid_editText);
        spw_editText = (EditText) findViewById(R.id.spw_editText);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        sid = auto.getString("inputId",null);
        spw = auto.getString("inputPw",null);
        db_id = auto.getString("dbId",null);
        db_pw = auto.getString("dbPw",null);

        if( (sid != null) && (spw != null) ) {
            sha_sid = makeShaCode(sid);
            sha_spw = makeShaCode(spw);

            if (sha_sid.equals(db_id) && sha_spw.equals(db_pw)) {
                Intent intent = new Intent(LoginActivity.this, FunctionActivity.class);

                MyApplication myApp = (MyApplication) getApplication();
                myApp.setGlobalStudentId(sid);

                startActivity(intent);
                finish();
            }
        }
        else {
            Button login_button = (Button) findViewById(R.id.login_button);
            login_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    sid = sid_editText.getText().toString();
                    spw = spw_editText.getText().toString();

                    if (sid.equals("") || spw.equals("")) {
                        Toast.makeText(getApplicationContext(), "학번과 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        String res = insertoToDatabase(sid, spw);

                        if( res != null ) {
                            mJsonString = res;
                            try {
                                JSONObject jsonObject = new JSONObject(mJsonString);

                                db_id = jsonObject.getString("user_id");
                                db_pw = jsonObject.getString("user_pw");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            sha_sid = makeShaCode(sid);
                            sha_spw = makeShaCode(spw);

                            if (sha_sid.equals(db_id) && sha_spw.equals(db_pw)) {

                                if (auto_login_box.isChecked() == true) {
                                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor autoLogin = auto.edit();
                                    autoLogin.putString("inputId", sid);
                                    autoLogin.putString("inputPw", spw);
                                    autoLogin.putString("dbId", db_id);
                                    autoLogin.putString("dbPw", db_pw);
                                    autoLogin.commit();
                                }

                                Toast.makeText(getApplicationContext(), "인증 페이지로 이동합니다.", Toast.LENGTH_SHORT).show();
                                intent = new Intent(getApplicationContext(), FunctionActivity.class);

                                MyApplication myApp = (MyApplication) getApplication();
                                myApp.setGlobalStudentId(sid);

                                startActivity(intent); // 다음화면으로 넘어가기
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "학번과 비밀번호가 맞지 않습니다", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "등록된 사용자가 아닙니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    }

    public String makeShaCode(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] digest = md.digest(str.getBytes());
        StringBuilder sha_sid = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            sha_sid.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sha_sid.toString();
    }

    private String insertoToDatabase(String user_id, String user_pw) {
        class GetData extends AsyncTask<String, Void, String> {
            ProgressDialog progressDialog;
            String errorString = null;

            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(LoginActivity.this,
                        "잠시만 기다려주세요", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                progressDialog.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String serverURL = "http://123.109.137.37/Capstone/app_logincheck.php";

                    String studentId = (String) params[0];
                    String studentPw = (String) params[1];

                    String data = URLEncoder.encode("StudentID", "UTF-8") + "=" + URLEncoder.encode(studentId, "UTF-8");
                    data += "&" + URLEncoder.encode("Password", "UTF-8") + "=" + URLEncoder.encode(studentPw, "UTF-8");

                    URL url = new URL(serverURL);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);

                    OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                    wr.write(data);
                    wr.flush();

                    int responseStatusCode = httpURLConnection.getResponseCode();
                    Log.d(TAG, "response code - " + responseStatusCode);

                    InputStream inputStream;
                    if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                        inputStream = httpURLConnection.getInputStream();
                    } else {
                        inputStream = httpURLConnection.getErrorStream();
                    }

                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                    StringBuilder sb = new StringBuilder();
                    String line = "";

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    return sb.toString().trim();

                } catch (Exception e) {
                    errorString = e.toString();
                    return null;
                }
            }
        }
        GetData task = new GetData();

        try {
            return task.execute(user_id, user_pw).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
