package com.yangyongwen.zhihudailypaper.homePage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.dataStructure.Story;
import com.yangyongwen.zhihudailypaper.dataStructure.TopStory;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.storycontent.StoryContentActivity;
import com.yangyongwen.zhihudailypaper.ui.CircleIndicator;
import com.yangyongwen.zhihudailypaper.utils.DateUtils;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by samsung on 2016/2/26.
 */
public class HomePageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final static String TAG= LogUtils.makeLogTag(HomePageAdapter.class);
    public static final int TOPSTORY_ITEM=0;
    public static final int  DATE_ITEM=1;
    public static final int STORY_ITEM=2;
    public static final String STORY_ID="story_id";
    public static final String CURRENT_ID="current_id";

    private String mOldestDate;


    private Context mContext;

    private FragmentManager mFragmentManager;

    private TopStoryAdapter mTopStoryAdapter;

    private int mTopStoryViewPagerPos;

    private SwipeRefreshLayout mSwipeRefreshLayout;



    private ArrayList<Story> mStories;
    private ArrayList<TopStory> mTopStories;

    private ArrayList<String> mStoryIds;
    private ArrayList<String> mTopStoryIds;



    public HomePageAdapter(Context context,FragmentManager fragmentManager){
        if(fragmentManager==null){
            throw new IllegalStateException("please set fragmentManager to HomepageRecyclerView");
        }
        mContext=context;
        mFragmentManager=fragmentManager;
        mStories=new ArrayList<Story>();
        mTopStories=new ArrayList<TopStory>();
        mStoryIds=new ArrayList<String>();
        mTopStoryIds=new ArrayList<String>();
    }

    public void setTopStories(ArrayList<TopStory> topStories){
        mTopStories.clear();
        mTopStories.addAll(topStories);
        mTopStoryIds.clear();
        for(TopStory topStory:topStories){
            mTopStoryIds.add(Integer.toString(topStory.getId()));
        }
    }

    public void addDailyStories(ArrayList<Story> dailyStories){
        for(Story story1:dailyStories){
            mStoryIds.add(Integer.toString(story1.getId()));
        }
        String date=dailyStories.get(0).getDate();
        mOldestDate=date;
        Story story=new Story();
        story.setDate(date);
        story.setTitle("date: "+date);
        dailyStories.add(0, story);
        mStories.addAll(dailyStories);
    }

    public String getOldestDate(){
        return mOldestDate;
    }


    public void setSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout){
        mSwipeRefreshLayout=swipeRefreshLayout;
    }


    private  class TopStoryViewHolder extends RecyclerView.ViewHolder{
        ViewPager mViewPager;
        CircleIndicator mCircleIndicator;
        GestureDetector tapGestureDetector;
        public TopStoryViewHolder(View v){
            super(v);
            mViewPager=(ViewPager)v.findViewById(R.id.topstory_viewpager);
            mCircleIndicator=(CircleIndicator)v.findViewById(R.id.topstory_indicator);
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent=new Intent(mContext, StoryContentActivity.class);
//                    ArrayList<String> stringArrayList=getStoryIds();
//                    intent.putStringArrayListExtra(STORY_ID,stringArrayList);
//                    intent.putExtra(CURRENT_ID, getOnClickId());
//                    mContext.startActivity(intent);
//                }
//            });

            tapGestureDetector=new GestureDetector(mContext,new TapGestureListener());
            mViewPager.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    tapGestureDetector.onTouchEvent(event);
                    return false;
                }
            });


        }

        class TapGestureListener extends GestureDetector.SimpleOnGestureListener{

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Intent intent=new Intent(mContext, StoryContentActivity.class);
                intent.putStringArrayListExtra(STORY_ID, mTopStoryIds);
                intent.putExtra(CURRENT_ID, getOnClickId());
                Activity activity=(Activity)mContext;
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return false;
            }
        }

        private String getOnClickId(){
            final String id=Integer.toString(mTopStories.get(mTopStoryViewPagerPos).getId());
            return id;
        }
    }

    public  class StoryViewHolder extends RecyclerView.ViewHolder{
        TextView mStoryTitle;
        ImageView mStoryIcon;
        public StoryViewHolder(View v) {
            super(v);
            mStoryIcon = (ImageView) v.findViewById(R.id.story_item_icon);
            mStoryTitle = (TextView) v.findViewById(R.id.story_item_title);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, StoryContentActivity.class);
                    intent.putStringArrayListExtra(STORY_ID, mStoryIds);
                    intent.putExtra(CURRENT_ID, getOnClickId());
                    Activity activity=(Activity)mContext;
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
            });
        }

        private String getOnClickId(){
            int pos=this.getAdapterPosition()-1;
            final String id=Integer.toString(mStories.get(pos).getId());
            return id;
        }
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder{
        TextView mDateTitle;
        public DateViewHolder(View v){
            super(v);
            mDateTitle=(TextView)v.findViewById(R.id.story_date);
        }
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        RecyclerView.ViewHolder vh=null;
        View v=null;
        switch (viewType){
            case STORY_ITEM:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item_cardview,parent,false);
                vh=new StoryViewHolder(v);
                break;
            case TOPSTORY_ITEM:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.topstory_item,parent,false);
                vh=new TopStoryViewHolder(v);
                break;
            case DATE_ITEM:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.story_date_item,parent,false);
                vh=new DateViewHolder(v);
                break;
            default:
                break;
        }
        return vh;
    }


    private  ArrayList<String> getStoryIds(){
        ArrayList<String> stringArrayList=new ArrayList<String>();
        for(Story story:mStories){
            stringArrayList.add(Integer.toString(story.getId()));
        }
        stringArrayList.remove(0);
        return stringArrayList;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
        switch (getItemViewType(position)){
            case TOPSTORY_ITEM:
                bindTopStoryViewHolder(holder,position);
                break;
            case STORY_ITEM:
                bindStoryViewHolder(holder,position);
                break;
            case DATE_ITEM:
                bindDateViewHolder(holder,position);
                break;
            default:
                break;
        }
    }

    private void bindTopStoryViewHolder(RecyclerView.ViewHolder holder,int position){
        TopStoryViewHolder topStoryViewHolder=(TopStoryViewHolder)holder;

        if(mTopStoryAdapter==null){
            mTopStoryAdapter = new TopStoryAdapter(mFragmentManager);
            mTopStoryAdapter.setTopStories(mTopStories);
        }

        mTopStoryAdapter.setTopStories(mTopStories);
        topStoryViewHolder.mViewPager.setAdapter(mTopStoryAdapter);
        topStoryViewHolder.mViewPager.setCurrentItem(mTopStoryViewPagerPos);
        if(mTopStories.size()!=0){
            topStoryViewHolder.mCircleIndicator.setViewPager(topStoryViewHolder.mViewPager);
            topStoryViewHolder.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mTopStoryViewPagerPos=position;
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                        mSwipeRefreshLayout.setEnabled(false);
                    } else if (state == ViewPager.SCROLL_STATE_IDLE) {
                        mSwipeRefreshLayout.setEnabled(true);
                    }
                }
            });
            topStoryViewHolder.mCircleIndicator.setVisibility(View.VISIBLE);
            topStoryViewHolder.mCircleIndicator.showIndicator();
        }



    }

    private void bindStoryViewHolder(RecyclerView.ViewHolder holder,int position){
        StoryViewHolder vh=(StoryViewHolder)holder;

        vh.mStoryTitle.setText(mStories.get(position - 1).getTitle());
        Picasso.with(mContext).load(mStories.get(position - 1).getImages()[0]).into(vh.mStoryIcon);
    }

    private void bindDateViewHolder(RecyclerView.ViewHolder holder,int position){


        DateViewHolder vh=(DateViewHolder)holder;
        String date=mStories.get(position-1).getTitle().split(":")[1].substring(1);
        String dateTitle;
        String today=mTopStories.get(0).getDate();
        if(date.equals(today)){
            dateTitle=new String("今日热闻");
        }else{
            String s1=date.substring(4,6);
            String s2=date.substring(6,8);
            dateTitle=s1+"月"+s2+"日 "+DateUtils.convertDay(date);
        }
        vh.mDateTitle.setText(dateTitle);

    }








    @Override
    public void onViewRecycled(RecyclerView.ViewHolder vh){
        if(vh instanceof TopStoryViewHolder){
            mTopStoryViewPagerPos=((TopStoryViewHolder) vh).mViewPager.getCurrentItem();

        }
        super.onViewRecycled(vh);
    }

    @Override
    public int getItemCount(){
        return mStories.size()+1;
    }


    @Override
    public int getItemViewType(int position){
        if(position==0) {
            return TOPSTORY_ITEM;
        }else if(isStoryDateTitle(position)){
            return DATE_ITEM;
        }else{
            return STORY_ITEM;
        }
    }

    public void clearAll(){
        mTopStories.clear();
        mStories.clear();
        mStoryIds.clear();
    }


    private boolean isStoryDateTitle(int position){
        return mStories.get(position-1).getTitle().contains("date")?true:false;
    }


    public String getToday(){
        return mTopStories.get(0).getDate();
    }


    public String getDateByPosition(int pos){
        String s=null;
        if(pos==0){
            s=mTopStories.get(0).getDate();
        }else{
            s=mStories.get(pos-1).getDate();
        }
        return s;
    }



}
