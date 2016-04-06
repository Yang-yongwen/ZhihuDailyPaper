package com.yangyongwen.zhihudailypaper.homePage;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yangyongwen.zhihudailypaper.dataStructure.Story;
import com.yangyongwen.zhihudailypaper.provider.ZhihuContentProvider;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.utils.DateUtils;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.UpdatableView;

import java.util.ArrayList;

/**
 * Created by yangyongwen on 16/2/16.
 */
public class HomePageFragment extends Fragment implements UpdatableView<HomePageModel>{

    private final static String TAG= LogUtils.makeLogTag(HomePageFragment.class);
    public static final int TOPSTORY_ITEM=0;
    public static final int  DATE_ITEM=1;
    public static final int STORY_ITEM=2;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mHomePageRecyclerView;
    private HomePageAdapter mHomePageAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private UserActionListener mUserActionListener;

    private Boolean isAddingDailyStories;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                   Bundle savedInstanceState){

        View view= inflater.inflate(R.layout.homepage_frag, container, false);

        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mUserActionListener.onUserAction(HomePageModel.HomePageActionEnum.LOADLATEST,null);
            }
        });


        isAddingDailyStories=false;


        mHomePageAdapter=new HomePageAdapter(getActivity(),getFragmentManager());
        mHomePageAdapter.setSwipeRefreshLayout(mSwipeRefreshLayout);


        mHomePageRecyclerView=(RecyclerView)view.findViewById(R.id.homepage_content);
        mHomePageRecyclerView.setAdapter(mHomePageAdapter);
        mLinearLayoutManager=new LinearLayoutManager(getActivity());
        mHomePageRecyclerView.setLayoutManager(mLinearLayoutManager);

        mHomePageRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LogUtils.LOGD(TAG,"onStateChanged");
                super.onScrollStateChanged(recyclerView, newState);
            }
            @Override
            public void onScrolled(RecyclerView recyclerView,int dx,int dy){
                super.onScrolled(recyclerView,dx,dy);
                LogUtils.LOGD(TAG,"onScroll: dx="+dx+" dy="+dy );
                if((mLinearLayoutManager.findLastVisibleItemPosition()==mHomePageAdapter.getItemCount()-1)&&!isAddingDailyStories){
                    isAddingDailyStories=true;
                    LogUtils.LOGD(TAG, "need to add daily stories");

                    String date=DateUtils.lastDay(mHomePageAdapter.getOldestDate());
                    Bundle bundle=new Bundle();
                    bundle.putString(HomePageModel.DATE,date);
                    mUserActionListener.onUserAction(HomePageModel.HomePageActionEnum.LOADDAILY, bundle);

                }

                int pos=mLinearLayoutManager.findFirstVisibleItemPosition();



                if(pos==0){
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("首页");
                }else {
                    String today=mHomePageAdapter.getToday();
                    String date1=mHomePageAdapter.getDateByPosition(pos);

                    String title=null;

                    if(date1.equals(today)){
                        title=new String("今日热闻");
                    }else{
                        String s1=date1.substring(4,6);
                        String s2=date1.substring(6,8);
                        title=s1+"月"+s2+"日 "+DateUtils.convertDay(date1);
                    }

                    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(title);



                }

            }
        });


//        mHomePageRecyclerView.setFragmentManager(getFragmentManager());
//        mHomePageRecyclerView.setSwipeRefreshLayout(mSwipeRefreshLayout);
        return view;
    }








    @Override
    public Uri getDataUri(QueryEnum queryEnum){
        if(queryEnum== HomePageModel.HomePageQueryEnum.TOPSTORY){
            return ZhihuContentProvider.STORY_TOP_STORY_CONTENT_URI;
        }else if(queryEnum== HomePageModel.HomePageQueryEnum.DAILYSTORY){
            return ZhihuContentProvider.STORY_CONTENT_URI;
        }else {
            return Uri.EMPTY;
        }
    }


    @Override
    public void displayData(HomePageModel model,QueryEnum query,Bundle bundle){
        if(query== HomePageModel.HomePageQueryEnum.TOPSTORY){
            mSwipeRefreshLayout.setRefreshing(false);
            mHomePageAdapter.clearAll();
            mHomePageAdapter.setTopStories(model.getTopStories());
            mHomePageAdapter.notifyDataSetChanged();
//            mHomePageRecyclerView.initHomePageView();
//            mHomePageRecyclerView.setTopStories(model.getTopStories());
        }else if(query== HomePageModel.HomePageQueryEnum.DAILYSTORY){
            isAddingDailyStories=false;
            String date=bundle.getString(HomePageModel.DATE);
            ArrayList<Story> dailyStories=model.getDailyStories(date);
            mHomePageAdapter.addDailyStories(dailyStories);
            mHomePageAdapter.notifyDataSetChanged();
        }

    }
    @Override
    public void displayErrorMessage(QueryEnum query){
        if(query== HomePageModel.HomePageQueryEnum.DAILYSTORY){
            isAddingDailyStories=false;
        }
        mSwipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(),"更新model失败，请重试",Toast.LENGTH_SHORT).show();
        LogUtils.LOGD(TAG,query.toString()+" failed");
    }
    @Override
    public void addListener(UserActionListener listener){
        mUserActionListener=listener;
    }

}
