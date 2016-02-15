package com.yangyongwen.zhihudailypaper;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yangyongwen.zhihudailypaper.HomePage.HomePageModel;
import com.yangyongwen.zhihudailypaper.Provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.Provider.ZhihuContract;

public class MainActivity extends AppCompatActivity {

    HomePageModel homePageModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
