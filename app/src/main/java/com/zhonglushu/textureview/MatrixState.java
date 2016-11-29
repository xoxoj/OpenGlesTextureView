package com.zhonglushu.textureview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayDeque;

/**
 * Created by rambo.huang on 16/11/20.
 */
public class MatrixState {

    protected static final int OBJECT_NUMS = 40;
    protected ArrayDeque<WeatherObject> mDeque = new ArrayDeque<WeatherObject>(OBJECT_NUMS);
    private ValueAnimator valueAnimator = null;

    public void initMatrix(Context context, float bmpWidth, float bmpHeight, float parentWidth, float parentHeight){

        long now = System.currentTimeMillis();
        for(int i = 0; i < OBJECT_NUMS; i++) {
            WeatherObject object = new Snow(context, bmpWidth, bmpHeight, parentWidth, parentHeight);
            object.reset(now + 500 * i);
            mDeque.add(object);
        }

        updateWorldCoordicate();

    }

    private boolean isFullQueue() {
        return true;
    }

    protected void updateWorldCoordicate() {
        for(WeatherObject object : mDeque) {
            float translationY = object.getWorldCoordicateTransY();
            float translationX = object.getWorldCoordicateTransX();
            Log.i("Rambo", "translationX = " + translationX + ", translationY = " + translationY);
            object.currentMatrix = new float[]{ translationX, translationY - object.getWorldCoordicateHeight(), 0,
                    translationX + object.getWorldCoordicateWidth(), translationY - object.getWorldCoordicateHeight(), 0,
                    translationX, translationY, 0,
                    translationX + object.getWorldCoordicateWidth(), translationY, 0 };
        }
    }

    public void responeVelocity(float vx) {
        if(valueAnimator != null && valueAnimator.isStarted()) {
            return;
        }
        valueAnimator = ValueAnimator.ofFloat(0.0f, vx);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                for(WeatherObject object : mDeque) {
                    if(!object.needStart())continue;
                    object.x += value*object.getWorldCoordicateWidth();
                }
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new BounceInterpolator());
        valueAnimator.start();
    }
}
