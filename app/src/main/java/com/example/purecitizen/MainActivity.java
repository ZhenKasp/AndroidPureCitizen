package com.example.purecitizen;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    List<Post> posts = new ArrayList<>();
    private String final_token;

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

    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        setInitialData();
        posts_get();


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        // создаем адаптер
        DataAdapter adapter = new DataAdapter(this, posts);
        // устанавливаем для списка адаптер
        recyclerView.setAdapter(adapter);




        String token = getIntent().getStringExtra("token");
        if (token != null) {
            Log.d("token", token);
            final_token = token;
        } else {
            String error = getIntent().getStringExtra("error");
            Log.e("Something wrong", error);
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    private void setInitialData(){

        posts.add(new Post ("WTF", "if this post does not exist then there are no others", R.drawable.image1));
        posts.add(new Post ("WTF", "example", R.drawable.image2));
        posts.add(new Post ("WTF", "example", R.drawable.image3));
        posts.add(new Post ("WTF", "example", R.drawable.image4));
        posts.add(new Post ("WTF", "example", R.drawable.image5));
        posts.add(new Post ("WTF", "example", R.drawable.image6));
        posts.add(new Post ("WTF", "example", R.drawable.image7));
        posts.add(new Post ("WTF", "example", R.drawable.gif1));
    }



    private void posts_get() {
        RequestQueue queue = Volley.newRequestQueue(this);
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
                                }

                                try {



                                } catch (Exception e) {
                                    Log.e("Error", e.toString());
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
