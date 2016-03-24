package com.nombee;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erikotsuda on 3/18/16.
 */
public class AsyncLoginRequest extends AsyncTask<Void, Void, String> {
    /**
     * Server URL, App Name
     */
    //private static final String SERVER_URL = "http://nombee-app.appspot.com/";
    private static final String SERVER_URL = "http://1-dot-erikotestserver2.appspot.com/";
    private static final String SERVER_APP = "testservletapp";

    private Activity loginActivity;
    private String email;
    private String pass;

    public AsyncLoginRequest(Activity activity, String email, String pass){
        this.loginActivity = activity;
        this.email = email;
        this.pass = pass;
    }

    @Override
    protected String doInBackground(Void... params){

        String postStr = SERVER_URL + SERVER_APP + "?content=" + email;
        Log.i("hoge",postStr);

        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection)new URL(SERVER_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(postStr.getBytes().length);
            conn.setRequestProperty("Content-Type","application/text; charset=UTF-8");
            Log.i("hoge","doPost start.:" + conn.toString());

            conn.connect();

            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            os.write(postStr.getBytes("UTF-8"));
            os.flush();
            os.close();

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                StringBuffer response = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while((inputLine = reader.readLine()) != null){
                    response.append(inputLine);
                    Log.i("res",inputLine);
                }
                Log.i("hoge","doPost success");
            }
        }catch (IOException e){
            Log.e("hoge","error orz:" + e.getMessage(), e);
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }

        return "hoge";
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(this.loginActivity, "Post完了", Toast.LENGTH_LONG).show();
    }
}
