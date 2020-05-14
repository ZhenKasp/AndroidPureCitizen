package com.example.purecitizen.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.purecitizen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeFragment extends Fragment {

    private Context context;

    List<Post> posts = new ArrayList<>();
    protected String final_token;

    public class Post {

        private String title;
        private String body;
        private int image;

        public Post(String title, String body, int image){

            this.title=title;
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

        public int getImage() {
            return this.image;
        }

        public void setImage(int image) {
            this.image = image;
        }
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
            holder.imageView.setImageResource(post.getImage());
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
                setInitialData();

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

    private void setInitialData(){
        posts.add(new Post ("WTF", "if this post does not exist then there are no others", R.drawable.image1));
        posts.add(new Post ("WTF", "example", R.drawable.image2));
        posts.add(new Post ("WTF", "example", R.drawable.image3));
        posts.add(new Post ("WTF", "example", R.drawable.image4));
        posts.add(new Post ("WTF", "example", R.drawable.image5));
        posts.add(new Post ("WTF", "example", R.drawable.image6));
        posts.add(new Post ("WTF", "example", R.drawable.image7));


    }
    private void posts_get() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://192.168.100.4:3000/api/v1/posts/";
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
                                    String title = json_post.getString("title");
                                    String body = json_post.getString("body");
                                    Log.d("title=", title);
                                    Log.d("body=", body);
                                    posts.add(new Post (title, body, R.drawable.image8));

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
                public Map<String, String> getHeaders() throws AuthFailureError {
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
}
