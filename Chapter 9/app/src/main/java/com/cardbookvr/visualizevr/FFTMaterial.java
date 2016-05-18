package com.cardbookvr.visualizevr;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.components.RenderObject;
import com.cardbookvr.renderbox.materials.Material;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Schoen and Jonathan on 4/22/2016.
 */
public class FFTMaterial extends Material {
    private static final String TAG = "FFTMaterial";

    static int program = -1; //Initialize to a totally invalid value for setup state
    static int positionParam;
    static int texCoordParam;
    static int textureParam;
    static int MVPParam;
    static int colorParam;
    static int widthParam;

    public float borderWidth = 0.01f;
    public float[] borderColor = new float[]{0.84f, 0.65f, 1f, 1f};

    FloatBuffer vertexBuffer;
    FloatBuffer texCoordBuffer;
    ShortBuffer indexBuffer;
    int numIndices;

    public FFTMaterial() {
        super();
        setupProgram();
    }

    public static void setupProgram() {
        if (program > -1)
            //This means program has been set up
            //(valid program or error)
            return;
        //Create shader program
        program = createProgram(R.raw.fft_vertex, R.raw.fft_fragment);
        RenderBox.checkGLError("Bitmap GenTexture");

        //Get vertex attribute parameters
        positionParam = GLES20.glGetAttribLocation(program, "a_Position");
        RenderBox.checkGLError("Bitmap GenTexture");
        texCoordParam = GLES20.glGetAttribLocation(program, "a_TexCoordinate");
        RenderBox.checkGLError("Bitmap GenTexture");

        //Enable them (turns out this is kind of a big deal ;)
        GLES20.glEnableVertexAttribArray(positionParam);
        RenderBox.checkGLError("Bitmap GenTexture");
        GLES20.glEnableVertexAttribArray(texCoordParam);
        RenderBox.checkGLError("Bitmap GenTexture");

        //Shader-specific parameters
        textureParam = GLES20.glGetUniformLocation(program, "u_Texture");
        MVPParam = GLES20.glGetUniformLocation(program, "u_MVP");
        colorParam = GLES20.glGetUniformLocation(program, "u_Color");
        RenderBox.checkGLError("FFT params");
    }

    public FFTMaterial setBuffers(FloatBuffer vertexBuffer, FloatBuffer texCoordBuffer,
                                  ShortBuffer indexBuffer, int numIndices) {
        //Associate VBO data with this instance of the material
        this.vertexBuffer = vertexBuffer;
        this.texCoordBuffer = texCoordBuffer;
        this.indexBuffer = indexBuffer;
        this.numIndices = numIndices;
        return this;
    }

    @Override
    public void draw(float[] view, float[] perspective) {
        GLES20.glUseProgram(program);

        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, VisualizerBox.fftTexture);

        // Tell the texture uniform sampler to use this texture in
        //the shader by binding to texture unit 0.
        GLES20.glUniform1i(textureParam, 0);

        Matrix.multiplyMM(modelView, 0, view, 0, RenderObject.model, 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        // Set the ModelViewProjection matrix for eye position.
        GLES20.glUniformMatrix4fv(MVPParam, 1, false, modelViewProjection, 0);

        GLES20.glUniform4fv(colorParam, 1, borderColor, 0);

        //Set vertex attributes
        GLES20.glVertexAttribPointer(positionParam, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glVertexAttribPointer(texCoordParam, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, indexBuffer);

        RenderBox.checkGLError("WaveformMaterial draw");
    }

    public static void destroy(){
        program = -1;
    }

}
