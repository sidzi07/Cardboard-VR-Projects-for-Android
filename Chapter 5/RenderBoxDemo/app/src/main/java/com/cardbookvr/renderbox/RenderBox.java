package com.cardbookvr.renderbox;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLU;
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
}
