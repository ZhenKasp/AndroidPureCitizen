package com.example.purecitizen.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.purecitizen.MainActivity;
import com.example.purecitizen.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static com.android.volley.VolleyLog.TAG;


public class HomeFragment extends Fragment {

    private Context context;

    List<Post> posts = new ArrayList<>();
    protected String final_token;

    public class Post {

        private String title;
        private String body;
        private Uri image;

        public Post(String title, String body, Uri image){

            this.title = title;
            this.body = body;
            this.image = image;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return this.body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public Uri getImage() {
            return this.image;
        }

        public void setImage(Uri image) { this.image = image; }
    }

    class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

        private LayoutInflater inflater;
        private List<Post> posts;

        DataAdapter(Context context, List<Post> posts) {
            this.posts = posts;
            this.inflater = LayoutInflater.from(context);
        }
        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = inflater.inflate(R.layout.list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Post post = posts.get(position);
            Picasso.with(getContext()).load(post.getImage()).into(holder.imageView);
            holder.titleView.setText(post.getTitle());
            holder.bodyView.setText(post.getBody());
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView imageView;
            final TextView titleView, bodyView;
            ViewHolder(View view){
                super(view);
                imageView = (ImageView)view.findViewById(R.id.image);
                titleView = (TextView) view.findViewById(R.id.title);
                bodyView = (TextView) view.findViewById(R.id.body);
            }
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            String token = getActivity().getIntent().getStringExtra("token");
            if (token != null) {
                Log.d("token", token);
                final_token = token;

                posts_get();

            } else {
                String error = getActivity().getIntent().getStringExtra("error");
                Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                        error.toString(), Toast.LENGTH_LONG);
                LinearLayout toastContainer = (LinearLayout) toast.getView();
                toastContainer.setBackgroundColor(Color.RED);
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;

    }

    private void posts_get() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://192.168.100.3:3000/api/v1/posts/";
        // Request a string response from the provided URL.
        Log.d("url=", url);
        try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("response=", response);
                            try {
                                JSONObject json = new JSONObject(response);
                                Log.d("json=", json.toString());
                                JSONArray json_posts = json.getJSONArray("post");
                                Log.d("posts=", json_posts.toString());
                                for (int x = 0; x < json_posts.length(); x++){

                                    JSONObject json_post = json_posts.getJSONObject(x);
                                    Log.d("post=", json_post.toString());
                                    final String title = json_post.getString("title");
                                    final String body = json_post.getString("body");
                                    final Uri image_uri = Uri.parse(json_post.getString("image"));
                                    Log.d("title=", title);
                                    Log.d("body=", body);
                                    Log.d("image=", image_uri.toString());


                                    posts.add(new Post (title, body, image_uri));

                                    RecyclerView recyclerView = getActivity().findViewById(R.id.list);
                                    DataAdapter adapter = new DataAdapter(getActivity(), posts);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("Something wrong", response);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Token token=" + final_token);
                    return headers;
                }
            };
            queue.add(stringRequest);
        } catch (Exception e) {
            Log.e("Some error", e.toString());
        }
    }

    private Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    }




}
