package com.cardbookvr.visualizevr;

/**
 * Created by Schoen and Jonathan on 4/22/2016.
 */
public abstract class Visualization {
    VisualizerBox visualizerBox;            //owner

    public boolean active = true;
    public abstract void activate(boolean enabled);

    public Visualization(VisualizerBox visualizerBox){
        this.visualizerBox = visualizerBox;
    }

    public abstract void setup();
    public abstract void preDraw();
    public abstract void postDraw();

}
