package com.cardbookvr.renderbox.math;
/**
 * Created by mtsch on 11/16/2015.
 * Simple 3D vector class.  Handles basic vector math for 3D vectors.
 * Somewhat sourced from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Vector3.java
 */
//TODO: migrate to class with final values, more like a struct/like Unity
public final class Vector3 {
	public float x;
	public float y;
	public float z;

	public static final Vector3 zero = new Vector3(0, 0, 0);
	public static final Vector3 up = new Vector3(0, 1, 0);
	public static final Vector3 down = new Vector3(0, -1, 0);
	public static final Vector3 left = new Vector3(-1, 0, 0);
	public static final Vector3 right = new Vector3(1, 0, 0);
	public static final Vector3 forward = new Vector3(0, 0, 1);
	public static final Vector3 backward = new Vector3(0, 0, -1);

	public Vector3() {
	}

	public Vector3(float xValue, float yValue, float zValue) {
		set(xValue, yValue, zValue);
	}

	public Vector3(Vector3 other) {
		set(other);
	}

	public Vector3(float[] vec){
		//Assume array has at least 3 elements--speed optimization
		x = vec[0];
		y = vec[1];
		z = vec[2];
	}

	public final Vector3 add(Vector3 other) {
		x += other.x;
		y += other.y;
		z += other.z;
		return this;
	}

	public final Vector3 add(float otherX, float otherY, float otherZ) {
		x += otherX;
		y += otherY;
		z += otherZ;
		return this;
	}

	public final Vector3 subtract(Vector3 other) {
		x -= other.x;
		y -= other.y;
		z -= other.z;
		return this;
	}

	public final Vector3 multiply(float magnitude) {
		x *= magnitude;
		y *= magnitude;
		z *= magnitude;
		return this;
	}

	public final Vector3 multiply(Vector3 other) {
		x *= other.x;
		y *= other.y;
		z *= other.z;
		return this;
	}

	public final Vector3 divide(float magnitude) {
		if (magnitude != 0.0f) {
			x /= magnitude;
			y /= magnitude;
			z /= magnitude;
		}
		return this;
	}

	public final Vector3 set(Vector3 other) {
		x = other.x;
		y = other.y;
		z = other.z;
		return this;
	}

	public final Vector3 set(float xValue, float yValue, float zValue) {
		x = xValue;
		y = yValue;
		z = zValue;
		return this;
	}
	public final Vector3 scale(float xValue, float yValue, float zValue) {
		x *= xValue;
		y *= yValue;
		z *= zValue;
		return this;
	}
	public final Vector3 scale(Vector3 scale) {
		x *= scale.x;
		y *= scale.y;
		z *= scale.z;
		return this;
	}

	public final float dot(Vector3 other) {
		return (x * other.x) + (y * other.y) + (z * other.z);
	}

	public final float length() {
		return (float) Math.sqrt(length2());
	}

	public final float length2() {
		return (x * x) + (y * y) + (z * z);
	}

	public final float distance2(Vector3 other) {
		float dx = x - other.x;
		float dy = y - other.y;
		float dz = z - other.z;
		return (dx * dx) + (dy * dy) + (dz * dz);
	}

	public Vector3 normalize() {
		final float magnitude = length();

		// TODO: I'm choosing safety over speed here.
		if (magnitude != 0.0f) {
			x /= magnitude;
			y /= magnitude;
			z /= magnitude;
		}

		return this;
	}

	public final Vector3 zero() {
		set(0.0f, 0.0f, 0.0f);
		return this;
	}
	public float[] toFloat3(){
		return new float[]{x,y,z};
	}
	public float[] toFloat4(){
		return new float[]{x,y,z,1};
	}
	public float[] toFloat4(float w){
		return new float[]{x,y,z,w};
	}
	public String toString(){
		return String.format("Vector3(%f,%f,%f)", x, y, z);
	}
}