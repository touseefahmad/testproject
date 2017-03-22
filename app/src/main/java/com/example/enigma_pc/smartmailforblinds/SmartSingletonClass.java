package com.example.enigma_pc.smartmailforblinds;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by enigma-pc on 3/13/2017.
 */

public class SmartSingletonClass extends Application {

    /**
     * Global request Queue for volley
     */


    private static SmartSingletonClass ourInstance = new SmartSingletonClass();
    private RequestQueue mRequestQueue;
    Context context;
    SharedPreferences prefs;
    public static final String TAG = "VolleyPatterns";
    private static Bitmap.CompressFormat DISK_IMAGECACHE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;

    private static int DISK_IMAGECACHE_SIZE = 1024 * 1024 * 10;

    private static int DISK_IMAGECACHE_QUALITY = 100;
    private ImageLoader mImageLoader;

    public SmartSingletonClass() {
    }
    public static SmartSingletonClass getInstance() {
        return ourInstance;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();



        ourInstance = this;
        context = this;

        RequestManager.init(this);
//        mImageLoader = new ImageLoader(mRequestQueue,
//                new ImageLoader.ImageCache() {
//                    private final LruCache<String, Bitmap>
//                            cache = new LruCache<String, Bitmap>(20);
//
//                    @Override
//                    public Bitmap getBitmap(String url) {
//                        return cache.get(url);
//                    }
//
//                    @Override
//                    public void putBitmap(String url, Bitmap bitmap) {
//                        cache.put(url, bitmap);
//                    }
//                });
        //createImageCache();
        /*************************
         * this is check which shared preferences i am using
         *
         * Thes are being used in login Register MyProjects Activity
         */

    }

    /**
     *
     * @return Request queue if not null else generates one and return
     */
    public RequestQueue getRequestQueue() {
        // First time initializes the request queue
        if (mRequestQueue == null) {

            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }
    /**
     * if tag is specified uses this if tag is not specified set default tag
     *
     * @param req
     * @param tag
     */
    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // Set default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        Log.d("Volley request Queue", req.getUrl());
        // add to requset queue
        getRequestQueue().add(req);

    }
    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    private void createImageCache() {

        ImageCacheManager.getInstance().init(this, this.getPackageCodePath(),
                DISK_IMAGECACHE_SIZE, DISK_IMAGECACHE_COMPRESS_FORMAT,
                DISK_IMAGECACHE_QUALITY, ImageCacheManager.CacheType.MEMORY);

    }


    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new BitmapLruCache());
        }
        return this.mImageLoader;
    }
}
/*
Error:Execution failed for task ':app:processDebugManifest'.
        > Manifest merger failed : Attribute application@name value=SingletonClass) from AndroidManifest.xml:13:9-84
        is also present at [:speech-android-wrapper:] AndroidManifest.xml:12:9-90 value=(com.ibm.watson.developer_cloud.android.speech_to_text.v1.WatsonSDK).
        Suggestion: add 'tools:replace="android:name"' to <application> element at AndroidManifest.xml:12:5-45:19 to override.*/
