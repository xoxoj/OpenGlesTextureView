package com.zhonglushu.textureview;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Created by rambo.huang on 16/11/20.
 */
public class WeatherObject {

    private int[] directValues = new int[]{1, -1, 1, 0, 1, -1, 0, -1, 1, 1, -1, 1, -1, 1, -1, 0, 1, -1, -1, 1};
    private float[] scaleValues = new float[]{0.5f, 0.8f, 0.6f, 1.0f, 0.5f, 0.65f, 0.4f, 0.7f, 0.9f, 1.0f, 0.85f};
    private float[] positionValues = new float[]{-0.4f, -0.36f, -0.32f, -0.25f, -0.22f, -0.16f, -0.1f, -0.05f, 0.0f, 0.06f, 0.1f, 0.14f, 0.21f, 0.28f, 0.32f, 0.38f, 0.42f};
    protected float parentWidth, parentHeight;
    protected float textureWidth;
    protected float textureHeight;
    protected float startX, startY, startZ;
    protected float x, y, z;
    protected long startTime = 0L;
    protected long lastTime = 0L;
    protected long currentTime = 0L;
    protected float horizontalSpeed = 40.0f;
    protected int sportDirection = 0;
    protected float startVelocity = 0.0f;
    protected float accelerateSpeed = 2.0f;
    protected float[] currentMatrix;
    protected boolean hasPrepareStart = false;

    public WeatherObject(Context context, float bmpWidth, float bmpHeight, float parentWidth, float parentHeight) {
        this.parentWidth = parentWidth;
        this.parentHeight = parentHeight;
        float scale = scaleValues[(int)(Math.random() * scaleValues.length)];
        textureWidth = (int)(bmpWidth * scale);
        textureHeight = (int)(bmpHeight * scale);
        startX = x = (float)(0.15 + Math.random()*1.70 - 1.0f);//positionValues[(int)(Math.random() * positionValues.length)];
        startY = y = 1.0f + textureHeight;
    }

    public int getTextureId() {
        return R.drawable.snow;
    }

    public float getWorldCoordicateTransY() {
        return y;
    }

    public boolean isReachBottom() {
        return y >= 1.0f;
    }

    public boolean needStart() {
        return hasPrepareStart = (System.currentTimeMillis() >= startTime)? true : false;
    }

    public float getWorldCoordicateTransX() {
        return x;
    }

    public float getWorldCoordicateWidth() {
        return textureWidth * 2.0f / parentWidth;
    }

    public float getWorldCoordicateHeight() {
        return textureHeight * 2.0f / parentHeight;
    }

    protected void updateNextSportDirection() {
        sportDirection = directValues[(int)(Math.random()*directValues.length)];
    }

    public void reset(long time) {
        lastTime = currentTime = startTime = time;
        startX = x = (float)(0.15 + Math.random()*1.70 - 1.0f);
        startY = y = 1.0f + textureHeight;
    }
}
