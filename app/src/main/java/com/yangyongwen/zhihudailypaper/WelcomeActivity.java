package com.yangyongwen.zhihudailypaper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.yangyongwen.zhihudailypaper.homePage.HomePageActivity;
import com.yangyongwen.zhihudailypaper.homePage.HomePageModel;
import com.yangyongwen.zhihudailypaper.network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.network.ZhihuStrRequest;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContract;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WelcomeActivity extends AppCompatActivity {


    private static final String TAG= LogUtils.makeLogTag(WelcomeActivity.class);

    private static final String SPLASH_URL="http://news-at.zhihu.com/api/4/start-image/1080*1776";


    private ImageView mImageView;
    private Context mContext;


    HomePageModel homePageModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext=this;
        mImageView=(ImageView)findViewById(R.id.welcome_icon);
        File pictureDirectory=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Zhihu");
        if(!pictureDirectory.exists()){
            pictureDirectory.mkdir();
        }
        File image=new File(pictureDirectory,"splash.jpg");
        if(image.exists()){
            try {
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig= Bitmap.Config.ARGB_8888;
                Bitmap bitmap=BitmapFactory.decodeStream(new FileInputStream(image),null,options);
                mImageView.setImageBitmap(bitmap);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }

        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                final ScaleAnimation scaleAnimation=new ScaleAnimation(1,1.2f,1,1.2f,mImageView.getWidth()/2,mImageView.getHeight()/2);
                scaleAnimation.setDuration(3000);
                scaleAnimation.setFillAfter(true);

                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent = new Intent(mContext, HomePageActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 300);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.startAnimation(scaleAnimation);
                    }
                },300);

            }
        });


        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                saveSplashImage();
                return null;
            }
        }.execute();


    }


    private void saveSplashImage(){
        NetworkRequestProxy.getInstance(getApplicationContext());
        final RequestQueue queue= NetworkRequestProxy.getQueue();

        Response.Listener<String> listener = new Response.Listener<String>() {
            @Override
            public void onResponse(final String response) {
                String url=getUrl(response);
                url=url.replaceAll( "\\\\", "");
                Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>(){
                    @Override
                    public void onResponse(final Bitmap bitmap) {
                        savePicture(getApplicationContext(), bitmap, "splash.jpg");
                    }
                };
                Response.ErrorListener errorListener = new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError arg0) {
                        LogUtils.LOGD(TAG,"request splash failed");
                    }
                };
                ImageRequest imageRequest=new ImageRequest(url,listener,1080,1776,Bitmap.Config.ARGB_8888,errorListener);
                queue.add(imageRequest);
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {
                LogUtils.LOGD(TAG,"request splash url failed");
            }
        };
        ZhihuStrRequest stringRequest=new ZhihuStrRequest(SPLASH_URL,listener,errorListener);
        queue.add(stringRequest);
    }

    private String getUrl(String str){
        int i=str.indexOf("http");
        return str.substring(i,str.length()-2);
    }




    private void savePicture(Context context,Bitmap bmp,String fileName){
        File pictureDirectory=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Zhihu");
        if(!pictureDirectory.exists()){
            pictureDirectory.mkdir();
        }
        if(fileName==null){
            fileName=System.currentTimeMillis()+".jpg";
        }
        File file=new File(pictureDirectory,fileName);
        try {
            FileOutputStream fos=new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,fos);
            fos.flush();
            fos.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
