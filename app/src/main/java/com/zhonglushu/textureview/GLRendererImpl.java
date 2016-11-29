package com.zhonglushu.textureview;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * Created by rambo.huang on 16/11/15.
 */
public class GLRendererImpl implements EGLHelper.GLRenderer {

    private int mProgramObject;
    private int mWidth;
    private int mHeight;
    private FloatBuffer mVertices;
    private ShortBuffer mTexCoords;
    private Context mContext;
    private static String TAG = "GLRendererImpl";

    private float[] mVerticesData;
    private final short[] mTexCoordsData = {0, 1, 1, 1, 0, 0, 1, 0};

    private int mVerticesIndex;
    private int mTextureCoordicateIndex;
    //private int mTextureIndex;
    //private int mUnitMatrixIndex;
    private MatrixState mMatrixState = new MatrixState();
    private int mTtexID;
    private Bitmap texture;

    public GLRendererImpl(Context ctx)
    {
        mContext = ctx;
    }

    public void setViewport(int width, int height)
    {
        mWidth = width;
        mHeight = height;

        texture = BitmapFactory.decodeResource(mContext.getResources(), getTextureId(), null);

        mMatrixState.initMatrix(mContext, texture.getWidth(), texture.getHeight(), width, height);

        mTexCoords = ByteBuffer.allocateDirect(mTexCoordsData.length * 2)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        mTexCoords.put(mTexCoordsData).position(0);
    }

    public void initGL()
    {
        //comipleAndLinkProgram();
        String vShader = ShaderUtil.loadFromAssetsFile("vertex_tex.sh", mContext.getResources());
        String fShader = ShaderUtil.loadFromAssetsFile("frag_tex.sh", mContext.getResources());
        mProgramObject = ShaderUtil.createProgram(vShader, fShader);

        //mTextureIndex = GLES20.glGetUniformLocation(mProgramObject, "u_Texture");
        //mUnitMatrixIndex = GLES20.glGetUniformLocation(mProgramObject, "unitMatrix");

        // Bind vPosition to attribute 0
        mVerticesIndex = GLES20.glGetAttribLocation(mProgramObject, "a_position");
        mTextureCoordicateIndex = GLES20.glGetAttribLocation(mProgramObject, "a_texCoords");

        loadTexture();

        GLES20.glClearColor(0,  0, 0, 0);
    }

    public void resize(int width, int height)
    {
        mWidth = width;
        mHeight = height;

    }

    public void responeVelocity(float vx) {
        mMatrixState.responeVelocity(vx);
    }

    @Override
    public void drawFrame() {
        // TODO Auto-generated method stub
        GLES20.glViewport(0, 0, mWidth, mHeight);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glUseProgram(mProgramObject);

        mMatrixState.updateWorldCoordicate();
        for(WeatherObject object : mMatrixState.mDeque) {
            if(!object.needStart())continue;
            mVerticesData = object.currentMatrix;

            mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
            mVertices.put(mVerticesData).position(0);

            GLES20.glVertexAttribPointer(mVerticesIndex, 3, GLES20.GL_FLOAT, false, 0, mVertices);

            GLES20.glEnableVertexAttribArray(mVerticesIndex);

            GLES20.glVertexAttribPointer(mTextureCoordicateIndex, 2, GLES20.GL_SHORT, false, 0, mTexCoords);

            GLES20.glEnableVertexAttribArray(mTextureCoordicateIndex);

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTtexID);

            //GLES20.glUniform1f(mTextureIndex, mTextureIndex);
            //GLES20.glUniformMatrix4fv(mUnitMatrixIndex, 1, false, mMatrixState.getUnitMatrix(), 0);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }

        //Log.i("GLRendererImpl", "drawing..." + mWidth);
    }

    private void loadTexture() {
        if (texture != null) {
            int[] texID = new int[1];
            GLES20.glGenTextures(1, texID, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texID[0]);
            mTtexID = texID[0];

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR);

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_REPEAT);

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,
                    0,
                    GLUtils.getInternalFormat(texture),
                    texture,
                    GLUtils.getType(texture),
                    0);
            texture.recycle();
        }
    }

    private int getTextureId() {
        return R.drawable.snow;
    }

    private int loadShader(int shaderType, String shaderSource) {
        int shader;
        int[] compiled = new int[1];

        // Create the shader object
        shader = GLES20.glCreateShader(shaderType);

        if (shader == 0)
            return 0;

        // Load the shader source
        GLES20.glShaderSource(shader, shaderSource);

        // Compile the shader
        GLES20.glCompileShader(shader);

        // Check the compile status
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);

        if (compiled[0] == 0) {
            Log.e(TAG, GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    /*private void comipleAndLinkProgram() {
        String vShaderStr = "attribute vec4 a_position;    \n"
                +"attribute vec2 a_texCoords; \n"
                +"varying vec2 v_texCoords; \n"
                + "void main()                  \n"
                + "{                            \n"
                + "   gl_Position = a_position;  \n"
                +"    v_texCoords = a_texCoords; \n"
                + "}                            \n";

        String fShaderStr = "precision mediump float;                     \n"
                +"uniform sampler2D u_Texture; \n"
                +"varying vec2 v_texCoords; \n"
                + "void main()                                  \n"
                + "{                                            \n"
                + "  gl_FragColor = texture2D(u_Texture, v_texCoords) ;\n"
                + "}                                            \n";

        int vertexShader;
        int fragmentShader;
        int programObject;
        int[] linked = new int[1];

        // Load the vertex/fragment shaders
        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fShaderStr);

        // Create the program object
        programObject = GLES20.glCreateProgram();

        if (programObject == 0)
            return ;

        GLES20.glAttachShader(programObject, vertexShader);
        GLES20.glAttachShader(programObject, fragmentShader);

        // Bind vPosition to attribute 0
        GLES20.glBindAttribLocation(programObject, 0, "a_position");
        GLES20.glBindAttribLocation(programObject, 1, "a_texCoords");

        // Link the program
        GLES20.glLinkProgram(programObject);

        // Check the link status
        GLES20.glGetProgramiv(programObject, GLES20.GL_LINK_STATUS, linked, 0);

        if (linked[0] == 0) {
            Log.e(TAG, "Error linking program:");
            Log.e(TAG, GLES20.glGetProgramInfoLog(programObject));
            GLES20.glDeleteProgram(programObject);
            return  ;
        }

        mProgramObject = programObject;
    }*/
}
