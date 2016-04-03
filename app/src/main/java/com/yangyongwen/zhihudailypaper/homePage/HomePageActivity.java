package com.yangyongwen.zhihudailypaper.homePage;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yangyongwen.zhihudailypaper.login.LoginActivity;
import com.yangyongwen.zhihudailypaper.network.NetworkRequest;
import com.yangyongwen.zhihudailypaper.network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContract;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.setting.SettingActivity;
import com.yangyongwen.zhihudailypaper.themeList.ThemeListFragment;
import com.yangyongwen.zhihudailypaper.ui.NavThemeItemView;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;
import com.yangyongwen.zhihudailypaper.common.Model;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.UpdatableView;
import com.yangyongwen.zhihudailypaper.common.UserActionEnum;

import java.util.ArrayList;

/**
 * Created by yangyongwen on 16/2/16.
 */
public class HomePageActivity extends AppCompatActivity implements NavThemeItemView.FollowListener,
        NavThemeItemView.SelectListener{

    private final static String TAG = LogUtils.makeLogTag(HomePageActivity.class);
    public final static String PRESENTER_TAG = "Presenter";
    private final static int FOLLOW_THEME=0;
    private final static int UNFOLLOW_THEME=1;

    private final static String THEME_ID="theme_id";

    private HandlerThread mHandlerThread;
    private Handler mHandler;


    private FragmentManager mFragmentManager;


    private Toolbar mActionBarToolbar;
    private DrawerLayout mDrawerLayout;
    private LinearLayout mThemeList;
    private LinearLayout mHomeItem;
    private ArrayList<NavThemeItemView> mNavThemeItemViews;
    private MenuItem mThemeFollowItem;

    private int mSelectThemeIndex=0;
    private int mFollowThemeNum=0;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.homepage_act);

        mHandlerThread=new HandlerThread("ActWorkerThread");
        mHandlerThread.start();
        mHandler=new WorkerHandler(mHandlerThread.getLooper());

        mFragmentManager=getSupportFragmentManager();

        mActionBarToolbar = getActionBarToolbar();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setTitle("首页");
        mActionBarToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        LinearLayout drawer_home_item = (LinearLayout) findViewById(R.id.drawer_home_item);
        drawer_home_item.setActivated(true);

        

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mActionBarToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        addPresenterFragment(R.id.homepage_frag, new HomePageModel(getApplicationContext()), null, null);


        mThemeList = (LinearLayout) findViewById(R.id.theme_item_list);
        mHomeItem = (LinearLayout) findViewById(R.id.drawer_home_item);
        mHomeItem.setBackgroundColor(Color.parseColor("#E0E0E0"));

        createDrawerThemeItem();
        NetworkRequestProxy.getInstance(getApplicationContext()).requestThemeList(new NetworkRequest.RequestCallback() {
            @Override
            public void onSuccess() {
                LogUtils.LOGD(TAG,"load theme list success");
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        createDrawerThemeItem();
                    }
                });
            }

            @Override
            public void onFailure() {
                Toast.makeText(getApplicationContext(), "主题列表加载失败", Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onResume(){
        super.onResume();

    }


    public HomePagePresenter addPresenterFragment(int updatableViewResId, Model model, QueryEnum[] queries,
                                                  UserActionEnum[] actions){
        FragmentManager fragmentManager=getSupportFragmentManager();

        HomePagePresenter presenter = (HomePagePresenter) fragmentManager.findFragmentByTag(
                PRESENTER_TAG);

        if(presenter==null){
            presenter = new HomePagePresenter();
            setUpPresenter(presenter, fragmentManager, updatableViewResId, model, queries, actions);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(presenter, PRESENTER_TAG);
            fragmentTransaction.commit();
        }else {
            setUpPresenter(presenter, fragmentManager, updatableViewResId, model, queries, actions);
        }

        return presenter;

    }

    public void setUpPresenter(HomePagePresenter presenter, FragmentManager fragmentManager,
                               int updatableViewResId, Model model, QueryEnum[] queries,
                               UserActionEnum[] actions){
        UpdatableView ui = (UpdatableView) fragmentManager.findFragmentById(
                updatableViewResId);
        presenter.setModel(model);
        presenter.setUpdatableView(ui);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if(mSelectThemeIndex==-1){
            getMenuInflater().inflate(R.menu.menu_main, menu);
            mThemeFollowItem=null;
        }else{
            getMenuInflater().inflate(R.menu.menu_theme, menu);
            mThemeFollowItem=menu.findItem(R.id.action_follow_theme);
            if(mNavThemeItemViews.get(mSelectThemeIndex).isFollowed()){
                mThemeFollowItem.setIcon(R.drawable.theme_remove);
            }else{
                mThemeFollowItem.setIcon(R.drawable.theme_add);
            }
        }

        return true;
    }

    private class WorkerHandler extends Handler{

        public WorkerHandler(Looper looper){
            super(looper);
        }


        @Override
        public void handleMessage(Message message){

        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_message) {
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        } else if (id == R.id.action_settings) {
           // createDrawerThemeItem();
            Intent intent=new Intent(this, SettingActivity.class);
            this.startActivity(intent);

        }else if(id==R.id.action_follow_theme){

            if(mNavThemeItemViews.get(mSelectThemeIndex).isFollowed()){
                onThemeUnFollow(mNavThemeItemViews.get(mSelectThemeIndex).getThemeId());
            }else {
                onThemeFollow(mNavThemeItemViews.get(mSelectThemeIndex).getThemeId());
            }

        }

        return super.onOptionsItemSelected(item);
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


    public void homeItemClicked(View view) {
        mHomeItem.setBackgroundColor(Color.parseColor("#E0E0E0"));
        mDrawerLayout.closeDrawers();
        if(mSelectThemeIndex!=-1) {
            mNavThemeItemViews.get(mSelectThemeIndex).unSelectTheme();
            invalidateOptionsMenu();
            mActionBarToolbar.setTitle("首页");

            mFragmentManager.popBackStack();

        }
        mSelectThemeIndex=-1;
    }


    private void createDrawerThemeItem() {
        Cursor cursor = getContentResolver().query(ZhihuContentProvider.STORY_THEME_CONTENT_URI, null, null,
                null, ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID + " ASC");
        mNavThemeItemViews=new ArrayList<NavThemeItemView>();
        mThemeList.removeAllViews();
        mFollowThemeNum=0;
        mSelectThemeIndex=-1;
        int index=0;
        if (cursor.moveToFirst()) {
            do {
                int themeId = cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID));
                String themeTitle = cursor.getString(cursor.getColumnIndex(ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_NAME));
                int i = cursor.getInt(cursor.getColumnIndex(ZhihuContract.TableStoryTheme.COLUMN_NAME_ISSUBCRIBED));
                boolean isFollow = i == 1 ? true : false;

                NavThemeItemView navThemeItemView = new NavThemeItemView(this);
                navThemeItemView.setTitle(themeTitle);
                navThemeItemView.setFollowed(isFollow);
                navThemeItemView.setThemeId(themeId);
                navThemeItemView.setFollowListener(this);
                navThemeItemView.setSelectListener(this);
                mNavThemeItemViews.add(navThemeItemView);
                mThemeList.addView(navThemeItemView);

                if(isFollow){
                    resetThemeListOrder(FOLLOW_THEME, index);
                    ++mFollowThemeNum;
                }
                ++index;

            } while (cursor.moveToNext());
        }
        cursor.close();



    }

    @Override
    public void onThemeSelect(int themeId) {
        int themeIndex=themeIdToIndex(themeId);
        if(themeIndex==mSelectThemeIndex){
            mDrawerLayout.closeDrawers();
            return;
        }
        mActionBarToolbar.setTitle(mNavThemeItemViews.get(themeIndex).getTitle());
        if(mSelectThemeIndex==-1){
            mHomeItem.setBackgroundColor(Color.parseColor("#f5f5f5"));
        }else {
            mNavThemeItemViews.get(mSelectThemeIndex).unSelectTheme();
        }
        int mLastSelectIndex=mSelectThemeIndex;
        mSelectThemeIndex=themeIndex;
        mNavThemeItemViews.get(mSelectThemeIndex).selectTheme();
        mDrawerLayout.closeDrawers();
        invalidateOptionsMenu();

        //TODO:      start a theme list fragment

        Bundle bundle=new Bundle();
        bundle.putInt(THEME_ID, themeId);

        if(mLastSelectIndex!=-1){
            mFragmentManager.popBackStack();
        }
        Fragment fragment=new ThemeListFragment();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction=mFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.homepage_frag_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out,
                R.anim.abc_popup_enter,R.anim.abc_popup_exit);
        fragmentTransaction.commit();


    }

    private int themeIdToIndex(int themeId){
        for(int i=0;i<mNavThemeItemViews.size();++i){
            if(mNavThemeItemViews.get(i).getThemeId()==themeId){
                return i;
            }
        }
        return -1;
    }


    @Override
    public void onThemeFollow(final int themeId){
        Toast.makeText(getApplicationContext(),"关注成功,关注内容会在首页呈现",Toast.LENGTH_SHORT).show();

        int selectThemeId=-1;

        if(mSelectThemeIndex!=-1){
            selectThemeId=mNavThemeItemViews.get(mSelectThemeIndex).getThemeId();
        }


        if(mThemeFollowItem!=null && selectThemeId==themeId){
            mThemeFollowItem.setIcon(R.drawable.theme_remove);
        }

        int index=themeIdToIndex(themeId);



        mNavThemeItemViews.get(index).followTheme();

        resetThemeListOrder(FOLLOW_THEME, index);

        mSelectThemeIndex=themeIdToIndex(selectThemeId);

        ++mFollowThemeNum;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues=new ContentValues();
                contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_ISSUBCRIBED, 1);
                String selection=ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID+" = ?";
                String[] selectionArgs={Integer.toString(themeId)};
                getContentResolver().update(ZhihuContentProvider.STORY_THEME_CONTENT_URI,contentValues,selection,selectionArgs);
            }
        });

        //TODO:   add the theme story to homepage
    }


    public void onThemeUnFollow(final int themeId){
        Toast.makeText(getApplicationContext(),"已取消关注",Toast.LENGTH_SHORT).show();


        int selectThemeId=-1;

        if(mSelectThemeIndex!=-1){
            selectThemeId=mNavThemeItemViews.get(mSelectThemeIndex).getThemeId();
        }

        if(mThemeFollowItem!=null && selectThemeId==themeId){
            mThemeFollowItem.setIcon(R.drawable.theme_add);
        }

        int index=themeIdToIndex(themeId);

        mNavThemeItemViews.get(index).unFollowTheme();

        resetThemeListOrder(UNFOLLOW_THEME, index);

        mSelectThemeIndex=themeIdToIndex(selectThemeId);

        --mFollowThemeNum;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ContentValues contentValues=new ContentValues();
                contentValues.put(ZhihuContract.TableStoryTheme.COLUMN_NAME_ISSUBCRIBED, 0);
                String selection=ZhihuContract.TableStoryTheme.COLUMN_NAME_THEME_ID+" = ?";
                String[] selectionArgs={Integer.toString(themeId)};
                getContentResolver().update(ZhihuContentProvider.STORY_THEME_CONTENT_URI,contentValues,selection,selectionArgs);
            }
        });

        //TODO   remove the theme story from homepage

        
    }

    private void resetThemeListOrder(int action,int themeIndex){
        NavThemeItemView navThemeItemView=mNavThemeItemViews.get(themeIndex);

        if(action==FOLLOW_THEME){

            mThemeList.removeView(navThemeItemView);
            mThemeList.addView(navThemeItemView, mFollowThemeNum);

            for(int i=themeIndex;i>mFollowThemeNum;--i){
                mNavThemeItemViews.set(i,mNavThemeItemViews.get(i-1));
            }
            mNavThemeItemViews.set(mFollowThemeNum,navThemeItemView);

        }else if(action== UNFOLLOW_THEME) {
            mThemeList.removeView(navThemeItemView);
            mThemeList.addView(navThemeItemView,mFollowThemeNum-1);

            for (int i=themeIndex;i<mFollowThemeNum-1;++i){
                mNavThemeItemViews.set(i,mNavThemeItemViews.get(i+1));
            }
            mNavThemeItemViews.set(mFollowThemeNum-1,navThemeItemView);

        }
    }



    @Override
    public void onBackPressed(){

        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
            return;
        }

        mHomeItem.setBackgroundColor(Color.parseColor("#E0E0E0"));
        if(mSelectThemeIndex!=-1) {
            mNavThemeItemViews.get(mSelectThemeIndex).unSelectTheme();
            invalidateOptionsMenu();
            mActionBarToolbar.setTitle("首页");
        }
        mSelectThemeIndex=-1;
        super.onBackPressed();
    }





}