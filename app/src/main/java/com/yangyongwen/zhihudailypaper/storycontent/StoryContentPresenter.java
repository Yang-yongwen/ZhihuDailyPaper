package com.yangyongwen.zhihudailypaper.storycontent;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.yangyongwen.zhihudailypaper.common.Model;
import com.yangyongwen.zhihudailypaper.common.Presenter;
import com.yangyongwen.zhihudailypaper.common.QueryEnum;
import com.yangyongwen.zhihudailypaper.common.UpdatableView;
import com.yangyongwen.zhihudailypaper.common.UserActionEnum;
import com.yangyongwen.zhihudailypaper.homePage.HomePageModel;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

/**
 * Created by yangyongwen on 16/3/18.
 */
public class StoryContentPresenter extends Fragment implements Presenter, UpdatableView.UserActionListener,HomePageModel.ModelUpdatedListener {


    private final static String TAG= LogUtils.makeLogTag(StoryContentPresenter.class);

    private final static int MODELUPDATESUCCESS=1;
    private final static int MODELUPDATEERROR=2;
    private Model mModel;
    private UpdatableView mUpdatableView;
    private Context mContext;
    Handler mHandler;


    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        mHandler=new MainThreadHandler(activity.getMainLooper());
        final Bundle bundle=new Bundle();
        bundle.putString(StoryContentModel.STORY_ID,((StoryContentModel)mModel).getStoryCurrentId());
//        this.onUserAction(StoryContentModel.StoryContentActionEnum.INIT, bundle);

        activity.getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                onUserAction(StoryContentModel.StoryContentActionEnum.INIT, bundle);
            }
        });
    }


    @Override
    public void setModel(Model model){
        mModel=model;
        mModel.setModelUpdateListener(this);
    }

    @Override
    public void setUpdatableView(UpdatableView view){
        mUpdatableView=view;
        mUpdatableView.addListener(this);
    }

    @Override
    public Context getContext(){
        return mContext;
    }

    @Override
    public void onUserAction(final UserActionEnum action,Bundle args){
//        mModel.requestModelUpdate(action, args, new Runnable() {
//            @Override
//            public void run() {
//                mUpdatableView.displayData(action,null);
//            }
//        });

        mModel.requestModelUpdate(action,args,null);

    }

    @Override
    public void onModelUpdateSuccess(QueryEnum queryEnum,Bundle bundle){
        LogUtils.LOGD(TAG,"storycontent model update success,query ID: "+queryEnum.getId());
        Message message=mHandler.obtainMessage(MODELUPDATESUCCESS);
        message.setData(bundle);
        message.obj=queryEnum;
        message.sendToTarget();
    }

    @Override
    public void onModelUpdateError(QueryEnum queryEnum){
        Message message=mHandler.obtainMessage(MODELUPDATEERROR);
        message.obj=queryEnum;
        message.sendToTarget();
    }


    private class MainThreadHandler extends Handler{

        public MainThreadHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message message){
            QueryEnum queryEnum=(QueryEnum)message.obj;
            Bundle bundle=message.getData();
            switch (message.what){
                case MODELUPDATESUCCESS:
                    mUpdatableView.displayData(mModel,queryEnum,bundle);
                    break;
                case MODELUPDATEERROR:
                    mUpdatableView.displayErrorMessage(queryEnum);
                    break;
                default:
                    break;
            }
        }

    }
}
