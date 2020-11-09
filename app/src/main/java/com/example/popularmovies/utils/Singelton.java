package com.example.popularmovies.utils;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class Singelton {

    private static Singelton mInstance;
    private RequestQueue mQueue;
    private static Context mContext;


    private Singelton(Context context){
        mContext = context;
        mQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue(){
        if(mQueue==null){
            mQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mQueue;
    }

    public static synchronized Singelton getInstance(Context context){
        if(mInstance==null){
            mInstance = new  Singelton(context);
        }
        return mInstance;

    }

    public<T> void addToRequestQueue(Request<T> request){
        mQueue.add(request);

    }

}

