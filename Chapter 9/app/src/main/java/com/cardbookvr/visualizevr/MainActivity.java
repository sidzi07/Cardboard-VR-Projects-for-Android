package com.cardbookvr.visualizevr;

import android.os.Bundle;

import com.cardbookvr.renderbox.IRenderBox;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Time;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.components.Cube;
import com.cardbookvr.visualizevr.visualizations.FFTVisualization;
import com.cardbookvr.visualizevr.visualizations.GeometricVisualization;
import com.cardbookvr.visualizevr.visualizations.WaveformVisualization;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import java.util.Random;

public class MainActivity extends CardboardActivity implements IRenderBox {
    private static final String TAG = "MainActivity";
    CardboardView cardboardView;

    VisualizerBox visualizerBox;

    float timeToChange = 0f;
    final float CHANGE_DELAY = 3f;
    final Random rand = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(new RenderBox(this, this));
        setCardboardView(cardboardView);

        visualizerBox = new VisualizerBox(cardboardView);
        visualizerBox.visualizations.add(new GeometricVisualization(visualizerBox));
        visualizerBox.visualizations.add(new WaveformVisualization(visualizerBox));
        visualizerBox.visualizations.add(new FFTVisualization(visualizerBox));
    }

    @Override
    public void setup() {
        new Transform()
                .setLocalPosition(0,0,-7)
                .setLocalRotation(45,60,0)
                .addComponent(new Cube(true));
        visualizerBox.setup();
        RenderBox.mainCamera.trailsMode = true;

        for (Visualization viz : visualizerBox.visualizations) {
            viz.activate(false);
        }
    }

    @Override
    public void preDraw() {
        if (Time.getTime() > timeToChange) {
            int idx = rand.nextInt(visualizerBox.visualizations.size() );
            Visualization viz = visualizerBox.visualizations.get(idx);
            viz.activate(!viz.active);
            timeToChange += CHANGE_DELAY;
        }

        visualizerBox.preDraw();
    }

    @Override
    public void postDraw() {
        visualizerBox.postDraw();
    }
}
