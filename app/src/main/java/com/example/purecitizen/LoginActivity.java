package com.example.purecitizen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.error.VolleyError;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private String token = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final TextView to_registration = findViewById(R.id.to_registration);
        final Button login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmailAddress(email) && validatePassword(password)){
                    log_in();

                    if (token != null) {
                        Intent intent = new Intent (LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        to_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateEmailAddress(EditText email) {
        String emailInput = email.getText().toString();
        if(!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            return true;
        } else {
            Toast toast = Toast.makeText(this, "Invalid Email Address!", Toast.LENGTH_SHORT);
            LinearLayout toastContainer = (LinearLayout) toast.getView();
            toastContainer.setBackgroundColor(Color.RED);
            toast.show();
            return false;
        }
    }


    private boolean validatePassword(EditText password) {
        String passwordInput = password.getText().toString();
        if(!passwordInput.isEmpty() && passwordInput.length() >= 6){
            return true;
        } else {
            Toast toast = Toast.makeText(this, "Invalid Password!", Toast.LENGTH_SHORT);
            LinearLayout toastContainer = (LinearLayout) toast.getView();
            toastContainer.setBackgroundColor(Color.RED);
            toast.show();
            return false;
        }
    }

    private void log_in()
    {
        EditText usernameEditText = findViewById(R.id.email);
        EditText passwordEditText = findViewById(R.id.password);

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://192.168.100.3:3000/api/v1/session/";

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("email", usernameEditText.getText().toString());
            jsonBody.put("password", passwordEditText.getText().toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("token") != null ) {
                                    token = response.getString("token");
                                    Log.d("token = ", token);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("token", token);

                                    startActivity(intent);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Wrong email or password", Toast.LENGTH_LONG);
                                LinearLayout toastContainer = (LinearLayout) toast.getView();
                                toastContainer.setBackgroundColor(Color.RED);
                                toast.show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error response", error.toString());
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "TimeoutError", Toast.LENGTH_LONG);
                            LinearLayout toastContainer = (LinearLayout) toast.getView();
                            toastContainer.setBackgroundColor(Color.RED);
                            toast.show();
                        }
                    }
            );
            requestQueue.add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
