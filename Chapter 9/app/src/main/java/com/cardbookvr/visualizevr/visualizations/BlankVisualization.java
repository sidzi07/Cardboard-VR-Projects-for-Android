package com.cardbookvr.visualizevr.visualizations;

import com.cardbookvr.visualizevr.Visualization;
import com.cardbookvr.visualizevr.VisualizerBox;

/**
 * Created by Schoen and Jonathan on 4/22/2016.
 */
public class BlankVisualization extends Visualization {
    static final String TAG = "BlankVisualization";

    public BlankVisualization(VisualizerBox visualizerBox) {
        super(visualizerBox);
    }

    @Override
    public void setup() {
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
    }
}
