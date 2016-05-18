package com.cardbookvr.renderbox.components;

import com.cardbookvr.renderbox.materials.BorderMaterial;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Schoen and Jonathan on 4/21/2016.
 */
public class Triangle extends RenderObject {
    /*
	    Special triangle for border shader
	    *   0/3 (0,1,0)/(0,1,0) (0,1)/(1,1)
	    	    	       /|\
	    	    	     /  | \
	    	    	    *---*--*
	    	    	    1   2   4
    */

    private static final float YAW_LIMIT = 0.15f;
    private static final float PITCH_LIMIT = 0.15f;
    public static final float[] COORDS = new float[] {
            0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0f, 1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
    };
    public static final float[] TEX_COORDS = new float[] {
            0f, 1f,
            0f, 0f,
            0.5f, 0f,
            1f, 1f,
            1f, 0f
    };
    public static final float[] COLORS = new float[] {
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    };
    public static final float[] NORMALS = new float[] {
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f
    };
    public static final short[] INDICES = new short[] {
            1, 0, 2,
            2, 3, 4
    };

    private static FloatBuffer vertexBuffer;
    private static FloatBuffer colorBuffer;
    private static FloatBuffer normalBuffer;
    private static FloatBuffer texCoordBuffer;
    private static ShortBuffer indexBuffer;
    static final int numIndices = 6;

    static boolean setup;

    public Triangle() {
        super();
        if (!setup) {
            allocateBuffers();
        }
    }

    public static void allocateBuffers() {
        setup = true;
        vertexBuffer = allocateFloatBuffer(COORDS);
        texCoordBuffer = allocateFloatBuffer(TEX_COORDS);
        colorBuffer = allocateFloatBuffer(COLORS);
        normalBuffer = allocateFloatBuffer(NORMALS);
        indexBuffer = allocateShortBuffer(INDICES);
    }

    public void setupBorderMaterial(BorderMaterial material) {
        this.material = material;
        material.setBuffers(vertexBuffer, texCoordBuffer, indexBuffer, numIndices);
    }

}
