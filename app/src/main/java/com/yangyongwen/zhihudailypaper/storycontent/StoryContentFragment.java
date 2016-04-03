package com.yangyongwen.zhihudailypaper.storycontent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.UpdatableView;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryDetail;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryExtraInfo;
import com.yangyongwen.zhihudailypaper.network.NetworkRequest;
import com.yangyongwen.zhihudailypaper.network.NetworkRequestProxy;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by samsung on 2016/3/15.
 */
public class StoryContentFragment extends Fragment implements UpdatableView<StoryContentModel>{

    private final static String TAG= LogUtils.makeLogTag(StoryContentFragment.class);
    private UserActionListener mUserActionListener;
    private ViewPager mViewPager;

    private String STORY_DETAIL_STATE="story_detail_state";

    private StoryContentAdapter mStoryContentAdapter;

    Toolbar mActionBar;

    TextView mPraiseNum;
    TextView mCommentNum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        View view=inflater.inflate(R.layout.storycontent_frag,container,false);

        mViewPager=(ViewPager)view.findViewById(R.id.storycontent_viewpager);

        mStoryContentAdapter=new StoryContentAdapter(getFragmentManager());
        mViewPager.setAdapter(mStoryContentAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                LogUtils.LOGD(TAG, "on page selected: page " + position);
                String storyId = mStoryContentAdapter.getStoryIdList().get(position);
//                if (mStoryContentAdapter.containStoryDetail(storyId)) {
//                    return;
//                }
                Bundle bundle = new Bundle();
                bundle.putString(StoryContentModel.STORY_ID, storyId);

                if (mStoryContentAdapter.containStoryDetail(storyId)) {
                    bundle.putString(STORY_DETAIL_STATE, "exist");
                }


                mUserActionListener.onUserAction(StoryContentModel.StoryContentActionEnum.PAGECHANGE, bundle);

                if (mCommentNum != null && mPraiseNum != null) {
                    mCommentNum.setText("...");
                    mPraiseNum.setText("...");
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        getActivity().getWindow().getDecorView().getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                if (mActionBar == null || mPraiseNum == null || mPraiseNum == null) {
                    mActionBar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
                    mPraiseNum = (TextView) mActionBar.findViewById(R.id.praise_num);
                    mCommentNum = (TextView) mActionBar.findViewById(R.id.comment_num);
                }
            }
        });

        getActivity().getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (mActionBar == null || mPraiseNum == null || mPraiseNum == null) {
                    mActionBar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
                    mPraiseNum = (TextView) mActionBar.findViewById(R.id.praise_num);
                    mCommentNum = (TextView) mActionBar.findViewById(R.id.comment_num);
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mActionBar == null || mPraiseNum == null || mPraiseNum == null) {
                    mActionBar = (Toolbar) getActivity().findViewById(R.id.toolbar_actionbar);
                    mPraiseNum = (TextView) mActionBar.findViewById(R.id.praise_num);
                    mCommentNum = (TextView) mActionBar.findViewById(R.id.comment_num);
                    mCommentNum.setText("...");
                    mPraiseNum.setText("...");
                }
            }
        }, 500);





        return view;
    }






    @Override
    public Uri getDataUri(QueryEnum queryEnum){
        return null;
    }

    @Override
    public void displayData(StoryContentModel model,QueryEnum query,Bundle bundle){
        if(query== StoryContentModel.StoryContentQueryEnum.STORYDETAIL){
            String storyId=bundle.getString(StoryContentModel.STORY_ID);
            StoryDetail storyDetail=model.getStoryDetail(storyId);
            mStoryContentAdapter.setStoryDetail(storyId, storyDetail);

            LogUtils.LOGD(TAG, "add storyDetail, story id: " + storyId);
            LogUtils.LOGD(TAG, "story detail: " + model.getStoryDetail(storyId).getTitle());


            View rootView=mViewPager.findViewWithTag(storyId);

            if(rootView!=null){
                String image = storyDetail.getImage();
                String title = storyDetail.getTitle();
                String body = storyDetail.getBody();
                String image_resource= storyDetail.getImage_source();



                if(image!=null){
                    TextView textView = (TextView) rootView.findViewById(R.id.story_title);
                    textView.setText(title);

                    TextView textView1=(TextView) rootView.findViewById(R.id.story_icon_resource);
                    textView1.setText(image_resource);

                    ImageView imageView = (ImageView) rootView.findViewById(R.id.story_icon);
                    Picasso.with(getActivity().getApplicationContext()).load(image).into(imageView);
                }





                WebView webView = (WebView) rootView.findViewById(R.id.story_content_webview);
                WebSettings webSettings = webView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                        if (Uri.parse(url).getHost().equals("www.example.com")) {
//                            // This is my web site, so do not override; let my WebView load the page
//                            return false;
//                        }
                        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        return true;
                    }
                });
                StoryContentAdapter.StoryContentVPFragment.setWebView(webView, body);
            }

//            mStoryContentAdapter.notifyDataSetChanged();
        }else if(query== StoryContentModel.StoryContentQueryEnum.STORYEXTRAINFO){
            final String storyId=bundle.getString(StoryContentModel.STORY_ID);
            final StoryExtraInfo storyExtraInfo=model.getStoryExtraInfo(storyId);

            if(mPraiseNum==null||mCommentNum==null){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPraiseNum.setText(storyExtraInfo.getPopularity());
                        mCommentNum.setText(storyExtraInfo.getComments());
                    }
                }, 500);
            }else {
                mPraiseNum.setText(convert(storyExtraInfo.getPopularity()));
                mCommentNum.setText(convert(storyExtraInfo.getComments()));
            }

        }else if(query== StoryContentModel.StoryContentQueryEnum.STORYIDLIST){
            mStoryContentAdapter.setStoryIdList(model.getStoryIdList());
            int index=0;
            String storyId=bundle.getString(StoryContentModel.STORY_ID);
            ArrayList<String> ids=model.getStoryIdList();
            for(String id:model.getStoryIdList()){
                if(id.equals(storyId)){
                    break;
                }
                index++;
            }
            if(index!=0){
                mViewPager.setCurrentItem(index,false);
            }


            /*
            * 当点击第一项和最后一项进入viewpager时,切换viewpager会发生闪烁...原因不明.
            * 目前解决办法是加载第一项和最后一项viewpager时预加载下一项...权宜之计
            * */

            if(index==0){
                Bundle bundle1 = new Bundle();
                bundle1.putString(StoryContentModel.STORY_ID, ids.get(1));
                mUserActionListener.onUserAction(StoryContentModel.StoryContentActionEnum.PAGECHANGE, bundle1);
            }
            if(index==ids.size()-1){
                Bundle bundle2 = new Bundle();
                bundle2.putString(StoryContentModel.STORY_ID, ids.get(ids.size()-2));
                mUserActionListener.onUserAction(StoryContentModel.StoryContentActionEnum.PAGECHANGE,bundle2);
            }

//            mStoryContentAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void displayErrorMessage(QueryEnum query){

    }

    @Override
    public void addListener(UserActionListener listener){
        mUserActionListener=listener;
    }


    private String convert(int i){

        String result="";
        if(i<1000){
            result=Integer.toString(i);
        }else if(i<10000){
            float f=((float)i)/1000;
            result=Float.toString(f).substring(0,3)+"k";
        }else if(i<100000){
            float f=((float)i)/1000;
            result=Float.toString(f).substring(0,2)+"k";
        }else{
            float f=((float)i)/1000;
            result=Float.toString(f).substring(0,3)+"k";
        }
        return result;
    }


}
