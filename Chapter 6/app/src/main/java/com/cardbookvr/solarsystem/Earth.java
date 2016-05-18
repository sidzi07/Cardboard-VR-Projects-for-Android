package com.cardbookvr.solarsystem;

import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.solarsystem.RenderBoxExt.components.Sphere;

/**
 * Created by Schoen and Jonathan on 4/21/2016.
 */
public class Earth extends Planet {

    Transform wobble;

    public Earth(float distance, float radius, float rotation,
                 float orbit, int texId, int nightTexId, Transform origin) {
        super(distance, radius, rotation, orbit, origin);

        wobble = new Transform()
                .setLocalPosition(distance, 0, 0)
                .setParent(orbitTransform, false);

        Transform tilt = new Transform()
                .setLocalRotation(-23.4f, 0, 0)
                .setParent(wobble, false);

        transform
                .setParent(tilt, false)
                .setLocalPosition(0, 0, 0)
                .addComponent(new Sphere(texId, nightTexId));
    }

    public void preDraw(float dt) {
        orbitTransform.rotate(0, dt * orbit, 0);
        wobble.rotate(0, dt * 5, 0);
        transform.rotate(0, dt * -rotation, 0);
    }

}
