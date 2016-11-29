package com.zhonglushu.textureview;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;
    private EGLHelper mEglHelper = null;
    private GLRendererImpl mRenderer;
    private float mTouchDownX;
    private float mTouchDownY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        //mTextureView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//隐藏虚拟按键，即navigator bar
        setContentView(mTextureView);

        mRenderer = new GLRendererImpl(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mTouchDownX = x;
                mTouchDownY = y;
                break;
            case MotionEvent.ACTION_UP:
                float deltaX = x - mTouchDownX;
                float deltaY = y - mTouchDownY;
                if(Math.abs(deltaX) > Math.abs(3*deltaY)) {
                    mRenderer.responeVelocity(deltaX / mTextureView.getMeasuredWidth());
                }
                break;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width,
                                          int height) {
        // TODO Auto-generated method stub
        mRenderer.setViewport(width, height);
        mEglHelper = new EGLHelper(surface, mRenderer);
        mEglHelper.drawFrame();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // TODO Auto-generated method stub
        mEglHelper.destoryGL();
        return true;
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width,
                                            int height) {
        // TODO Auto-generated method stub
        mRenderer.resize(width, height);

    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // TODO Auto-generated method stub

    }
}
