package com.zhonglushu.textureview;

import android.content.Context;

/**
 * Created by rambo.huang on 16/11/18.
 */
public class Snow extends WeatherObject{

    public Snow(Context context, float bmpWidth, float bmpHeight, float parentWidth, float parentHeight) {
        super(context, bmpWidth, bmpHeight, parentWidth, parentHeight);
        startVelocity = 50.0f + (float)(Math.random() * 50);
        accelerateSpeed = 20.0f + (float)(Math.random() * 50);
        //startSport();
    }

    @Override
    public float getWorldCoordicateTransX() {
        if(startTime == 0L) {
            return super.getWorldCoordicateTransX();
        }
        x += getVectorX() / (parentWidth / 2.0f);
        return x;
    }

    /**
     * translate screen height to opengles world coordicate
     * @return
     */
    public float getWorldCoordicateTransY() {
        if(startTime == 0L || !needStart()){
            return super.getWorldCoordicateTransY();
        }
        float vectorY = getVectorY();
        if(vectorY > parentHeight) {
            reset(System.currentTimeMillis() + 500);
        } else {
            y = (parentHeight / 2.0f - vectorY) / (parentHeight / 2.0f);
        }
        return y;
    }

    public float getVectorY() {
        float t = (System.currentTimeMillis() - startTime) / 1000.0f;
        return startVelocity * t + t * t * accelerateSpeed / 2.0f;
    }

    public float getVectorX() {
        if(getVectorY() + textureHeight < parentHeight) {
            long now = System.currentTimeMillis();
            long deltaTime = now - lastTime;
            long tempTime = now - currentTime;
            if(tempTime > 10){
                currentTime += tempTime;
                float vectorX = horizontalSpeed * tempTime  * sportDirection / 1000.0f;
                if(deltaTime > 4000) {
                    lastTime = currentTime;
                    updateNextSportDirection();
                }
                return vectorX;
            }
        }
        return 0.0f;
    }

    @Override
    public int getTextureId() {
        return R.drawable.snow;
    }
}
