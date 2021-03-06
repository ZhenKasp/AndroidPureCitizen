package com.example.purecitizen.ui.slideshow;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.example.purecitizen.MainActivity;
import com.example.purecitizen.R;
import com.example.purecitizen.ui.home.HomeFragment;
import java.util.HashMap;
import java.util.Map;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
import static android.app.Activity.RESULT_OK;

public class SlideshowFragment extends Fragment  {

    Double latitude, longitude;
    String final_location;
    public String image_to_download;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        final Button btn_submit = root.findViewById(R.id.btn_submit);
        final ImageButton camera = root.findViewById(R.id.btn_image);
        final ImageView gallery = root.findViewById(R.id.iv_image);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });


        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final EditText et_body = getActivity().findViewById(R.id.et_body);
                    final EditText et_title = getActivity().findViewById(R.id.et_title);

                    if (et_title.getText().toString() != null && et_body.getText().toString() != null && image_to_download != null) {
                        create_post();

                    } else {
                        Toast toast = Toast.makeText(getActivity(),"Поля заполнены неправильно", Toast.LENGTH_LONG);
                        LinearLayout toastContainer = (LinearLayout) toast.getView();
                        toastContainer.setBackgroundColor(Color.RED);
                        toast.show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    final ImageButton camera = getActivity().findViewById(R.id.btn_image);
                    image_to_download = getPath(selectedImage);
                    camera.setImageURI(selectedImage);
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    final ImageView gallery = getActivity().findViewById(R.id.iv_image);

                    gallery.setImageURI(selectedImage);
                }
                break;
        }
    }

    private String getPath(Uri uri){
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);

        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ?", new String[]{document_id}, null
        );
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return path;
    }

    private void create_post() {
        String URL = "http://192.168.100.3:3000/api/v1/posts/";

        final EditText et_body = getActivity().findViewById(R.id.et_body);
        final EditText et_title = getActivity().findViewById(R.id.et_title);
        final SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response != null ) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "Запрос создан успешно", Toast.LENGTH_LONG);
                            LinearLayout toastContainer = (LinearLayout) toast.getView();
                            toastContainer.setBackgroundColor(Color.GREEN);
                            toast.show();
                            go_to_home_fragment();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(com.android.volley.error.VolleyError error) {
                        Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                                error.toString(), Toast.LENGTH_LONG);
                        LinearLayout toastContainer = (LinearLayout) toast.getView();
                        toastContainer.setBackgroundColor(Color.RED);
                        toast.show();
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Token token=" + ((MainActivity)getActivity()).final_token);
                return headers;
            }
        };

        SmartLocation.with(getContext()).location()
                .start(new OnLocationUpdatedListener() {
                    @Override
                    public void onLocationUpdated(Location location) {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        final_location = latitude + " : " + longitude;
                        if (final_location != "0.0 : 0.0") {
                            smr.addStringParam("title", et_title.getText().toString());
                            smr.addStringParam("body", et_body.getText().toString());
                            smr.addFile("image", image_to_download);
                            smr.addStringParam("latitude", String.valueOf(latitude));
                            smr.addStringParam("longitude", String.valueOf(longitude));

                            SmartLocation.with(getContext()).location().stop();

                            RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
                            mRequestQueue.add(smr);
                        }

                    }
                });
    }

    private  void go_to_home_fragment() {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.nav_host_fragment, new HomeFragment(), "NewFragmentTag");
        ft.commit();
    }
}
