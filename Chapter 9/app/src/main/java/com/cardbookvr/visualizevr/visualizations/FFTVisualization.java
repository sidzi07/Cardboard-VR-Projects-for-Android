package com.cardbookvr.visualizevr.visualizations;

import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.components.Plane;
import com.cardbookvr.renderbox.components.RenderObject;
import com.cardbookvr.visualizevr.FFTMaterial;
import com.cardbookvr.visualizevr.Visualization;
import com.cardbookvr.visualizevr.VisualizerBox;

/**
 * Created by Schoen and Jonathan on 4/22/2016.
 */
public class FFTVisualization extends Visualization {
    static final String TAG = "FFTVisualization";

    RenderObject plane;

    public FFTVisualization(VisualizerBox visualizerBox) {
        super(visualizerBox);
    }

    @Override
    public void setup() {
        plane = new Plane().setMaterial(new FFTMaterial()
                .setBuffers(Plane.vertexBuffer, Plane.texCoordBuffer, Plane.indexBuffer, Plane.numIndices));

        new Transform()
                .setLocalPosition(5, 0, 0)
                .setLocalRotation(0, -90, 0)
                .addComponent(plane);
    }

    @Override
    public void preDraw() {
    }

    @Override
    public void postDraw() {
    }

    @Override
    public void activate(boolean enabled) {
        active = enabled;
        plane.enabled = enabled;
    }

}
