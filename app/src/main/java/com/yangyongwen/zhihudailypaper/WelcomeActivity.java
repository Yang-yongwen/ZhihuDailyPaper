package com.yangyongwen.zhihudailypaper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
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

import com.yangyongwen.zhihudailypaper.homePage.HomePageActivity;
import com.yangyongwen.zhihudailypaper.homePage.HomePageModel;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContract;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

public class WelcomeActivity extends AppCompatActivity {


    private static final String TAG= LogUtils.makeLogTag(WelcomeActivity.class);


    private ImageView mImageView;
    private Context mContext;


    HomePageModel homePageModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=this;

        mImageView=(ImageView)findViewById(R.id.welcome_icon);




        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                ScaleAnimation scaleAnimation=new ScaleAnimation(1,1.2f,1,1.2f,mImageView.getWidth()/2,mImageView.getHeight()/2);
                scaleAnimation.setDuration(30);
                scaleAnimation.setFillAfter(true);

                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        Intent intent=new Intent(mContext,HomePageActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        },300);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                mImageView.startAnimation(scaleAnimation);

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            ContentValues contentValues=new ContentValues();
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_STORY_ID,10002);
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_DATE,"20160101");
            contentValues.put(ZhihuContract.TableStory.COLUMN_NAME_TITLE, "测试");
            getApplicationContext().getContentResolver().insert(ZhihuContentProvider.STORY_CONTENT_URI, contentValues);

            String[] mProjection={
                    ZhihuContract.TableStory.COLUMN_NAME_TITLE,

            };

            String mSelectionClause=ZhihuContract.TableStory.COLUMN_NAME_STORY_ID+" = ?";
            String[] mSelectionArgs={"10000"};

            Cursor cursor=getContentResolver().query(ZhihuContentProvider.STORY_CONTENT_URI, mProjection, mSelectionClause,
                    mSelectionArgs, ZhihuContract.TableStory.COLUMN_NAME_STORY_ID + " ASC");

            if (cursor.moveToFirst()){
                do{
                    String data = cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStory.COLUMN_NAME_TITLE));
                    Toast.makeText(getApplicationContext(),data, Toast.LENGTH_SHORT).show();
                    // do what ever you want here
                }while(cursor.moveToNext());
            }
            cursor.close();

            homePageModel=new HomePageModel(this);



            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
