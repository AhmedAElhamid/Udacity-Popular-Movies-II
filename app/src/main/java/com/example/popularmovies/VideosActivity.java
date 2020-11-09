package com.example.popularmovies;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.popularmovies.model.Video;
import com.example.popularmovies.utils.JsonUtils;
import com.example.popularmovies.utils.NetworkUtils;
import com.example.popularmovies.utils.Singelton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity {
    private ListView listView;
    private static ArrayAdapter<String> arrayAdapter;
    private TextView mTextView;
    private String API_KEY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        API_KEY=getResources().getString(R.string.api_key);
        mTextView = (TextView) findViewById(R.id.no_trailer_textView);
        Intent intent = getIntent();
        if(intent.hasExtra(MovieDetails.MOVIE_ID_EXTRA)){
            String id = intent.getStringExtra(MovieDetails.MOVIE_ID_EXTRA);
            String url = NetworkUtils.buildVideosUrl(id,API_KEY).toString();
            getVideos(this,url);
        }

    }

    private void setListView(final List<Video> videos){
        List<String> names = new ArrayList<>();
        for(int i=0;i<videos.size();i++){
            names.add(videos.get(i).getName());
        }
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.videos_list_layout,names);
        listView = (ListView) findViewById(R.id.videos_listView);
        if(videos.size()>0) {
            listView.setAdapter(arrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(videos.get(i).getKey())));
                }
            });
        }else {
            listView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
        }
    }

    private void getVideos(Context context,String url){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Log.d(TAG,response.toString());
                List<Video> videos = JsonUtils.parseVideosJson(response);
                setListView(videos);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        Singelton.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }

    public void onBackClicked(View view) {
        onBackPressed();
    }
}