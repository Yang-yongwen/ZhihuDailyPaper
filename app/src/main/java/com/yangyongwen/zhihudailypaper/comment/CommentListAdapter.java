package com.yangyongwen.zhihudailypaper.comment;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.dataStructure.Comment;
import com.yangyongwen.zhihudailypaper.dataStructure.ThemeContent;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;
import com.yangyongwen.zhihudailypaper.utils.Message;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by samsung on 2016/4/14.
 */
public class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG= LogUtils.makeLogTag(CommentListAdapter.class);

    private static final int COMMENT_CONTENT_ITEM=0;
    private static final int COMMENT_NUM_ITEM=1;
    private static final int COMMENT_EMPTY_ITEM=2;


    private boolean longCommentEmpty;

    private ArrayList<Comment> mShortComments;
    private ArrayList<Comment> mLongComments;
    private int mLongCommentNum;
    private int mShortCommentNum;

    private Context mContext;

    private int mItemCount;


    public CommentListAdapter(Context context,int shortNum,int longNum){
        mContext=context;
        mLongCommentNum=longNum;
        mShortCommentNum=shortNum;
        mLongComments=new ArrayList<Comment>();
        mShortComments=new ArrayList<Comment>();
        mItemCount=3;
    }

    public void addShortComments(Comment[] comments){
        List<Comment> comments1= Arrays.asList(comments);
        mShortComments.addAll(comments1);
        if(mLongComments.size()>0){
            mItemCount=mLongComments.size()+mShortComments.size()+2;
        }else{
            mItemCount=mShortComments.size()+3;
        }
        notifyDataSetChanged();
    }

    public void addLongComments(Comment[] comments){
        List<Comment> comments1= Arrays.asList(comments);
        mLongComments.addAll(comments1);
        if(mLongComments.size()>0){
            mItemCount=mLongComments.size()+mShortComments.size()+2;
        }else{
            mItemCount=mShortComments.size()+3;
        }
        notifyDataSetChanged();
    }

    private  class CommentNumViewHolder extends RecyclerView.ViewHolder{
        TextView mCommentNum;
        ImageView mCommentFold;
        public CommentNumViewHolder(View v){
            super(v);
            mCommentNum=(TextView)v.findViewById(R.id.comment_num);
            mCommentFold=(ImageView)v.findViewById(R.id.comment_fold);
        }
    }


    private class CommentItemViewHolder extends RecyclerView.ViewHolder{
        TextView mVoteNum;
        RoundedImageView mAvatar;
        TextView mCommentText;
        TextView mCommentTime;
        TextView mCommenterName;
        View view;
        public CommentItemViewHolder(View v){
            super(v);
            mAvatar=(RoundedImageView)v.findViewById(R.id.commenter_avatar);
            mVoteNum=(TextView)v.findViewById(R.id.vote_num);
            mCommentText=(TextView)v.findViewById(R.id.comment_text);
            mCommentTime=(TextView)v.findViewById(R.id.comment_time);
            mCommenterName=(TextView)v.findViewById(R.id.commenter_name);
            view=v;
        }
    }

    private class CommentEmptyViewHolder extends RecyclerView.ViewHolder{
        TextView mTextView;
        ImageView mImageView;
        CommentEmptyViewHolder(View v){
            super(v);

            mImageView=(ImageView)v.findViewById(R.id.empty_icon);
            mTextView=(TextView)v.findViewById(R.id.empty_text);
        }
    }





    public void init(int shortComment,int longComment){
        mShortCommentNum=shortComment;
        mLongCommentNum=longComment;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        RecyclerView.ViewHolder vh=null;
        View v=null;
        switch (viewType){
            case COMMENT_CONTENT_ITEM:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_cotent_item,parent,false);
                vh=new CommentItemViewHolder(v);
                break;
            case COMMENT_EMPTY_ITEM:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_empty_item,parent,false);
                vh=new CommentEmptyViewHolder(v);
                break;
            case COMMENT_NUM_ITEM:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_num_item,parent,false);
                vh=new CommentNumViewHolder(v);
                break;
            default:
                break;
        }
        return vh;
    }


    @Override
    public int getItemCount(){
//        if(longCommentEmpty){
//            return 3+mShortComments.size();
//        }else{
//            return mLongComments.size()+mShortComments.size()+2;
//        }
        return mItemCount;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){
        switch (getItemViewType(position)){
            case COMMENT_CONTENT_ITEM:
                bindCommentContent(holder, position);
                break;
            case COMMENT_EMPTY_ITEM:
                bindCommentEmpty(holder, position);
                break;
            case COMMENT_NUM_ITEM:
                bindCommentNum(holder, position);
                break;
            default:
                break;
        }
    }

    private void bindCommentContent(RecyclerView.ViewHolder vh,int pos){
        Comment comment=null;
        CommentItemViewHolder viewHolder=(CommentItemViewHolder)vh;
        if(mLongComments.size()>0){
            if(pos<=mLongComments.size()){
                pos--;
                comment=mLongComments.get(pos);
            }else{
                pos=pos-2-mLongComments.size();
                comment=mShortComments.get(pos);
            }
        }else{
            pos-=3;
            comment=mShortComments.get(pos);
        }
        viewHolder.mCommenterName.setText(comment.author);
//        viewHolder.mCommentTime.setText(Integer.toString(comment.time));

        String time;
        Date date = new Date(comment.time);
        DateFormat formatter = new SimpleDateFormat("MM-d H:m");
        time = formatter.format(date);

        viewHolder.mCommentTime.setText(time);

        viewHolder.mCommentText.setText(comment.content);
        viewHolder.mVoteNum.setText(Integer.toString(comment.likes));
        Picasso.with(mContext).load(comment.avatar).into((ImageView) viewHolder.mAvatar);

        viewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater
                        = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View loadingView = layoutInflater.inflate(R.layout.comment_dialog_view, null);





                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(loadingView);
                final AlertDialog mAlertDialog=builder.create();

                View.OnClickListener onClickListener=new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAlertDialog.dismiss();
                    }
                };
                loadingView.findViewById(R.id.zantong).setOnClickListener(onClickListener);
                loadingView.findViewById(R.id.jubao).setOnClickListener(onClickListener);
                loadingView.findViewById(R.id.huifu).setOnClickListener(onClickListener);
                loadingView.findViewById(R.id.fuzhi).setOnClickListener(onClickListener);
                mAlertDialog.show();
            }
        });


    }





    private void bindCommentEmpty(RecyclerView.ViewHolder vh,int pos){
        if(mLongComments.size()==0){
            CommentEmptyViewHolder viewHolder=(CommentEmptyViewHolder)vh;
            viewHolder.mImageView.setImageResource(R.drawable.comment_empty);
            viewHolder.mTextView.setText("深度长评虚位以待");
        }
    }

    private void bindCommentNum(RecyclerView.ViewHolder vh,int pos){
        CommentNumViewHolder viewHolder=(CommentNumViewHolder)vh;
        String s;
        if(pos==0){
            s=mLongCommentNum+"条长评";
            viewHolder.mCommentFold.setVisibility(View.INVISIBLE);
        }else{
            final ImageView imageView=viewHolder.mCommentFold;
            s=mShortCommentNum+"条短评";
            viewHolder.mCommentFold.setVisibility(View.VISIBLE);
            viewHolder.mCommentFold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mShortComments.size() == 0) {
                        imageView.setRotation(180);
                        EventBus.getDefault().post(new Message.LoadShortComment("load short message"));
                    } else {
                        imageView.setRotation(0);
                        mShortComments.clear();
                        if(mLongComments.size()>0){
                            mItemCount=mLongComments.size()+mShortComments.size()+2;
                        }else{
                            mItemCount=mShortComments.size()+3;
                        }
                        notifyDataSetChanged();
                    }

                }
            });
        }
        viewHolder.mCommentNum.setText(s);
    }



    @Override
    public int getItemViewType(int position){
        if(mLongComments.size()==0){
            if(position==0||position==2){
                return COMMENT_NUM_ITEM;
            }else if(position==1){
                return COMMENT_EMPTY_ITEM;
            }
            return COMMENT_CONTENT_ITEM;
        }else{
            if(position==0||position==mLongComments.size()+1){
                return COMMENT_NUM_ITEM;
            }
            return COMMENT_CONTENT_ITEM;
        }
    }


}
