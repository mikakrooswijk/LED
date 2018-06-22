package com.example.mikakrooswijk.led;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private TextView usernameText, passwordText;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        passwordText = findViewById(R.id.passwordText);
        usernameText = findViewById(R.id.usernameText);
        url = "http://94.208.5.119:3030/api/security/login";

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    login(usernameText.getText().toString(), passwordText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void login(String username, String password) throws JSONException {

        Log.i("LOGIN", "trying to log in with " + username + " " + password);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("username", username);
        jsonBody.put("password", password);


        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String authorized = response.getString("authorized");
                            String token = response.getString("token");

                            if(authorized == "true"){
                                SharedPreferences preferences;
                                SharedPreferences.Editor edit;

                                preferences = getApplicationContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                                edit = preferences.edit();

                                edit.putString("token", token);
                                edit.commit();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                            }else{
                                Toast.makeText(getApplicationContext(),"wrong username or password", Toast.LENGTH_SHORT);
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"something went wrong", Toast.LENGTH_SHORT);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"something went wrong", Toast.LENGTH_SHORT);
                    }
                });

        requestQueue.add(jsObjRequest);


    }

}
