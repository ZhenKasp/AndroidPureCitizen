package com.example.purecitizen.ui.slideshow;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Picture;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.purecitizen.LoginActivity;
import com.example.purecitizen.MainActivity;
import com.example.purecitizen.R;
import com.example.purecitizen.ui.home.HomeFragment;
import com.example.purecitizen.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SlideshowFragment extends Fragment  {

    String error_response = null;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        final EditText et_body = getActivity().findViewById(R.id.et_body);
        final EditText et_title = getActivity().findViewById(R.id.et_title);
        final Button btn_submit = root.findViewById(R.id.btn_submit);
        final ImageButton image = root.findViewById(R.id.btn_image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final EditText et_body = getActivity().findViewById(R.id.et_body);
                    final EditText et_title = getActivity().findViewById(R.id.et_title);

                    if (et_title.getText().toString() != null && et_body.getText().toString() != null) {

                        create_post();

                        if (error_response == null){
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "Post successfully created", Toast.LENGTH_LONG);
                            LinearLayout toastContainer = (LinearLayout) toast.getView();
                            toastContainer.setBackgroundColor(Color.GREEN);
                            toast.show();

                            go_to_home_fragment();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast toast = Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT);
                    LinearLayout toastContainer = (LinearLayout) toast.getView();
                    toastContainer.setBackgroundColor(Color.RED);
                    toast.show();
                }
            }
        });

        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getContext(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getContext(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            final ImageButton image = getActivity().findViewById(R.id.btn_image);
            image.setImageBitmap(photo);
        }
    }


    private void create_post() {
        FragmentActivity root = getActivity();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            String URL = "http://192.168.100.4:3000/api/v1/posts/";

            JSONObject jsonBody = new JSONObject();
            final EditText et_body = getActivity().findViewById(R.id.et_body);
            final EditText et_title = getActivity().findViewById(R.id.et_title);

            jsonBody.put("title", et_title.getText().toString());
            jsonBody.put("body", et_body.getText().toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getJSONArray("error") == null){
                                    error_response = null;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Something wrong", e.toString());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Error response", error.toString());
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    error.toString(), Toast.LENGTH_LONG);
                            LinearLayout toastContainer = (LinearLayout) toast.getView();
                            toastContainer.setBackgroundColor(Color.RED);
                            toast.show();
                        }
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Token token=" + ((MainActivity)getActivity()).final_token);
                    return headers;
                }
            };
            requestQueue.add(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private  void go_to_home_fragment() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, new HomeFragment(), "NewFragmentTag");
        ft.commit();
    }
}
