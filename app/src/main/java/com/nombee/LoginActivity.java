package com.nombee;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
 **/

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * Server URL, App Name
     */
    //private static final String SERVER_URL = "https://nombee-app.appspot.com/";
    //private static final String SERVER_LOGIN = "login";
    //private static final String SERVER_URL = "http://1-dot-erikotestserver2.appspot.com/";
    //private static final String SERVER_LOGIN = "testservletapp";

    /**
     * Static Strings
     */
    private static final String REQUEST_SUCCESS = "success";

    /**
     * Keep track of the http request tasks to ensure we can cancel it if requested.
     */
    private UserLoginTask loginTask = null;
    private GetUserInfoTask getUserInfoTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View fbLoginView;

    // Shared Preference
    SharedPreferences sharedPreferences;

    //Globals
    Globals globals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        //Get globals
        globals = (Globals)this.getApplication();

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        sharedPreferences = getSharedPreferences("TokenSave", Context.MODE_PRIVATE);

        //atask.execute(arg);

        // start Facebook Login
/**
        callbackManager = CallbackManager.Factory.create();
        LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(LoginActivity.this, "Sucess desu", Toast.LENGTH_SHORT);
                mEmailView.setText(
                        "User ID: "
                                + loginResult.getAccessToken().getUserId()
                                + "\n" +
                                "Auth Token: "
                                + loginResult.getAccessToken().getToken()
                );
                Log.d("Token", loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this, "Cancel desu", Toast.LENGTH_SHORT);
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LoginActivity.this, "Error desu", Toast.LENGTH_SHORT);
            }
        });
 **/
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (loginTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            loginTask = new UserLoginTask(email, password);
            loginTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String result = null;
            String userInfoStr = null;

            //Creating JSON Object
            JSONObject userInfo = new JSONObject();
            try {
                userInfo.put("username", this.mEmail);
                userInfo.put("pass", this.mPassword);
                userInfoStr = userInfo.toString();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("json", "error");
                return null;
            }

            //Send user info via https
            HttpURLConnection conn = null;
            try{
                conn = (HttpURLConnection)new URL(Constants.SERVER_URL+ Constants.LOGIN_URL).openConnection();
                conn.setRequestMethod("POST");
                //conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setFixedLengthStreamingMode(userInfoStr.getBytes().length);
                conn.setRequestProperty("Content-Type","application/text; charset=UTF-8");
                Log.i("hoge","doPost start.:" + conn.toString());

                conn.connect();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(userInfoStr);
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

                    //Create JSON Object from response data
                    JSONObject resJson = new JSONObject(response.toString());
                    String loginResult = resJson.getString("result");
                    Log.i("hoge", "login result: " + loginResult);

                    if (loginResult.equals(REQUEST_SUCCESS)) {
                        String authToken = resJson.getString("auth_token");
                        int expire = resJson.getInt("expire");

                        //Save Token in SharedPreference
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("auth_token", authToken);
                        editor.apply();
                        Log.i("hoge", "login success:" + authToken);
                        return true;
                    } else {
                        Log.i("hoge", "login failed");
                        return false;
                    }
                }
            }catch (IOException e){
                Log.e("hoge","error orz:" + e.getMessage(), e);
            } catch (JSONException je) {
                Log.e("hoge", "JSON error: " + je.getMessage(), je);
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            loginTask = null;
            showProgress(false);

            if (success) {
                //Get User Info
                getUserInfoTask = new GetUserInfoTask();
                getUserInfoTask.execute((Void) null);

                //Intent intent = new Intent();
                //intent.setClassName("com.nombee", "com.nombee.TopActivity");
                //intent.setClassName("com.nombee", "com.nombee.TestActivity");
                //startActivity(intent);
                //finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            loginTask = null;
            showProgress(false);
        }

    }

    /**
     * AsyncTask for HTTP Request to get user info
     */
    public class GetUserInfoTask extends AsyncTask<Void, Void, Boolean> {
        private final String authToken;


        GetUserInfoTask(){

            SharedPreferences data = getSharedPreferences("TokenSave", Context.MODE_PRIVATE);
            this.authToken = data.getString("auth_token","n/a");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            //Send user info via https

            // comment ouf for testing
            /*
            HttpURLConnection conn = null;
            try{
                conn = (HttpURLConnection)new URL(Constants.SERVER_URL+ Constants.USER_URL).openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type","application/text; charset=UTF-8");
                conn.setRequestProperty("Authorization","NombeeToken:" + authToken);
                Log.i("hoge","User Info Requested:" + conn.toString());
                conn.connect();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                    StringBuffer response = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    while((inputLine = reader.readLine()) != null){
                        response.append(inputLine);
                        Log.i("res",inputLine);
                    }

                    //Create JSON Object from response data
                    JSONObject resJson = new JSONObject(response.toString());
                    String requestResult = resJson.getString("result");
                    Log.i("hoge", "User Info Request Result: " + requestResult);

                    if (requestResult.equals(REQUEST_SUCCESS)) {
                        globals.userName = resJson.getString("userName");
                        String birthDate[] = resJson.getString("birthDate").split("/");
                        globals.birthYear = Integer.parseInt(birthDate[0]);
                        globals.birthMonth = Integer.parseInt(birthDate[1]);
                        globals.birthDay = Integer.parseInt(birthDate[2]);

                        Log.i("hoge", "user info success:" + globals.userName);
                        return true;
                    } else {
                        Log.i("hoge", "user info failed");
                        return false;
                    }
                }
            }catch(IOException e){
                Log.e("hoge","error orz:" + e.getMessage(), e);
            } catch (JSONException je) {
                Log.e("hoge", "JSON error: " + je.getMessage(), je);
            }finally {
                if(conn != null){
                    conn.disconnect();
                }
            }

            return null;
            */
            return true;
        }



        @Override
        protected void onPostExecute(final Boolean success) {
            getUserInfoTask = null;
            showProgress(false);

            if (success) {
                //Open Top Activity
                Intent intent = new Intent();
                intent.setClassName("com.nombee", "com.nombee.TopActivity");
                //intent.setClassName("com.nombee", "com.nombee.TestActivity");
                startActivity(intent);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            getUserInfoTask = null;
            showProgress(false);
        }
    }
}

