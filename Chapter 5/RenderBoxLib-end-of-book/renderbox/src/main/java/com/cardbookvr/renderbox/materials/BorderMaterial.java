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
public class BorderMaterial extends Material {
    private static final String TAG = "bordermaterial";

    int textureId;
    public float borderWidth = 0.1f;
    public float[] borderColor = new float[]{0, 0, 0, 1};
    // black
    static int program = -1;
    //Initialize to a totally invalid value for setup state
    static int positionParam;
    static int texCoordParam;
    static int textureParam;
    static int MVPParam;
    static int colorParam;
    static int widthParam;

    FloatBuffer vertexBuffer;
    FloatBuffer texCoordBuffer;
    ShortBuffer indexBuffer;
    int numIndices;

    public BorderMaterial() {
        super();
        setupProgram();
    }

    public static void setupProgram() {
        //Already setup?
        if (program > -1)
            return;

        //Create shader program
        program = createProgram(R.raw.border_vertex, R.raw.border_fragment);

        //Get vertex attribute parameters
        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        texCoordParam = GLES20.glGetAttribLocation(program, "a_TexCoordinate");

        //Enable them (turns out this is kind of a big deal ;)
        GLES20.glEnableVertexAttribArray(positionParam);
        GLES20.glEnableVertexAttribArray(texCoordParam);

        //Shader-specific parameters
        textureParam = GLES20.glGetUniformLocation(program, "u_Texture");
        MVPParam = GLES20.glGetUniformLocation(program, "u_MVP");
        colorParam = GLES20.glGetUniformLocation(program, "u_Color");
        widthParam = GLES20.glGetUniformLocation(program, "u_Width");
        RenderBox.checkGLError("Border params");
    }

    public void setBuffers(FloatBuffer vertexBuffer, FloatBuffer texCoordBuffer,
                           ShortBuffer indexBuffer, int numIndices) {
        //Associate VBO data with this instance of the material
        this.vertexBuffer = vertexBuffer;
        this.texCoordBuffer = texCoordBuffer;
        this.indexBuffer = indexBuffer;
        this.numIndices = numIndices;
    }

    public void setTexture(int textureHandle) {
        textureId = textureHandle;
    }

    @Override
    public void draw(float[] view, float[] perspective) {
        GLES20.glUseProgram(program);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        // Tell the texture uniform sampler to use this texture in
        // the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureParam, 0);

        Matrix.multiplyMM(modelView, 0, view, 0, RenderObject.model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        // Set the ModelViewProjection matrix for eye position.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, modelViewProjection, 0);

        GLES20.glUniform4fv(colorParam, 1, borderColor, 0);
        GLES20.glUniform1f(widthParam, borderWidth);

        //Set vertex attributes
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(texCoordParam, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        RenderBox.checkGLError("Border material draw");
    }

    public static void destroy() {
        program = -1;
    }

}
