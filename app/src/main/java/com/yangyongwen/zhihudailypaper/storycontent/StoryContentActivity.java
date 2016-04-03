package com.yangyongwen.zhihudailypaper.storycontent;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.common.Model;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.UpdatableView;
import com.yangyongwen.zhihudailypaper.common.UserActionEnum;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryDetail;
import com.yangyongwen.zhihudailypaper.homePage.HomePageAdapter;
import com.yangyongwen.zhihudailypaper.homePage.HomePagePresenter;
import com.yangyongwen.zhihudailypaper.login.LoginActivity;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by samsung on 2016/3/15.
 */
public class StoryContentActivity extends AppCompatActivity implements Model.ModelUpdatedListener{

    private static final String TAG= LogUtils.makeLogTag(StoryContentActivity.class);

    private static final String PRESENTER_TAG="fragment_tag";

    private Toolbar mActionBarToolbar;

    private ViewPager mViewPager;
    StoryContentModel storyContentModel;
    private String mStoryId;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.storycontent_act);

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


        Intent intent=getIntent();

        ArrayList<String> ids=intent.getStringArrayListExtra(HomePageAdapter.STORY_ID);
        StoryContentAdapter storyContentAdapter=new StoryContentAdapter(getSupportFragmentManager());
        storyContentAdapter.setStoryIdList(ids);
        storyContentAdapter.setCurrentId(intent.getStringExtra(HomePageAdapter.CURRENT_ID));
//
//
//        mViewPager=(ViewPager)findViewById(R.id.storycontent_viewpager);
//        mViewPager.setAdapter(storyContentAdapter);
//        mViewPager.setCurrentItem(storyContentAdapter.getCurrentItem());

        String id=intent.getStringExtra(HomePageAdapter.CURRENT_ID);
        mStoryId=id;
        storyContentModel=new StoryContentModel(getApplicationContext(),ids,id);

//        storyContentModel.setModelUpdateListener(this);
//        Bundle bundle=new Bundle();
//        bundle.putString("story_id",id);
//        storyContentModel.requestModelUpdate(StoryContentModel.StoryContentActionEnum.INIT, bundle, null);

        addPresenterFragment(R.id.storycontent_frag,storyContentModel,null,null);

    }

    public void onModelUpdateSuccess(QueryEnum queryEnum,Bundle bundle){


        final StoryDetail storyDetail=storyContentModel.getStoryDetail(mStoryId);

        final Context context=this;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                TextView textView1=(TextView) findViewById(R.id.story_title1);
//                textView1.setText(storyDetail.getTitle());
//
//                TextView textView2=(TextView) findViewById(R.id.story_icon_resource1);
//                textView2.setText(storyDetail.getImage_source());
//
//                ImageView imageView = (ImageView) findViewById(R.id.story_icon1);
//                Picasso.with(context).load(storyDetail.getImage()).into(imageView);
//
//                WebView webView = (WebView) findViewById(R.id.story_detail_web_view1);
//                WebSettings webSettings = webView.getSettings();
//                webSettings.setJavaScriptEnabled(true);
//                webView.setWebViewClient(new WebViewClient());
//                setWebView(webView, storyDetail.getBody());



            }
        });

    }

    private void setWebView(WebView webView,String body){
        StringBuilder sb=new StringBuilder();
        sb.append("<HTML><HEAD><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
        sb.append(body);
        sb.append("</body></HTML>");
        webView.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);
    }

    public void onModelUpdateError(QueryEnum queryEnum){
        StoryDetail storyDetail=storyContentModel.getStoryDetail(mStoryId);
        int i;
    }





    public StoryContentPresenter addPresenterFragment(int updatableViewResId, Model model, QueryEnum[] queries,
                                                  UserActionEnum[] actions){
        FragmentManager fragmentManager=getSupportFragmentManager();

        StoryContentPresenter presenter = (StoryContentPresenter) fragmentManager.findFragmentByTag(
                PRESENTER_TAG);

        if(presenter==null){
            presenter = new StoryContentPresenter();
            setUpPresenter(presenter, fragmentManager, updatableViewResId, model, queries, actions);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(presenter, PRESENTER_TAG);
            fragmentTransaction.commit();
        }else {
            setUpPresenter(presenter, fragmentManager, updatableViewResId, model, queries, actions);
        }

        return presenter;

    }

    public void setUpPresenter(StoryContentPresenter presenter, FragmentManager fragmentManager,
                               int updatableViewResId, Model model, QueryEnum[] queries,
                               UserActionEnum[] actions){
        UpdatableView ui = (UpdatableView) fragmentManager.findFragmentById(
                updatableViewResId);
        presenter.setModel(model);
        presenter.setUpdatableView(ui);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_storycontent, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        switch (id){
            case R.id.action_share:{
                onActionBarShowOrHide(true);

//                Intent intent=new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("http://www.baidu.com"));
//                startActivity(intent);

                break;
            }
            case R.id.action_collect:{
                Intent intent=new Intent(this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;
            }
            case R.id.action_comment:{

                break;
            }
            case R.id.action_praise:{

                break;
            }
            default:
        }

        return false;
    }




    private void onActionBarShowOrHide(boolean shown){
        if(shown){
            ViewCompat.animate(mActionBarToolbar).translationY(0).alpha(1).setDuration(300).
                    setInterpolator(new DecelerateInterpolator()).withLayer();




        }else{
            ViewCompat.animate(mActionBarToolbar).translationY(-mActionBarToolbar.getBottom()).alpha(0).setDuration(300).
                    setInterpolator(new DecelerateInterpolator()).withLayer();


//            final int height=mActionBarToolbar.getHeight();
//            ViewGroup.LayoutParams layoutParams=mActionBarToolbar.getLayoutParams();
//            ValueAnimator valueAnimator=ValueAnimator.ofInt(1,100);
//            valueAnimator.setInterpolator(new DecelerateInterpolator());
//            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//                private IntEvaluator intEvaluator=new IntEvaluator();
//                @Override
//                public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                    int currentValue=(Integer)valueAnimator.getAnimatedValue();
//                    float fraction=valueAnimator.getAnimatedFraction();
//                    mActionBarToolbar.getLayoutParams().height=intEvaluator.evaluate(fraction,height,0);
//                    mActionBarToolbar.requestLayout();
//                }
//            });
//            valueAnimator.setDuration(300).start();
        }
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






}
