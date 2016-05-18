package com.cardbookvr.renderbox.materials;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cardbookvr.renderbox.R;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.components.RenderObject;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Schoen and Jonathan on 4/21/2016.
 */
public class SolidColorLightingMaterial extends Material {
    private static final String TAG = "solidcolorlighting";

    float[] color = new float[4];
    static int program = -1;
    static int positionParam;
    static int colorParam;
    static int normalParam;
    static int modelParam;
    static int MVParam;
    static int MVPParam;
    static int lightPosParam;
    static int lightColParam;

    FloatBuffer vertexBuffer;
    FloatBuffer normalBuffer;
    ShortBuffer indexBuffer;
    int numIndices;

    public SolidColorLightingMaterial(float[] c) {
        super();
        setColor(c);
        setupProgram();
    }

    public void setColor(float[] c) {
        color = c;
    }

    public static void setupProgram(){
        //Already setup?
        if (program != -1) return;

        //Create shader program
        program = createProgram(R.raw.solid_color_lighting_vertex, R.raw.solid_color_lighting_fragment);

        //Get vertex attribute parameters
        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        normalParam = GLES20.glGetAttribLocation(program, "a_Normal");

        //Enable them (turns out this is kind of a big deal ;)
        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(normalParam);

        //Shader-specific parameters
        colorParam = GLES20.glGetUniformLocation(program, "u_Color");
        MVParam = GLES20.glGetUniformLocation(program, "u_MV");
        MVPParam = GLES20.glGetUniformLocation(program, "u_MVP");
        lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos");
        lightColParam = GLES20.glGetUniformLocation(program, "u_LightCol");

        RenderBox.checkGLError("Solid Color Lighting params");
    }

    public void setBuffers(FloatBuffer vertexBuffer, FloatBuffer normalBuffer, ShortBuffer indexBuffer, int numIndices){
        this.vertexBuffer = vertexBuffer;
        this.normalBuffer = normalBuffer;
        this.indexBuffer = indexBuffer;
        this.numIndices = numIndices;
    }

    @Override
    public void draw(float[] view, float[] perspective) {
        GLES20.glUseProgram(program);

        GLES20.glUniform3fv(lightPosParam, 1, RenderBox.instance.mainLight.lightPosInEyeSpace, 0);
        GLES20.glUniform4fv(lightColParam, 1, RenderBox.instance.mainLight.color, 0);

        Matrix.multiplyMM(modelView, 0, view, 0, RenderObject.lightingModel, 0);
        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(MVParam, 1, false, modelView, 0);
        Matrix.multiplyMM(modelView, 0, view, 0, RenderObject.model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        // Set the ModelViewProjection matrix for eye position.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, modelViewProjection, 0);

        GLES20.glUniform4fv(colorParam, 1, color, 0);

        //Set vertex attributes
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normalBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, indexBuffer);
    }

}
