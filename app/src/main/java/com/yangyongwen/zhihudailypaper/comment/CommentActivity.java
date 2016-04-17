package com.yangyongwen.zhihudailypaper.comment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.dataStructure.Comment;
import com.yangyongwen.zhihudailypaper.dataStructure.ThemeContent;
import com.yangyongwen.zhihudailypaper.login.LoginActivity;
import com.yangyongwen.zhihudailypaper.themeList.ThemeDataProvider;
import com.yangyongwen.zhihudailypaper.themeList.ThemeListAdapter;
import com.yangyongwen.zhihudailypaper.ui.CircleIndicator;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;
import com.yangyongwen.zhihudailypaper.utils.Message;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class CommentActivity extends AppCompatActivity {

    private static final String TAG= LogUtils.makeLogTag(CommentActivity.class);
    private static final String SHORT_COMMENT_NUM="short_comment_num";
    private static final String LONG_COMMENT_NUM="long_comment_num";
    private static final String STORY_ID="story_id";

    private static final int LONG_COMMENT=0;
    private static final int SHORT_COMMENT=1;

    private Toolbar mActionBarToolbar;
    private RecyclerView mCommentListView;
    private LinearLayoutManager mLinearLayoutManager;
    private CommentListAdapter mCommentListAdapter;

    private String mId;

    private AlertDialog mAlertDialog;


    final CommentDataProvider.DataRequestCallback shortCallback=new CommentDataProvider.DataRequestCallback() {
        @Override
        public void onSuccess(Object object,Bundle bundle){
            final Comment[] comments=((CommentDataProvider.Comments)object).comments;
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mCommentListAdapter.addShortComments(comments);
                }
            });
        }
        @Override
        public void onFailure(Bundle bundle){
            LogUtils.LOGD(TAG, "request comment list from server failed");
        }
    };

    final CommentDataProvider.DataRequestCallback longCallback=new CommentDataProvider.DataRequestCallback() {
        @Override
        public void onSuccess(Object object,Bundle bundle){
            final Comment[] comments=((CommentDataProvider.Comments)object).comments;
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mCommentListAdapter.addLongComments(comments);
                    mAlertDialog.dismiss();
                }
            });        }
        @Override
        public void onFailure(Bundle bundle){
            LogUtils.LOGD(TAG, "request comment list from server failed");
            mAlertDialog.dismiss();
            Toast.makeText(getApplicationContext(),"加载失败",Toast.LENGTH_SHORT).show();
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        mActionBarToolbar=getActionBarToolbar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBarToolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mId=getIntent().getStringExtra(STORY_ID);
        int shortNum=getIntent().getIntExtra(SHORT_COMMENT_NUM, 0);
        int longNum=getIntent().getIntExtra(LONG_COMMENT_NUM, 0);
        int num=longNum+shortNum;
        getSupportActionBar().setTitle(num + "条点评");

//        ExpandableTextView textView=(ExpandableTextView)findViewById(R.id.expand_text_view);
//        textView.setText("Using the library is really simple, just look at the source code of the provided sample. (Look at the SampleTextListAdapter.java for the use within a ListView");

//        final TextView textView=(TextView)findViewById(R.id.comment_text);
//        textView.setText("Using the library is really simple, just look at the source code of the provided sample. (Look at the SampleTextListAdapter.java for the use within a ListView");


        mCommentListAdapter=new CommentListAdapter(this,shortNum,longNum);
        mLinearLayoutManager=new LinearLayoutManager(this);
        mCommentListView=(RecyclerView)findViewById(R.id.comment_list);
        mCommentListView.setAdapter(mCommentListAdapter);
        mCommentListView.setLayoutManager(mLinearLayoutManager);
        mCommentListView.addItemDecoration(new SimpleDividerItemDecoration(this));

        CommentDataProvider.getInstance(this).getCommentList(LONG_COMMENT, mId, longCallback);

        LayoutInflater layoutInflater
                = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View loadingView = layoutInflater.inflate(R.layout.loading_comment, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(loadingView);
        mAlertDialog=builder.create();
        mAlertDialog.setCanceledOnTouchOutside(false);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mAlertDialog.show();
//            }
//        },100);
        mAlertDialog.show();

        EventBus.getDefault().register(this);
    }






    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe
    public void onLoadShortComment(Message.LoadShortComment event){
        CommentDataProvider.getInstance(this).getCommentList(SHORT_COMMENT,mId,shortCallback);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_comment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_write_comment){
            Intent intent=new Intent(this, LoginActivity.class);
            startActivity(intent);
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

    private class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        }
        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }


}
