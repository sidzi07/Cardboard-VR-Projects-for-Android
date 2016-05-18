package com.cardbookvr.renderbox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;

import com.cardbookvr.renderbox.components.Camera;
import com.cardbookvr.renderbox.components.Light;
import com.cardbookvr.renderbox.components.RenderObject;
import com.cardbookvr.renderbox.materials.VertexColorLightingMaterial;
import com.cardbookvr.renderbox.materials.VertexColorMaterial;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public class RenderBox implements CardboardView.StereoRenderer {
    private static final String TAG = "RenderBox";

    public static RenderBox instance;
    public Activity mainActivity;
    IRenderBox callbacks;

    public List<RenderObject> renderObjects = new ArrayList<RenderObject>();

    public static Camera mainCamera;

    public static final float[] headView = new float[16];
    public static final float[] headAngles = new float[3];

    public Light mainLight;


    public RenderBox(Activity mainActivity, IRenderBox callbacks){
        instance = this;
        this.mainActivity = mainActivity;
        this.callbacks = callbacks;
        Time.start();
    }

    @Override
    public void onNewFrame(HeadTransform headTransform) {
        Time.update();
        headTransform.getHeadView(headView, 0);
        headTransform.getEulerAngles(headAngles, 0);
        mainCamera.onNewFrame();
        callbacks.preDraw();
    }

    @Override
    public void onDrawEye(Eye eye) {
        mainCamera.onDrawEye(eye);
    }

    @Override
    public void onFinishFrame(Viewport viewport) {
        callbacks.postDraw();
    }

    @Override
    public void onSurfaceChanged(int i, int i1) {

    }

    @Override
    public void onSurfaceCreated(EGLConfig eglConfig) {
        RenderBox.reset();
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.5f);

        mainLight = new Light();
        new Transform().addComponent(mainLight);

        mainCamera = new Camera();

        checkGLError("onSurfaceCreated");
        callbacks.setup();
    }

    @Override
    public void onRendererShutdown() {

    }

    /**
     * Used to "clean up" compiled shaders, which have to be
     recompiled for a "fresh" activity
     */
    public static void reset(){
        VertexColorMaterial.destroy();
        VertexColorLightingMaterial.destroy();

    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so
     what that error is.
     * @param label Label to report in case of error.
     */
    public static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) !=
                GLES20.GL_NO_ERROR) {
            String errorText = String.format("%s: glError %d, %s",
                    label, error, GLU.gluErrorString(error));
            Log.e(TAG, errorText);
            throw new RuntimeException(errorText);
        }
    }

    public static int loadTexture(final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(
                    RenderBox.instance.mainActivity.getResources(), resourceId, options);
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}
