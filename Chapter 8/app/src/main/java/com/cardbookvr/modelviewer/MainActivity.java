package com.cardbookvr.modelviewer;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.cardbookvr.renderbox.IRenderBox;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.math.Quaternion;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

public class MainActivity extends CardboardActivity implements IRenderBox {
    private static final String TAG = "ModelViewer";
    CardboardView cardboardView;

    Transform model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(new RenderBox(this, this));
        setCardboardView(cardboardView);
    }

    @Override
    public void setup() {
        RenderBox.instance.mainCamera.headTracking = false;

        ModelObject modelObj;
        Uri intentUri = getIntent().getData();
        if (intentUri != null) {
            Log.d(TAG, "!!!! intent " + intentUri.getPath());
            modelObj = new ModelObject(intentUri.getPath());
        } else {
            // default object
            modelObj = new ModelObject(R.raw.teapot);
        }

        float scalar = modelObj.normalScalar();
        model = new Transform()
                .setLocalPosition(0, 0, -3)
                .setLocalScale(scalar, scalar, scalar)
                .addComponent(modelObj);
    }

    @Override
    public void preDraw() {
        float[] hAngles = RenderBox.instance.headAngles;
        Quaternion rot = new Quaternion();
        rot.setEulerAnglesRad(hAngles[0], hAngles[1], hAngles[2]);
        model.setLocalRotation(rot.conjugate());
    }

    @Override
    public void postDraw() {

    }
}
