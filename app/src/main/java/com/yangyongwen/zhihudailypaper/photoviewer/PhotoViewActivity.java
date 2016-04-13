package com.yangyongwen.zhihudailypaper.photoviewer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.login.LoginActivity;
import com.yangyongwen.zhihudailypaper.ui.ObservableFrameLayout;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends AppCompatActivity {

    private static final String TAG= LogUtils.makeLogTag(PhotoViewActivity.class);

    private static final String PHOTO_URL="photo_url";

    private Toolbar mActionBarToolbar;
    private PhotoView mPhoto;
    private PhotoViewAttacher mPhotoViewAttacher;

    private boolean mActionBarShown;

    private Handler mHandler;
    private Runnable mRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        mActionBarToolbar=getActionBarToolbar();

        mActionBarToolbar=getActionBarToolbar();
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mPhoto=(PhotoView)findViewById(R.id.photo);

        String image=getIntent().getStringExtra(PHOTO_URL);

        Picasso.with(this).load(image).into(mPhoto);

        mRunnable=new Runnable() {
            @Override
            public void run() {
                mActionBarToolbar.setVisibility(View.INVISIBLE);
                mActionBarShown=false;
            }
        };
        mHandler=new Handler();

        showActionBar(true);

        mPhoto.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                showActionBar(!mActionBarShown);
            }

            @Override
            public void onOutsidePhotoTap() {
                showActionBar(!mActionBarShown);
            }
        });




    }

    private void showActionBar(final boolean show){
        mHandler.removeCallbacks(mRunnable);
        mActionBarShown=show;
        if(show){
            mActionBarToolbar.setVisibility(View.VISIBLE);
            mHandler.postDelayed(mRunnable,5000);
        }else{
            mActionBarToolbar.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_photoview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        switch (id){
            case R.id.action_save:{
                Bitmap bitmap=((BitmapDrawable)mPhoto.getDrawable()).getBitmap();
                if(bitmap==null){
                    Toast.makeText(this,"保存失败",Toast.LENGTH_SHORT).show();
                    break;
                }
                savePicture(this,bitmap,null);
                Toast.makeText(this,"保存成功",Toast.LENGTH_SHORT).show();
                break;
            }

            default:
        }
        return false;
    }

    public Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                setSupportActionBar(mActionBarToolbar);
            }
        }
        return mActionBarToolbar;
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

        /*
        * 将图片插到系统图库
        * */
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(),file.getAbsolutePath(),fileName,null);
//        }catch (FileNotFoundException e){
//            e.printStackTrace();
//        }

        //通知gallery更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

    }


}
