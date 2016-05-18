package com.cardbookvr.renderboxlib;

import android.os.Bundle;
import android.util.Log;

import com.cardbookvr.renderbox.IRenderBox;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Time;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.components.Cube;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

public class MainActivity extends CardboardActivity implements IRenderBox {
    private static final String TAG = "MainActivity";

    Transform cube;
    Cube cubeObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(new RenderBox(this,this));
        setCardboardView(cardboardView);

    }

    @Override
    public void setup() {
        cube = new Transform();
        cubeObject = new Cube(true);
        cube.addComponent(cubeObject);
        cube.setLocalPosition(2.0f, -2.f, -5.0f);
    }

    @Override
    public void preDraw() {
        float dt = Time.getDeltaTime();
        cube.rotate(dt * 5, dt * 10, dt * 7.5f);
    }

    @Override
    public void postDraw() {
        if (cubeObject.isLooking) {
            Log.d(TAG, "isLooking at Cube");
        }
    }
}
