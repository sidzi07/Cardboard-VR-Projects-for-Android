package com.cardbookvr.renderbox.components;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cardbookvr.renderbox.R;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.materials.Material;
import com.cardbookvr.renderbox.math.Vector3;
import com.google.vrtoolkit.cardboard.Eye;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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

    public boolean headTracking = true;
    public boolean trailsMode;

    // for custom clear (fullscreen quad and buffers)
    static int program = -1;
    static int positionParam, colorParam;
    static boolean setup;
    public static FloatBuffer vertexBuffer;
    public static ShortBuffer indexBuffer;
    public static final int numIndices = 6;

    public static final float[] COORDS = new float[] {
            -1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f
    };
    public static final short[] INDICES = new short[] {
            0, 1, 2,
            1, 3, 2
    };
    public static float[] customClearColor = new float[]{0, 0,0,0.05f};


    public Camera(){
        //The camera breaks pattern and creates its own Transform
        transform = new Transform();
        setupProgram();
        allocateBuffers();
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
        if (trailsMode) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
            customClear(customClearColor);
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        } else {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        }

        RenderBox.checkGLError("glClear");

        if (headTracking) {
            // Apply the eye transformation to the camera.
            Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);
        } else {
            // copy camera into view
            for (int i = 0; i < camera.length; i++) {
                view[i] = camera[i];
            }
        }

        // Compute lighting position
        RenderBox.instance.mainLight.onDraw(view);

        // Build the ModelView and ModelViewProjection matrices
        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);

        for (RenderObject obj : RenderBox.instance.renderObjects) {
            obj.draw(view, perspective);
        }
        RenderBox.checkGLError("Drawing complete");
    }

    public static void setupProgram() {
        if (program > -1)    //This means program has been set up
            //(valid program or error)
            return;
        //Create shader program
        program = Material.createProgram(R.raw.fullscreen_solid_color_vertex, R.raw.fullscreen_solid_color_fragment);

        //Get vertex attribute parameters
        positionParam = GLES20.glGetAttribLocation(program, "v_Position");

        //Enable vertex attribute parameters
        GLES20.glEnableVertexAttribArray(positionParam);

        //Shader-specific parameters
        colorParam = GLES20.glGetUniformLocation(program, "u_Color");

        RenderBox.checkGLError("Fullscreen Solid Color params");
    }

    public static void allocateBuffers() {
        setup = true;
        vertexBuffer = RenderObject.allocateFloatBuffer(COORDS);
        indexBuffer = RenderObject.allocateShortBuffer(INDICES);
    }

    public static void customClear(float[] clearColor) {
        GLES20.glUseProgram(program);
        // Set the position buffer
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glUniform4fv(colorParam, 1, clearColor, 0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    }

}
