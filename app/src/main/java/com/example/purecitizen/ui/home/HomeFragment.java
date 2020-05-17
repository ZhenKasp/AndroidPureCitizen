package com.example.purecitizen.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.purecitizen.R;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    List<Post> posts = new ArrayList<>();
    protected String final_token;

    public class Post {

        private String title;
        private String body;
        private Uri image;
        private  String latitude_longitude;

        public Post(String title, String body, Uri image, String latitude_longitude){

            this.title = title;
            this.body = body;
            this.image = image;
            this.latitude_longitude = latitude_longitude;
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

        public Uri getImage() { return this.image; }

        public void setImage(Uri image) { this.image = image; }

        public String getLatitude_longitude() { return this.latitude_longitude; }

        public void setLatitude_longitude(String  latitude_longitude) { this.latitude_longitude = latitude_longitude; }
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
            holder.latitudeLongitudeView.setText(post.getLatitude_longitude());
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final ImageView imageView;
            final TextView titleView, bodyView, latitudeLongitudeView;
            ViewHolder(View view){
                super(view);
                imageView = (ImageView)view.findViewById(R.id.image);
                titleView = (TextView) view.findViewById(R.id.title);
                bodyView = (TextView) view.findViewById(R.id.body);
                latitudeLongitudeView = (TextView) view.findViewById(R.id.latitude_longitude);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        try {
            String token = getActivity().getIntent().getStringExtra("token");
            if (token != null) {
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
                try {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                JSONArray json_posts = json.getJSONArray("post");
                                for (int x = 0; x < json_posts.length(); x++){

                                    JSONObject json_post = json_posts.getJSONObject(x);

                                    final String title = json_post.getString("title");
                                    final String body = json_post.getString("body");
                                    final String latitude = json_post.getString("latitude");
                                    final String longitude = json_post.getString("longitude");
                                    final Uri image_uri = Uri.parse(json_post.getString("image"));

                                    final String final_location = latitude + " : " + longitude;

                                    posts.add(new Post (title, body, image_uri, final_location));

                                    RecyclerView recyclerView = getActivity().findViewById(R.id.list);
                                    DataAdapter adapter = new DataAdapter(getActivity(), posts);
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
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
            e.printStackTrace();
        }
    }
}
