package com.nombee;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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
        String result = null;

        //Creating JSON Object
        JSONObject userInfo = new JSONObject();
        try {
            userInfo.put("username", this.email);
            userInfo.put("pass", this.pass);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("json","errer");
            return null;
        }

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), userInfo.toString());
        Request request = new Request.Builder().url(SERVER_URL+SERVER_APP).post(requestBody).build();

        OkHttpClient client = new OkHttpClient();

        try {
            Response response = client.newCall(request).execute();
            //java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(java.util.logging.Level.FINEST);
            //java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(java.util.logging.Level.FINEST);
            result = response.body().string();
            Log.i("hoge","doPost success" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "hoge";
    }

    @Override
    protected void onPostExecute(String result){
        Toast.makeText(this.loginActivity, "Post完了", Toast.LENGTH_LONG).show();
    }

    public void sendHttpRequestWithHttpUrlConnection(){
        String postStr = "content=hofe";
        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection)new URL(SERVER_URL+SERVER_APP).openConnection();
            conn.setRequestMethod("POST");
            //conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(postStr.getBytes().length);
            conn.setRequestProperty("Content-Type","application/text; charset=UTF-8");
            Log.i("hoge","doPost start.:" + conn.toString());

            conn.connect();

            //DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            //os.write(postStr.getBytes("UTF-8"));
            //os.write(postStr.getBytes());
            //os.writeBytes(postStr);
            //os.flush();
            //os.close();

            /*
            PrintWriter pw = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(
                            conn.getOutputStream(),"UTF-8"
                    )
            ));
            pw.print(postStr);
            pw.close();
            */

            /*
            PrintStream ps = new PrintStream(conn.getOutputStream());
            ps.print(postStr);
            ps.close();
            */
            // POSTデータ送信処理
            /*
            OutputStream out = null;
            try {
                out = conn.getOutputStream();
                out.write(postStr.getBytes("UTF-8"));
                out.flush();
            } catch (IOException e) {
                // POST送信エラー
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
            */
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write("content=hogehoge");
            writer.flush();
            writer.close();
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
    }
}
