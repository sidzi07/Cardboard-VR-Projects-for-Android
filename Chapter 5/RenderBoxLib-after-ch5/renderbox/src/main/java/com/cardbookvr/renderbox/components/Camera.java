package com.cardbookvr.renderbox.components;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.math.Vector3;
import com.google.vrtoolkit.cardboard.Eye;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public class Camera extends Component {
    private static final String TAG = "renderbox.Camera";

    private static final float Z_NEAR = .1f;
    public static final float Z_FAR = 1000f;

    private final float[] camera = new float[16];
    private final float[] view = new float[16];
    public Transform getTransform(){return transform;}

    public Camera(){
        //The camera breaks pattern and creates its own Transform
        transform = new Transform();
    }

    public void onNewFrame(){
        // Build the camera matrix and apply it to the ModelView.
        Vector3 position = transform.getPosition();
        Matrix.setLookAtM(camera, 0, position.x, position.y,
                position.z + Z_NEAR, position.x, position.y, position.z,
                0.0f, 1.0f, 0.0f);

        RenderBox.checkGLError("onNewFrame");
    }

    public void onDrawEye(Eye eye) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        RenderBox.checkGLError("glClear");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Compute lighting position
        RenderBox.instance.mainLight.onDraw(view);

        // Build the ModelView and ModelViewProjection matrices
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        for(RenderObject obj : RenderBox.instance.renderObjects) {
            obj.draw(view, perspective);
        }
        RenderBox.checkGLError("Drawing complete");
    }

}
