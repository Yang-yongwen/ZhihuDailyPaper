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
import com.yangyongwen.zhihudailypaper.comment.CommentActivity;
import com.yangyongwen.zhihudailypaper.common.Model;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.UpdatableView;
import com.yangyongwen.zhihudailypaper.common.UserActionEnum;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryDetail;
import com.yangyongwen.zhihudailypaper.homePage.HomePageAdapter;
import com.yangyongwen.zhihudailypaper.homePage.HomePagePresenter;
import com.yangyongwen.zhihudailypaper.login.LoginActivity;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;
import com.yangyongwen.zhihudailypaper.utils.Message;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by samsung on 2016/3/15.
 */
public class StoryContentActivity extends AppCompatActivity {

    private static final String TAG= LogUtils.makeLogTag(StoryContentActivity.class);


    private static final String PRESENTER_TAG="fragment_tag";

    private Toolbar mActionBarToolbar;

    StoryContentModel storyContentModel;


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

        String id=intent.getStringExtra(HomePageAdapter.CURRENT_ID);

        storyContentModel=new StoryContentModel(getApplicationContext(),ids,id);

        addPresenterFragment(R.id.storycontent_frag,storyContentModel,null,null);

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

        for(int i=0;i<menu.size();++i){
            final MenuItem item=menu.getItem(i);
            if(item.getItemId()==R.id.action_comment||item.getItemId()==R.id.action_praise){
                item.getActionView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onOptionsItemSelected(item);
                    }
                });
            }
        }

        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();
        switch (id){
            case R.id.action_share:{

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
                EventBus.getDefault().post(new Message.StartCommActMsg("start comment activity"));
                break;
            }
            case R.id.action_praise:{

                break;
            }
            default:
        }

        return false;
    }




//    private void onActionBarShowOrHide(boolean shown){
//        if(shown){
//            ViewCompat.animate(mActionBarToolbar).translationY(0).alpha(1).setDuration(300).
//                    setInterpolator(new DecelerateInterpolator()).withLayer();
//
//
//
//
//        }else{
//            ViewCompat.animate(mActionBarToolbar).translationY(-mActionBarToolbar.getBottom()).alpha(0).setDuration(300).
//                    setInterpolator(new DecelerateInterpolator()).withLayer();
//
//
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
//        }
//    }




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
