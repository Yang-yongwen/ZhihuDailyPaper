package com.yangyongwen.zhihudailypaper.themeList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.dataStructure.Editor;
import com.yangyongwen.zhihudailypaper.dataStructure.Story;
import com.yangyongwen.zhihudailypaper.dataStructure.ThemeContent;
import com.yangyongwen.zhihudailypaper.dataStructure.TopStory;
import com.yangyongwen.zhihudailypaper.storycontent.StoryContentActivity;
import com.yangyongwen.zhihudailypaper.ui.CircleIndicator;
import com.yangyongwen.zhihudailypaper.utils.LogUtils;

import java.util.ArrayList;

/**
 * Created by samsung on 2016/3/31.
 */
public class ThemeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final String TAG= LogUtils.makeLogTag(ThemeListAdapter.class);

    private static final int THEME_HEADER=0;
    private static final int THEME_EDITOR=1;
    private static final int STORY_ITEM=2;

    public static final String STORY_ID="story_id";
    public static final String CURRENT_ID="current_id";


    private ThemeContent mThemeContent;

    private ArrayList<String> mStoryIds;




    private Context mContext;




    public ThemeListAdapter(Context context){
        mContext=context;
        mThemeContent=new ThemeContent();
        mStoryIds=new ArrayList<String>();
    }

    public void setThemeContent(ThemeContent content){
        mThemeContent=content;
        mStoryIds.clear();
        for(Story story:mThemeContent.stories){
            mStoryIds.add(Integer.toString(story.getId()));
        }
    }





    private  class ThemeHeaderViewHolder extends RecyclerView.ViewHolder{

        ImageView mThemeIcon;
        TextView mThemeDescription;

        public ThemeHeaderViewHolder(View v){
            super(v);
            mThemeIcon=(ImageView)v.findViewById(R.id.theme_icon);
            mThemeDescription=(TextView)v.findViewById(R.id.theme_description);
        }

    }



    private  class ThemeEditorViewHolder extends RecyclerView.ViewHolder{

        LinearLayout mThemeContainer;

        public ThemeEditorViewHolder(View v){
            super(v);
            mThemeContainer=(LinearLayout)v.findViewById(R.id.theme_editor_container);
        }
    }


    private  class StoryItemViewHolder extends RecyclerView.ViewHolder{

        ImageView mStoryIcon;
        TextView mStoryTitle;
        FrameLayout mStoryIconContainer;

        public StoryItemViewHolder(View v){
            super(v);
            mStoryIcon=(ImageView)v.findViewById(R.id.story_item_icon);
            mStoryTitle=(TextView)v.findViewById(R.id.story_item_title);
            mStoryIconContainer=(FrameLayout)v.findViewById(R.id.story_item_icon_container);

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
        String getOnClickId(){
            int pos=this.getAdapterPosition()-2;
            final String id=mStoryIds.get(pos);
            return id;
        }

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        RecyclerView.ViewHolder vh=null;
        View v=null;
        switch (viewType){
            case STORY_ITEM:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item_cardview,parent,false);
                vh=new StoryItemViewHolder(v);
                break;
            case THEME_HEADER:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_list_header,parent,false);
                vh=new ThemeHeaderViewHolder(v);
                break;
            case THEME_EDITOR:
                v= LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_editor_item,parent,false);
                vh=new ThemeEditorViewHolder(v);
                break;
            default:
                break;
        }
        return vh;
    }





    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,int position){

        switch (getItemViewType(position)){
            case THEME_HEADER:
                bindThemeHeaderViewHolder(holder,position);
                break;
            case THEME_EDITOR:
                bindThemeEditorViewHolder(holder, position);
                break;
            case STORY_ITEM:
                bindStoryItemViewHolder(holder, position);
                break;
            default:
                break;
        }

    }


    private void bindThemeHeaderViewHolder(RecyclerView.ViewHolder holder,int position){
        ThemeHeaderViewHolder viewHolder=(ThemeHeaderViewHolder) holder;

        viewHolder.mThemeDescription.setText(mThemeContent.description);
        Picasso.with(mContext).load(mThemeContent.background).into(viewHolder.mThemeIcon);

    }

    private void bindThemeEditorViewHolder(RecyclerView.ViewHolder holder,int position){
        ThemeEditorViewHolder themeEditorViewHolder=(ThemeEditorViewHolder)holder;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Editor[] editors=mThemeContent.editors;

        themeEditorViewHolder.mThemeContainer.removeAllViews();

        for(Editor editor:editors){
            View view=inflater.inflate(R.layout.editor_avatar_container,null);
            ImageView imageView=(ImageView)view.findViewById(R.id.editor_avatar);
            Picasso.with(mContext).load(editor.avatar).into(imageView);
            themeEditorViewHolder.mThemeContainer.addView(view);
        }


    }
    private void bindStoryItemViewHolder(RecyclerView.ViewHolder holder,int position){
        StoryItemViewHolder storyItemViewHolder=(StoryItemViewHolder)holder;

        storyItemViewHolder.mStoryTitle.setText(mThemeContent.stories[position-2].getTitle());

        String imageUrl=null;

        if(mThemeContent.stories[position-2].getImages()!=null){
            imageUrl=mThemeContent.stories[position-2].getImages()[0];
        }
        if(imageUrl!=null){
            Picasso.with(mContext).load(imageUrl).into(storyItemViewHolder.mStoryIcon);
        }

    }




    @Override
    public int getItemCount(){
        if(mThemeContent!=null&&mThemeContent.stories!=null){
            return mThemeContent.stories.length+2;
        }else{
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position){
        if(position==0){
            return THEME_HEADER;
        }else if(position==1){
            return THEME_EDITOR;
        }else{
            return STORY_ITEM;
        }
    }

}
