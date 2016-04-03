package com.yangyongwen.zhihudailypaper.homePage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yangyongwen.zhihudailypaper.R;
import com.yangyongwen.zhihudailypaper.storycontent.StoryContentActivity;

import java.util.ArrayList;

/**
 * Created by yangyongwen on 16/2/29.
 */
public class TopStoryFragment extends Fragment{

    public static final String ARG_OBJECT = "object";



    @Override
    public View onCreateView(final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.topstory_viewpager_container, container, false);



        Bundle args = getArguments();

        String image=args.getString(TopStoryAdapter.IMAGE);
        String title=args.getString(TopStoryAdapter.TITLE);


        TextView textView=(TextView)rootView.findViewById(R.id.topstory_title);
        textView.setText(title);

        ImageView imageView=(ImageView)rootView.findViewById(R.id.topstory_icon);
        Picasso.with(getActivity().getApplicationContext()).load(image).into(imageView);



        return rootView;
    }




}
