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

public class RegistrationActivity extends AppCompatActivity {


    private String token = null;
    private EditText email;
    private EditText password;
    private EditText first_name;
    private EditText last_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        final TextView to_login = findViewById(R.id.to_login);
        final EditText email = findViewById(R.id.email);
        final EditText password = findViewById(R.id.password);
        final EditText first_name = findViewById(R.id.first_name);
        final EditText last_name = findViewById(R.id.last_name);
        final Button registration_button = findViewById(R.id.registration_button);

        to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registration_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateEmailAddress(email) && validateFirstName(first_name) && validateLastName(last_name) && validatePassword(password)) {
                    registration();
                    if (token != null) {
                        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                }
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

    private boolean validateFirstName(EditText first_name) {
        String firstNamedInput = first_name.getText().toString();
        if(!firstNamedInput.isEmpty() && firstNamedInput.length() >= 3 && firstNamedInput.length() < 15){
            return true;
        } else {
            Toast toast = Toast.makeText(this, "Invalid First Name!", Toast.LENGTH_SHORT);
            LinearLayout toastContainer = (LinearLayout) toast.getView();
            toastContainer.setBackgroundColor(Color.RED);
            toast.show();
            return false;
        }
    }

    private boolean validateLastName(EditText last_name) {
        String lastNameInput = last_name.getText().toString();
        if(!lastNameInput.isEmpty() && lastNameInput.length() >= 3 && lastNameInput.length() < 15){
            return true;
        } else {
            Toast toast = Toast.makeText(this, "Invalid Last Name!", Toast.LENGTH_SHORT);
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

    private void registration()
    {
        EditText username = findViewById(R.id.email);
        EditText first_name = findViewById(R.id.first_name);
        EditText last_name= findViewById(R.id.last_name);
        EditText password= findViewById(R.id.password);

        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String URL = "http://192.168.100.3:3000/api/v1/users/";

            JSONObject jsonBody = new JSONObject();

            jsonBody.put("email", username.getText().toString());
            jsonBody.put("password", password.getText().toString());
            jsonBody.put("first_name", first_name.getText().toString());
            jsonBody.put("last_name", last_name.getText().toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response != null ) {
                                    token = response.getString("token");
                                    Log.d("token = ", token);
                                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                    intent.putExtra("token", token);

                                    startActivity(intent);
                                }

                            } catch (Exception e) {
                                Log.e("Registration error", e.toString());
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "Wrong params", Toast.LENGTH_LONG);
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
