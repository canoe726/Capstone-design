package com.example.myauthentication;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RecordRegisterActivity extends AppCompatActivity {

    final String upLoadServerUri = "http://123.109.137.37/Capstone/uploadToserver.php";//서버컴퓨터의 ip주소
    final String uploadFilePath = "/sdcard/SejongAuth/";

    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;
    private final int MY_PERMISSIONS_FILE_AUDIO = 1;

    int count = 0;
    int serverResponseCode = 0;

    MediaRecorder recorder;
    ProgressDialog dialog;

    String RECORDED_FILE = "";
    String uploadFileName = "";
    String auth_words[] = {"세종", "율곡", "충무"};
    String save_words[] = {"sejong", "yulgok", "chungmu"};

    TextView voice_char, voice_count;
    String sid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_register);

        MyApplication myApp = (MyApplication) getApplication();
        sid = myApp.getGlobalStudentId();

        requestAudioPermissions();
        requestFilePermissions();

        voice_char = (TextView)findViewById(R.id.voice_char);
        voice_count = (TextView)findViewById(R.id.voice_count);

        if( count/3 == 0 ) {
            voice_char.setText(auth_words[0]);
        } else if( count/3 == 1 ) {
            voice_char.setText(auth_words[1]);
        } else {
            voice_char.setText(auth_words[2]);
        }
        voice_count.setText(((count)%3+1) + " / 3");

        Button rec_button = (Button)findViewById(R.id.rec_button);
        final Button next_button = (Button)findViewById(R.id.next_button);

        rec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(recorder != null){
                    recorder.stop();
                    recorder.release();
                    recorder = null;
                }// TODO Auto-generated method stub
                recorder = new MediaRecorder();
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                recorder.setMaxDuration(3000);

                RECORDED_FILE = "/sdcard/SejongAuth/" + sid + "/";
                if( count/3 == 0 ) {
                    RECORDED_FILE += save_words[0] + ((count)%3+1) + ".m4a";
                    uploadFileName = save_words[0] + ((count)%3+1) + ".m4a";
                } else if( count/3 == 1 ) {
                    RECORDED_FILE += save_words[1] + ((count)%3+1) + ".m4a";
                    uploadFileName = save_words[1] + ((count)%3+1) + ".m4a";
                } else {
                    RECORDED_FILE += save_words[2] + ((count)%3+1) + ".m4a";
                    uploadFileName = save_words[2] + ((count)%3+1) + ".m4a";
                }
                recorder.setOutputFile(RECORDED_FILE);

                try{
                    Toast.makeText(getApplicationContext(),"3초간 녹음을 시작합니다.", Toast.LENGTH_LONG).show();
                    recorder.prepare();
                    recorder.start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            // TODO
                            next_button.setVisibility(View.VISIBLE);
                        }
                    }, 2000);

                }catch (Exception ex){
                    Log.e("SampleAudioRecorder", "Exception : ", ex);
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( count < 8 ) {
                    count += 1;

                    if(recorder == null)
                        return;

                    recorder.stop();
                    recorder.release();
                    recorder = null;

                    next_button.setVisibility(View.INVISIBLE);
                    if( count/3 == 0 ) {
                        voice_char.setText(auth_words[0]);
                    } else if( count/3 == 1 ) {
                        voice_char.setText(auth_words[1]);
                    } else {
                        voice_char.setText(auth_words[2]);
                    }
                    voice_count.setText(((count)%3+1) + " / 3");

                    dialog = ProgressDialog.show(RecordRegisterActivity.this, "", "Uploading file...", true);

                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                }
                            });
                            String path = uploadFilePath + sid + "/" + uploadFileName;
                            uploadFile(path);
                        }
                    }).start();

                } else {
                    Intent intent = new Intent(getApplicationContext(), FunctionActivity.class);
                    Toast.makeText(getApplicationContext(), "등록이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                    startActivity(intent);

                    dialog = ProgressDialog.show(RecordRegisterActivity.this, "", "Uploading file...", true);

                    new Thread(new Runnable() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {

                                }
                            });
                            String path = uploadFilePath + sid + "/" + uploadFileName;
                            uploadFile(path);
                        }
                    }).start();

                    finish();
                }
            }
        });
    }

    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "음성 녹음을 하기 위한 권한을 등록해 주세요.", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            //recordAudio();
        }
    }

    private void requestFilePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "파일 저장을 위한 권한을 등록해주세요.", Toast.LENGTH_LONG).show();

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_FILE_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_FILE_AUDIO);
            }
        }
        //If permission is granted, then go ahead recording audio
        else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            //Go ahead with recording audio now
            //recordAudio();
        }
    }

    public int uploadFile(String sourceFileUri) {
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {
            dialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    +uploadFilePath + sid + "/" + uploadFileName);

            runOnUiThread(new Runnable() {
                public void run() {
                }
            });
            return 0;
        }
        else
        {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes("\r\n--" + boundary + "\r\n");
                dos.writeBytes("Content-Disposition: form-data; name=\"sid\"\r\n\r\n" + sid);
                dos.writeBytes("\r\n--" + boundary + "\r\n");

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        public void run() {
                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
                                    +uploadFileName;

                            Toast.makeText(RecordRegisterActivity.this, "File Upload Complete.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {
                dialog.dismiss();
                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RecordRegisterActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(RecordRegisterActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : "
                        + e.getMessage(), e);
            }
            dialog.dismiss();
            return serverResponseCode;
        } // End else block
    }
}
