package com.cardbookvr.renderbox.materials;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cardbookvr.renderbox.R;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.components.RenderObject;

import java.nio.FloatBuffer;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public class VertexColorMaterial extends Material {
    static boolean setup;
    static int program;

    static int positionParam;
    static int colorParam;
    static int modelParam;
    static int MVPParam;

    FloatBuffer vertexBuffer;
    FloatBuffer colorBuffer;
    int numIndices;

    public VertexColorMaterial(){
        super();
        setupProgram();
    }

    public static void setupProgram(){
        if (setup) return;
        setup = true;
        //Create shader program
        program = createProgram(R.raw.vertex_color_vertex, R.raw.vertex_color_fragment);

        //Get vertex attribute parameters
        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        colorParam = GLES20.glGetAttribLocation(program, "a_Color");

        //Enable vertex attribute parameters
        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(colorParam);

        //Shader-specific paramteters
        modelParam = GLES20.glGetUniformLocation(program, "u_Model");
        MVPParam = GLES20.glGetUniformLocation(program, "u_MVP");

        RenderBox.checkGLError("Solid Color Lighting params");
    }

    public void setBuffers(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, int numIndices){
        this.vertexBuffer = vertexBuffer;
        this.colorBuffer = colorBuffer;
        this.numIndices = numIndices;
    }

    @Override
    public void draw(float[] view, float[] perspective) {
        Matrix.multiplyMM(modelView, 0, view, 0, RenderObject.model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);

        GLES20.glUseProgram(program);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelParam, 1, false, RenderObject.model, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, colorBuffer);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, modelViewProjection, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numIndices);
    }

    public static void destroy(){
        program = -1;
    }

}
