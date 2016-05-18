package com.cardbookvr.solarsystem;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Bundle;

import com.cardbookvr.renderbox.IRenderBox;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Time;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.components.Camera;
import com.cardbookvr.solarsystem.RenderBoxExt.components.Sphere;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

public class MainActivity extends CardboardActivity implements IRenderBox {
    private static final String TAG = "SolarSystem";

    Planet[] planets;

    // tighten up the distances (millions km)
    float DISTANCE_FACTOR = 0.5f;
    // this is 100x relative to interplanetary distances
    float SCALE_FACTOR = 0.0001f;
    // animation rate for one earth rotation (seconds per rotation)
    float EDAY_RATE = 10f;
    // rotation scale factor e.g. to animate earth: dt * 24 * DEG_PER_EHOUR
    float DEG_PER_EHOUR = (360f / 24f / EDAY_RATE);
    // animation rate for one earth rotation (seconds per orbit) (real is EDAY_RATE * 365.26)
    float EYEAR_RATE = 1500f;
    // orbit scale factor
    float DEG_PER_EYEAR = (360f / EYEAR_RATE);

    int currPlanet = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardboardView cardboardView = (CardboardView)
                findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(new RenderBox(this, this));
        setCardboardView(cardboardView);
    }

    @Override
    public void setup() {
        Transform origin = new Transform();

        //Stars in the sky
        Transform stars = new Transform()
                .setParent(RenderBox.mainCamera.transform, false)
                .setLocalScale(Camera.Z_FAR * 0.99f, Camera.Z_FAR * 0.99f, Camera.Z_FAR * 0.99f)
                .addComponent(new Sphere(R.drawable.milky_way_tex, false));

        //Sun
        Transform sun = new Transform()
                .setParent(origin, false)
                .setLocalScale(6.963f, 6.963f, 6.963f)
                .addComponent(new Sphere(R.drawable.sun_tex, false));

        //"Sun" light
        RenderBox.instance.mainLight.transform.setPosition(origin.getPosition());
        RenderBox.instance.mainLight.color = new float[]{1, 1, 0.8f, 1};

        // Planets
        setupPlanets(origin);

        // Start looking at Earth
        goToPlanet(currPlanet);
    }

    @Override
    public void preDraw() {
        float dt = Time.getDeltaTime();
        for(int i = 0; i < planets.length; i++) {
            planets[i].preDraw(dt);
        }
    }

    @Override
    public void postDraw() {

    }


    public void setupPlanets(Transform origin) {

        float[] distances = new float[]{57.9f, 108.2f, 149.6f, 227.9f, 778.3f, 1427f, 2871f, 4497f, 5913f};

        float[] fudged_distances = new float[]{57.9f, 108.2f, 149.6f, 227.9f, 400f, 500f, 600f, 700f, 800f};

        float[] radii = new float[]{2440f, 6052f, 6371f, 3390f, 69911f, 58232f, 25362f, 24622f, 1186f};

        float[] rotations = new float[]{1408.8f * 0.05f, 5832f * 0.01f, 24f, 24.6f, 9.84f, 10.2f, 17.9f, 19.1f, 6.39f};

        float[] orbits = new float[]{0.24f, 0.615f, 1.0f, 2.379f, 11.862f, 29.456f, 84.07f, 164.81f, 247.7f};

        int[] texIds = new int[]{
                R.drawable.mercury_tex,
                R.drawable.venus_tex,
                R.drawable.earth_tex,
                R.drawable.mars_tex,
                R.drawable.jupiter_tex,
                R.drawable.saturn_tex,
                R.drawable.uranus_tex,
                R.drawable.neptune_tex,
                R.drawable.pluto_tex
        };

        planets = new Planet[distances.length + 1];
        for (int i = 0; i < distances.length; i++) {
            if (i == 2) {
                planets[i] = new Earth(
                        fudged_distances[i] * DISTANCE_FACTOR,
                        radii[i] * SCALE_FACTOR,
                        rotations[i] * DEG_PER_EHOUR,
                        orbits[i] * DEG_PER_EYEAR * fudged_distances[i] / distances[i],
                        texIds[i],
                        R.drawable.earth_night_tex,
                        origin);
            } else {
                planets[i] = new Planet(
                        fudged_distances[i] * DISTANCE_FACTOR,
                        radii[i] * SCALE_FACTOR,
                        rotations[i] * DEG_PER_EHOUR,
                        orbits[i] * DEG_PER_EYEAR * fudged_distances[i] / distances[i],
                        texIds[i],
                        origin);
            }
        }

        // Create the moon
        planets[distances.length] = new Planet(7.5f, 0.5f, 0, -0.516f,
                R.drawable.moon_tex, planets[2].getTransform());
    }

    void goToPlanet(int index){
        RenderBox.mainCamera.getTransform().setParent(planets[index].getOrbitransform(), false);
        RenderBox.mainCamera.getTransform().setLocalPosition( planets[index].distance,
                        planets[index].radius * 1.5f, planets[index].radius * 2f);
    }

    public void onCardboardTrigger(){
        if (++currPlanet >= planets.length)
            currPlanet = 0;
        goToPlanet(currPlanet);
    }



    public static int loadTexture(final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling

            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(
                    RenderBox.instance.mainActivity.getResources(), resourceId, options);
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

}
