package com.cardbookvr.renderbox.components;

import android.opengl.Matrix;

import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.materials.Material;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public abstract class RenderObject extends Component {
    private static final String TAG = "RenderObject";

    protected Material material;
    public static float[] model;
    public static float[] lightingModel;

    public boolean isLooking;
    private static final float YAW_LIMIT = 0.15f;
    private static final float PITCH_LIMIT = 0.15f;
    final float[] modelView = new float[16];

    public RenderObject(){
        super();
        RenderBox.instance.renderObjects.add(this);
    }

    protected static FloatBuffer allocateFloatBuffer(float[] data) {
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(data.length * 4);
        bbVertices.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bbVertices.asFloatBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }

    protected static ShortBuffer allocateShortBuffer(short[] data) {
        ByteBuffer bbVertices = ByteBuffer.allocateDirect(data.length * 2);
        bbVertices.order(ByteOrder.nativeOrder());
        ShortBuffer buffer = bbVertices.asShortBuffer();
        buffer.put(data);
        buffer.position(0);
        return buffer;
    }

    public Material getMaterial(){
        return material;
    }
    public RenderObject setMaterial(Material material){
        this.material = material;
        return this;
    }

    public void draw(float[] view, float[] perspective){
        if(!enabled)
            return;
        //Compute position every frame in case it changed
        transform.drawMatrices();
        material.draw(view, perspective);
        isLooking = isLookingAtObject();
    }

    private boolean isLookingAtObject() {
        float[] initVec = { 0, 0, 0, 1.0f };
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from onNewFrame.
        Matrix.multiplyMM(modelView, 0, RenderBox.headView, 0, model, 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }

}
