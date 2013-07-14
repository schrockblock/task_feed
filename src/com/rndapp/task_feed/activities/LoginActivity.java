package com.rndapp.task_feed.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;
import com.google.gson.Gson;
import com.rndapp.task_feed.R;
import com.rndapp.task_feed.api.ServerCommunicator;
import com.rndapp.task_feed.models.SignInModel;
import com.rndapp.task_feed.models.User;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: ell
 * Date: 7/13/13
 * Time: 2:12 PM
 */
public class LoginActivity extends SherlockActivity implements View.OnClickListener{
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences sp = getSharedPreferences(ServerCommunicator.API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
        String apiKey = sp.getString("api_key", "");
        if (!apiKey.equals("")){
            startActivity(new Intent(this, FeedActivity.class));
            finish();
        }

        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.create_account_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_button:
                EditText userField = (EditText)findViewById(R.id.username_field);
                EditText passField = (EditText)findViewById(R.id.password_field);
                if (!userField.getText().toString().equals("") && !passField.getText().toString().equals("")){
                    findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                    new LoginTask().execute(userField.getText().toString(), passField.getText().toString());
                }
                break;
            case R.id.create_account_button:
                startActivity(new Intent(this, CreateAccountActivity.class));
                break;
        }
    }

    private class LoginTask extends AsyncTask<String, String, Object> {
        private boolean errored;
        private String errorText;

        /*
        * params[0] = username
        * params[1] = password
        */
        @Override
        protected Object doInBackground(String... params) {
            ServerCommunicator server = new ServerCommunicator(LoginActivity.this);

            //get user details
            SignInModel signInModel = new SignInModel();
            signInModel.setUsername(params[0]);
            signInModel.setPassword(params[1]);

                JSONObject signInJson = null;
                try {
                    signInJson = new JSONObject(new Gson().toJson(signInModel));
                }catch (Exception e){
                    e.printStackTrace();
                }
                startSession(LoginActivity.this, signInJson);

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            ((TextView)findViewById(R.id.update)).setText(values[0]);
        }

        private void startSession(Context context, JSONObject userAndPwd){
            publishProgress("Starting session...");
            ServerCommunicator server = new ServerCommunicator(context);
            try{
                String json = server.postToEndpointUnauthed("session", userAndPwd, true);
                Log.d("Received from /sessions", json);
                JSONObject jsob = new JSONObject(json);
                if (jsob.has("api_key") && jsob.getString("api_key") != null){
                    String apiKey = jsob.getString("api_key");
                    saveApiKey(apiKey);
                    //create user using Gson
                    user = new Gson().fromJson(json, User.class);
                }else if (jsob.has("errors")){
                    //error
                    errored = true;
                    errorText = jsob.getString("errors");
                }else {
                    //error
                    errored = true;
                    errorText = "Unknown Error";
                }
            }catch(Exception e){
                e.printStackTrace();
                //error
                errored = true;
                errorText = e.getLocalizedMessage();
            }
        }

        private void saveApiKey(String apiKey){
            SharedPreferences sp = getSharedPreferences(ServerCommunicator.API_KEY_PREFERENCE, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editPrefs = sp.edit();
            //store api key
            editPrefs.putString("api_key", apiKey);
            editPrefs.commit();
        }

        @Override
        protected void onPostExecute(Object o){
            new Handler().post(new Runnable(){
                @Override
                public void run() {
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    //go to the next activity
                    if (!errored && user != null){
                        LoginActivity.this
                                .startActivity(new Intent(LoginActivity.this, FeedActivity.class));
                    }else{
                        ((TextView)findViewById(R.id.update)).setText(errorText);
                    }
                }
            });
        }
    }
}
