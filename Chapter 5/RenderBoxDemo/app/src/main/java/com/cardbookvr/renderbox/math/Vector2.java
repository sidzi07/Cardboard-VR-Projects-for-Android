package com.cardbookvr.renderbox.math;

/**
 * Created by mtsch on 11/16/2015.
 */
public class Vector2 {
	public float x;
	public float y;

	public static final Vector2 zero = new Vector2(0, 0);
	public static final Vector2 up = new Vector2(0, 1);
	public static final Vector2 down = new Vector2(0, -1);
	public static final Vector2 left = new Vector2(-1, 0);
	public static final Vector2 right = new Vector2(1, 0);

	public Vector2() {
	}

	public Vector2(float xValue, float yValue) {
		set(xValue, yValue);
	}

	public Vector2(Vector2 other) {
		set(other);
	}

	public Vector2(float[] vec){
		//Assume array has at least 2 elements--speed optimization
		x = vec[0];
		y = vec[1];
	}

	public final Vector2 add(Vector2 other) {
		x += other.x;
		y += other.y;
		return this;
	}

	public final Vector2 add(float otherX, float otherY, float otherZ) {
		x += otherX;
		y += otherY;
		return this;
	}

	public final Vector2 subtract(Vector2 other) {
		x -= other.x;
		y -= other.y;
		return this;
	}

	public final Vector2 multiply(float magnitude) {
		x *= magnitude;
		y *= magnitude;
		return this;
	}

	public final Vector2 multiply(Vector2 other) {
		x *= other.x;
		y *= other.y;
		return this;
	}

	public final Vector2 divide(float magnitude) {
		if (magnitude != 0.0f) {
			x /= magnitude;
			y /= magnitude;
		}
		return this;
	}

	public final Vector2 set(Vector2 other) {
		x = other.x;
		y = other.y;
		return this;
	}

	public final Vector2 set(float xValue, float yValue) {
		x = xValue;
		y = yValue;
		return this;
	}
	public final Vector2 scale(float xValue, float yValue) {
		x *= xValue;
		y *= yValue;
		return this;
	}
	public final Vector2 scale(Vector2 scale) {
		x *= scale.x;
		y *= scale.y;
		return this;
	}

	public final float dot(Vector2 other) {	return (x * other.x) + (y * other.y); }

	public final float length() {
		return (float) Math.sqrt(length2());
	}

	public final float length2() {
		return (x * x) + (y * y);
	}

	public final float distance2(Vector2 other) {
		float dx = x - other.x;
		float dy = y - other.y;
		return (dx * dx) + (dy * dy);
	}

	public Vector2 normalize() {
		final float magnitude = length();

		// TODO: I'm choosing safety over speed here.
		if (magnitude != 0.0f) {
			x /= magnitude;
			y /= magnitude;
		}

		return this;
	}

	public final Vector2 zero() {
		set(0.0f, 0.0f);
		return this;
	}
	public float[] toFloat3(){
		return new float[]{x,y};
	}
	public float[] toFloat4(){
		return new float[]{x,y,1};
	}
	public float[] toFloat4(float w){
		return new float[]{x,y,w};
	}
	public String toString(){
		return String.format("Vector2(%f,%f)", x, y);
	}
}
