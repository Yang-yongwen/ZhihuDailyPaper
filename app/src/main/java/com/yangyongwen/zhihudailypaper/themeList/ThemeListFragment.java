package com.yangyongwen.zhihudailypaper.themeList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.dataStructure.ThemeContent;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;


/**
 * Created by samsung on 2016/3/31.
 */
public class ThemeListFragment extends Fragment {

    private static final String TAG= LogUtils.makeLogTag(ThemeListFragment.class);

    private static final String THEME_ID="theme_id";

    private LinearLayoutManager mLinearLayoutManager;
    private ThemeListAdapter mThemeListAdapter;
    private RecyclerView mThemeRecyclerView;

    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View view= inflater.inflate(R.layout.fragment_theme_list, container, false);

        Bundle bundle=getArguments();
        final int id=bundle.getInt(THEME_ID);

        mThemeListAdapter=new ThemeListAdapter(getActivity());
        mLinearLayoutManager=new LinearLayoutManager(getActivity());
        mThemeRecyclerView=(RecyclerView)view.findViewById(R.id.theme_list_content);
        mThemeRecyclerView.setAdapter(mThemeListAdapter);
        mThemeRecyclerView.setLayoutManager(mLinearLayoutManager);

        mSwipeRefreshLayout=(SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ThemeDataProvider.getInstance(getActivity().getApplicationContext()).getThemeContent(id, callback);
            }
        });


        ThemeDataProvider.getInstance(getActivity().getApplicationContext()).getThemeContent(id, callback);


        return view;
    }

    final ThemeDataProvider.DataRequestCallback callback=new ThemeDataProvider.DataRequestCallback() {
        @Override
        public void onSuccess(Object object,Bundle bundle){
            final ThemeContent themeContent=(ThemeContent)object;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mThemeListAdapter.setThemeContent(themeContent);
                    mThemeListAdapter.notifyDataSetChanged();
                }
            });
            //set theme content to adapter

        }
        @Override
        public void onFailure(Bundle bundle){
            LogUtils.LOGD(TAG,"request theme content from server failed");
            Toast.makeText(getActivity(),"网络加载主题内容失败",Toast.LENGTH_SHORT).show();
        }
    };




}
