package com.cardbookvr.renderbox.components;

import android.opengl.Matrix;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public class Light extends Component {
    private static final String TAG = "RenderBox.Light";

    public final float[] lightPosInEyeSpace = new float[4];
    public float[] color = new float[]{1,1,1,1};

    public void onDraw(float[] view){
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, transform.getPosition().toFloat4(), 0);
    }
}
