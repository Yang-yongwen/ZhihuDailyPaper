package com.yangyongwen.zhihudailypaper.storycontent;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.dataStructure.MapList;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryDetail;
import com.yangyongwen.zhihudailypaper.dataStructure.StoryExtraInfo;
import com.yangyongwen.zhihudailypaper.photoviewer.PhotoViewActivity;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by samsung on 2016/3/15.
 */
public class StoryContentAdapter extends FragmentStatePagerAdapter {

    private static final String TAG= LogUtils.makeLogTag(StoryContentAdapter.class);

    public final static String TITLE="story_content_title";
    public final static String IMAGE="story_content_image";
    public final static String BODY="story_content_body";
    public final static String IMAGE_RESOURCE="image_res";
    public final static String FRAGMENT_TAG="fragment_tag";
    private static final String PHOTO_URL="photo_url";

    private HashMap<String,StoryDetail> mStoryDetailList;
    private Context mContext=null;

    private ArrayList<String> mStroyIdList;

    private String mCurrentId;


    public void setStoryIdList(ArrayList<String > ids){
        mStroyIdList=ids;
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getStoryIdList(){
        return mStroyIdList;
    }

    public void setCurrentId(String id){
        mCurrentId=id;
    }

    public int getCurrentItem(){
        int index=0;
        for(String id:mStroyIdList){
            if(id==mCurrentId){
                return index;
            }
            index++;
        }
        return -1;
    }





    public StoryContentAdapter(FragmentManager fm) {
        super(fm);
        mStoryDetailList=new HashMap<String,StoryDetail>();
        mStroyIdList=new ArrayList<>();
    }

    public boolean containStoryDetail(String id){
        return mStoryDetailList.get(id)!=null;
    }




    @Override
    public Fragment getItem(int i) {
        Fragment fragment=new StoryContentVPFragment();

//        if(mStoryDetailList.getValue(i)!=null){
//            StoryDetail storyDetail=mStoryDetailList.getValue(i);
//            Bundle bundle=new Bundle();
//            bundle.putString(BODY,storyDetail.getBody());
//            bundle.putString(TITLE,storyDetail.getTitle());
//            bundle.putString(IMAGE,storyDetail.getImage());
//            bundle.putString(IMAGE_RESOURCE,storyDetail.getImage_source());
//            fragment.setArguments(bundle);
//        }

        String storyId=mStroyIdList.get(i);

        StoryDetail storyDetail=mStoryDetailList.get(storyId);

        Bundle bundle=new Bundle();
        bundle.putString(FRAGMENT_TAG,storyId);

        if(storyDetail!=null){
            bundle.putString(BODY,storyDetail.getBody());
            bundle.putString(TITLE,storyDetail.getTitle());
            bundle.putString(IMAGE,storyDetail.getImage());
            bundle.putString(IMAGE_RESOURCE, storyDetail.getImage_source());
            LogUtils.LOGD(TAG,"story detail: "+storyDetail.getTitle());
        }

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getCount() {
        return mStroyIdList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "OBJECT " + (position + 1);
    }


    public static class StoryContentVPFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater,
                                 ViewGroup container, Bundle savedInstanceState) {
            // The last two arguments ensure LayoutParams are inflated
            // properly.
            View rootView = inflater.inflate(
                    R.layout.storycontent_viewpager_frag, container, false);
            Bundle args = getArguments();

            String tag=args.getString(FRAGMENT_TAG);

            String image = args.getString(IMAGE);
            String title = args.getString(TITLE);
            String body = args.getString(BODY);
            String image_resource=args.getString(IMAGE_RESOURCE);

            if(body!=null) {

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
                webView.setWebViewClient(new WebViewClient() {
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

                webView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        WebView.HitTestResult hr = ((WebView) view).getHitTestResult();

                        if (hr.getType() == WebView.HitTestResult.IMAGE_TYPE && motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            Intent intent=new Intent(getActivity(), PhotoViewActivity.class);
                            intent.putExtra(PHOTO_URL,hr.getExtra());
                            startActivity(intent);
                        }
                        LogUtils.LOGD(TAG, "getExtra = " + hr.getExtra() + "\t\t type = " + hr.getType());
                        return false;
                    }
                });

                setWebView(webView, body);


            }

            rootView.setTag(tag);
            LogUtils.LOGD(TAG,"fragment set tag: "+tag);

            return rootView;
        }

//        public static void setViewContent(View rootView,String title,String image,String image_res,String body){
//            String image = args.getString(IMAGE);
//            String title = args.getString(TITLE);
//            String body = args.getString(BODY);
//            String image_resource=args.getString(IMAGE_RESOURCE);
//
//            TextView textView = (TextView) rootView.findViewById(R.id.story_title);
//            textView.setText(title);
//
//            TextView textView1=(TextView) rootView.findViewById(R.id.story_icon_resource);
//            textView1.setText(image_resource);
//
//            ImageView imageView = (ImageView) rootView.findViewById(R.id.story_icon);
//            Picasso.with(getActivity().getApplicationContext()).load(image).into(imageView);
//
//            WebView webView = (WebView) rootView.findViewById(R.id.story_content_webview);
//            WebSettings webSettings = webView.getSettings();
//            webSettings.setJavaScriptEnabled(true);
//            webView.setWebViewClient(new WebViewClient());
//            setWebView(webView, body);
//        }


        public static void setWebView(WebView webView,String body){
            StringBuilder sb=new StringBuilder();
            sb.append("<HTML><HEAD><LINK href=\"style.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
            sb.append(body);
            sb.append("</body></HTML>");
            webView.loadDataWithBaseURL("file:///android_asset/", sb.toString(), "text/html", "utf-8", null);
        }



    }

    public static class StoryContent{
        public StoryDetail mStoryDetail;
        public StoryExtraInfo mStoryExtraInfo;
    }


    public void setStoryDetail(String storyId, StoryDetail storyDetail){
        mStoryDetailList.put(storyId,storyDetail);
        notifyDataSetChanged();
    }

    public void setStoryExtraInfo(String storyId){

    }



}
