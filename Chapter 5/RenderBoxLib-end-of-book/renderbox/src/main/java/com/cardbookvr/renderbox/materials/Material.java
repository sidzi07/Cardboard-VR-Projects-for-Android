package com.cardbookvr.renderbox.materials;

import android.opengl.GLES20;
import android.util.Log;

import com.cardbookvr.renderbox.RenderBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public abstract class Material {
    private static final String TAG = "RenderBox.Material";

    protected static final float[] modelView = new float[16];
    protected static final float[] modelViewProjection = new float[16];

    public static int createProgram(int vertexShaderResource, int fragmentShaderResource){
        int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, vertexShaderResource);
        int passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderResource);

        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, passthroughShader);
        GLES20.glLinkProgram(program);
        GLES20.glUseProgram(program);

        RenderBox.checkGLError("Material.createProgram");
        return program;
    }

    public abstract void draw(float[] view, float[] perspective);

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type The type of shader we will be creating.
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The shader object handler.
     */
    public static int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId The resource ID of the raw text file about to be turned into a shader.
     * @return The context of the text file, or null in case of error.
     */
    private static String readRawTextFile(int resId) {
        InputStream inputStream = RenderBox.instance.mainActivity.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
