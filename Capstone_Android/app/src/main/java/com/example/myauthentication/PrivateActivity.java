package com.example.myauthentication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class PrivateActivity extends AppCompatActivity {

    private static String TAG = "php_PrivateActivity";

    private static final String TAG_JSON="webnautes";
    private static final String TAG_NAME = "name";
    private static final String TAG_PHONE = "phone";
    private static final String TAG_EMAIL ="email";
    private static final String TAG_FingerPrint ="finger";

    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);

        mlistView = (ListView) findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();

        String sid = "";
        MyApplication myApp = (MyApplication) getApplication();
        sid = myApp.getGlobalStudentId();

        insertoToDatabase(sid);
    }

    private void insertoToDatabase(String user_id) {
        class GetData extends AsyncTask<String, Void, String> {
            ProgressDialog progressDialog;
            String errorString = null;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(PrivateActivity.this,
                        "잠시만 기다려주세요", null, true, true);
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                progressDialog.dismiss();
                Log.d(TAG, "response  - " + result);

                if (result != null) {
                    mJsonString = result;
                    showResult();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    String serverURL = "http://123.109.137.37/Capstone/get_personal_info.php";

                    String user_id = (String) params[0];
                    String data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");

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
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }
                    bufferedReader.close();
                    return sb.toString().trim();

                } catch (Exception e) {

                    Log.d(TAG, "InsertData: Error ", e);
                    errorString = e.toString();

                    return null;
                }

            }
        }
        GetData task = new GetData();
        task.execute(user_id);
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String phone = item.getString(TAG_PHONE);
                String email = item.getString(TAG_EMAIL);
                String finger = item.getString(TAG_FingerPrint);

                HashMap<String,String> hashMap = new HashMap<>();

                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_PHONE, phone);
                hashMap.put(TAG_EMAIL, email);
                hashMap.put(TAG_FingerPrint, finger);

                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    PrivateActivity.this, mArrayList, R.layout.private_list_item,
                    //new String[]{TAG_NAME,TAG_PHONE, TAG_EMAIL},
                    new String[]{TAG_NAME, TAG_PHONE, TAG_EMAIL, TAG_FingerPrint},
                    new int[]{R.id.textView_list_name, R.id.textView_list_phone, R.id.textView_list_email, R.id.textView_list_finger}
            );
            mlistView.setAdapter(adapter);

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }

}
