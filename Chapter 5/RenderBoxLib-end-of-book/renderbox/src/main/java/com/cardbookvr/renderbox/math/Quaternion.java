package com.cardbookvr.renderbox.math;

/**
 * Created by mtsch on 11/16/2015.
 * Somewhat sourced from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Quaternion.java
 */
public class Quaternion {
	private static final String TAG = "RenderBox.Quaternion";
	public float x, y, z, w;
	public Quaternion(){
		w = 1;
	}
	public Quaternion(Quaternion quat){
		x = quat.x;
		y = quat.y;
		z = quat.z;
		w = quat.w;
	}
	public Quaternion setEulerAngles (float pitch, float yaw, float roll) {
		return setEulerAnglesRad(
				pitch * MathUtils.degreesToRadians,
				yaw * MathUtils.degreesToRadians,
				roll * MathUtils.degreesToRadians);
	}
	public Quaternion setEulerAnglesRad (float pitch, float yaw, float roll) {
		final float hr = roll * 0.5f;
		final float shr = (float)Math.sin(hr);
		final float chr = (float)Math.cos(hr);
		final float hp = pitch * 0.5f;
		final float shp = (float)Math.sin(hp);
		final float chp = (float)Math.cos(hp);
		final float hy = yaw * 0.5f;
		final float shy = (float)Math.sin(hy);
		final float chy = (float)Math.cos(hy);
		final float chy_shp = chy * shp;
		final float shy_chp = shy * chp;
		final float chy_chp = chy * chp;
		final float shy_shp = shy * shp;

		x = (chy_shp * chr) + (shy_chp * shr); // cos(yaw/2) * sin(pitch/2) * cos(roll/2) + sin(yaw/2) * cos(pitch/2) * sin(roll/2)
		y = (shy_chp * chr) - (chy_shp * shr); // sin(yaw/2) * cos(pitch/2) * cos(roll/2) - cos(yaw/2) * sin(pitch/2) * sin(roll/2)
		z = (chy_chp * shr) - (shy_shp * chr); // cos(yaw/2) * cos(pitch/2) * sin(roll/2) - sin(yaw/2) * sin(pitch/2) * cos(roll/2)
		w = (chy_chp * chr) + (shy_shp * shr); // cos(yaw/2) * cos(pitch/2) * cos(roll/2) + sin(yaw/2) * sin(pitch/2) * sin(roll/2)

		return this;
	}
	public Quaternion multiply(final Quaternion other){
		final float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
		final float newY = this.w * other.y + this.y * other.w + this.z * other.x - this.x * other.z;
		final float newZ = this.w * other.z + this.z * other.w + this.x * other.y - this.y * other.x;
		final float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		this.w = newW;
		return this;
	}
	public Quaternion conjugate () {
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	public Matrix4 toMatrix4(){
		Matrix4 result = new Matrix4();
		final float[] matrix = result.val;
		final float xx = x * x;
		final float xy = x * y;
		final float xz = x * z;
		final float xw = x * w;
		final float yy = y * y;
		final float yz = y * z;
		final float yw = y * w;
		final float zz = z * z;
		final float zw = z * w;
		// Set matrix from quaternion
		matrix[Matrix4.M00] = 1 - 2 * (yy + zz);
		matrix[Matrix4.M01] = 2 * (xy - zw);
		matrix[Matrix4.M02] = 2 * (xz + yw);
		matrix[Matrix4.M03] = 0;
		matrix[Matrix4.M10] = 2 * (xy + zw);
		matrix[Matrix4.M11] = 1 - 2 * (xx + zz);
		matrix[Matrix4.M12] = 2 * (yz - xw);
		matrix[Matrix4.M13] = 0;
		matrix[Matrix4.M20] = 2 * (xz - yw);
		matrix[Matrix4.M21] = 2 * (yz + xw);
		matrix[Matrix4.M22] = 1 - 2 * (xx + yy);
		matrix[Matrix4.M23] = 0;
		matrix[Matrix4.M30] = 0;
		matrix[Matrix4.M31] = 0;
		matrix[Matrix4.M32] = 0;
		matrix[Matrix4.M33] = 1;
		return result;
	}
	public String toString(){
		return String.format("Quaternion(%.5f, %.5f, %.5f, %.5f)", x, y, z, w);
	}
}