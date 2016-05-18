package com.cardbookvr.renderbox;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public final class Time {
    private Time(){}
    static long startTime;
    static long lastFrame;
    static long deltaTime;
    static int frameCount;

    protected static void start(){
        frameCount = 0;
        startTime = System.currentTimeMillis();
        lastFrame = startTime;
    }

    protected static void update(){
        long current =System.currentTimeMillis();
        frameCount++;
        deltaTime = current - lastFrame;
        lastFrame = current;
    }

    public static int getFrameCount(){return frameCount;}

    public static float getTime(){
        return (float)(System.currentTimeMillis() - startTime) / 1000;
    }

    public static float getDeltaTime(){
        return deltaTime * 0.001f;
    }
}
