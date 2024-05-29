package com.example.instashare.Activity;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.example.instashare.Model.Direction;
import com.example.instashare.Model.ZeroGravityAnimation;
import com.example.instashare.R;

public class Emoji {

    private ViewGroup container;
    private ViewGroup rootView;
    private Context context;

    public Emoji(ViewGroup container, ViewGroup rootView, Context context) {
        this.container = container;
        this.rootView = rootView;
        this.context = context;
    }

    public Emoji(){

    }
    public void flyEmoji(final int resId,String state) {
        ZeroGravityAnimation animation = new ZeroGravityAnimation();
        animation.setCount(1);
        animation.setScalingFactor(2.0f);
        if(state.equals("friend")){
            animation.setOriginationDirection(Direction.BOTTOM);
            animation.setDestinationDirection(Direction.TOP);
        } else{
            animation.setOriginationDirection(Direction.TOP);
            animation.setDestinationDirection(Direction.BOTTOM);
        }

        animation.setImage(resId);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        container = rootView.findViewById(R.id.animation_holder);
        animation.play((Activity) context,container);
    }

    public void emoji(String e, String state){
        for (int i = 0; i < 15; i++) {
            switch(e){
                case "1":
                    flyEmoji(R.drawable.emoji_laughing, state);
                    break;
                case "2":
                    flyEmoji(R.drawable.emoji_love, state);
                    break;
                case "3":
                    flyEmoji(R.drawable.emoji_wow, state);
                    break;
                case "4":
                    flyEmoji(R.drawable.emoji_cry, state);
                    break;
            }
        }
    }
}
