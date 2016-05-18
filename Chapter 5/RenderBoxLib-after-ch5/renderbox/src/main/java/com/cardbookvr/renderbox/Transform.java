package com.cardbookvr.renderbox;

import com.cardbookvr.renderbox.components.Component;
import com.cardbookvr.renderbox.components.RenderObject;
import com.cardbookvr.renderbox.math.Matrix4;
import com.cardbookvr.renderbox.math.Quaternion;
import com.cardbookvr.renderbox.math.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Schoen and Jonathan on 4/16/2016.
 */
public class Transform {
    private static final String TAG = "RenderBox.Transform";

    List<Component> components = new ArrayList<Component>();

    private Vector3 localPosition = new Vector3(0,0,0);
    private Quaternion localRotation = new Quaternion();
    private Vector3 localScale = new Vector3(1,1,1);

    private Transform parent = null;


    public Transform() {}

    public Transform addComponent(Component component){
        component.transform = this;
        return this;
    }

    public List<Component> getComponents(){
        return components;
    }

    /**** parent methods ****/

    public Transform setParent(Transform Parent){
        setParent(parent, true);
        return this;
    }

    public Transform setParent(Transform parent, boolean updatePosition){
        if(this.parent == parent) //Early-out if setting same parent--don't do anything
            return this;
        if(parent == null){
            unParent(updatePosition);
            return this;
        }

        if(updatePosition){
            Vector3 tmp_position = getPosition();
            this.parent = parent;
            setPosition(tmp_position);
        } else {
            this.parent = parent;
        }
        return this;
    }

    public Transform upParent(){
        unParent(true);
        return this;
    }

    public Transform unParent(boolean updatePosition){
        if(parent == null) //Early out--we already have no parent
            return this;
        if(updatePosition){
            localPosition = getPosition();
        }
        parent = null;
        return this;
    }

    /**** position methods ****/

    public Transform setPosition(float x, float y, float z){
        if(parent != null){
            localPosition = new Vector3(x, y, z).subtract(parent.getPosition());
        } else {
            localPosition = new Vector3(x, y, z);
        }
        return this;
    }

    public Transform setPosition(Vector3 position){
        if(parent != null){
            localPosition = new Vector3(position).subtract(parent.getPosition());
        } else {
            localPosition = position;
        }
        return this;
    }

    public Vector3 getPosition(){
        if(parent != null){
            return Matrix4.TRS(parent.getPosition(), parent.getRotation(),
                    parent.getScale()).multiplyPoint3x4(localPosition);
        }
        return localPosition;
    }

    public Transform setLocalPosition(float x, float y, float z){
        localPosition = new Vector3(x, y, z);
        return this;
    }

    public Transform setLocalPosition(Vector3 position){
        localPosition = position;
        return this;
    }

    public Vector3 getLocalPosition(){
        return localPosition;
    }

    /**** rotation methods ****/

    public Transform setRotation(float pitch, float yaw,
                                 float roll){
        if(parent != null){
            localRotation = new Quaternion(parent.getRotation()).
                    multiply(new Quaternion().setEulerAngles(pitch, yaw,
                            roll).conjugate()).conjugate();
        } else {
            localRotation = new Quaternion().setEulerAngles(pitch,
                    yaw, roll);
        }
        return this;
    }

    /**
     * Set the rotation of the object in global space
     * Note: if this object has a parent, setRoation modifies the input rotation!
     * @param rotation
     */
    public Transform setRotation(Quaternion rotation){
        if(parent != null){
            localRotation = new Quaternion(parent.getRotation()).multiply(rotation.conjugate()).conjugate();
        } else {
            localRotation = rotation;
        }
        return this;
    }

    public Quaternion getRotation(){
        if(parent != null){
            return new Quaternion(parent.getRotation()).multiply(localRotation);
        }
        return localRotation;
    }

    public Transform setLocalRotation(float pitch, float yaw, float roll) {
        localRotation = new Quaternion().setEulerAngles(pitch, yaw, roll);
        return this;
    }

    public Transform setLocalRotation(Quaternion rotation){
        localRotation = rotation;
        return this;
    }

    public Quaternion getLocalRotation(){
        return localRotation;
    }

    public Transform rotate(float pitch, float yaw, float roll){
        localRotation.multiply(new Quaternion().setEulerAngles(pitch, yaw, roll));
        return this;
    }

    /**** scale methods ****/

    public Vector3 getScale(){
        if(parent != null){
            Matrix4 result = new Matrix4();
            result.setRotate(localRotation);
            return new Vector3(parent.getScale()).scale(localScale);
        }
        return localScale;
    }

    public Transform setLocalScale(float x, float y, float z){
        localScale = new Vector3(x,y,z);
        return this;
    }

    public Transform setLocalScale(Vector3 scale){
        localScale = scale;
        return this;
    }

    public Vector3 getLocalScale(){
        return localScale;
    }

    public Transform scale(float x, float y, float z){
        localScale.scale(x, y, z);
        return this;
    }

    /*** utility methods ****/

    public float[] toFloatMatrix(){
        return Matrix4.TRS(getPosition(), getRotation(),
                getScale()).val;
    }

    public float[] toLightMatrix(){
        return Matrix4.TR(getPosition(), getRotation()).val;
    }

    /**
     * Set up the lighting model and model matrices for a draw
     call
     * Since the lighting model is an intermediate step, it makes
     sense to combine this call
     */
    public void drawMatrices() {
        Matrix4 modelMatrix = Matrix4.TR(getPosition(), getRotation());
        RenderObject.lightingModel = modelMatrix.val;
        modelMatrix = new Matrix4(modelMatrix);
        RenderObject.model = modelMatrix.scale(getScale()).val;
    }

}
