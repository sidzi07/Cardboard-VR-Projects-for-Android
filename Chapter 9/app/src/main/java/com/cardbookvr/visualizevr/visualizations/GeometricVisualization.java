package com.cardbookvr.visualizevr.visualizations;

import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.components.Cube;
import com.cardbookvr.visualizevr.Visualization;
import com.cardbookvr.visualizevr.VisualizerBox;

/**
 * Created by Schoen and Jonathan on 4/22/2016.
 */
public class GeometricVisualization extends Visualization {
    static final String TAG = "GeometricVisualization";

    Transform[] cubes;
    Cube[] cubeRenderers;

    public GeometricVisualization(VisualizerBox visualizerBox) {
        super(visualizerBox);
    }

    public void setup() {
        cubes = new Transform[VisualizerBox.captureSize / 2];
        cubeRenderers = new Cube[VisualizerBox.captureSize / 2];

        float offset = -3f;
        float scaleFactor = (offset * -2) / cubes.length;
        for (int i = 0; i < cubes.length; i++) {
            cubeRenderers[i] = new Cube(true);
            cubes[i] = new Transform()
                    .setLocalPosition(offset, -2, -5)
                    .addComponent(cubeRenderers[i]);
            offset += scaleFactor;
        }
    }

    public void preDraw() {
        if (VisualizerBox.audioBytes != null) {
            float scaleFactor = 3f / cubes.length;
            for (int i = 0; i < cubes.length; i++) {
                cubes[i].setLocalScale(scaleFactor, VisualizerBox.audioBytes[i] * 0.01f, 1);
            }
        }
    }

    public void postDraw() {
    }

    @Override
    public void activate(boolean enabled) {
        active = enabled;
        for (int i = 0; i < cubes.length; i++) {
            cubeRenderers[i].enabled = enabled;
        }
    }

}
