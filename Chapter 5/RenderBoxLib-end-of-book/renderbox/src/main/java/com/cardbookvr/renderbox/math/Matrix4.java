package com.cardbookvr.renderbox.math;

import android.opengl.Matrix;

/**
 * Created by mtsch on 11/16/2015.
 * Mostly sourced from https://github.com/libgdx/libgdx/blob/master/gdx/src/com/badlogic/gdx/math/Matrix4.java
 */
public class Matrix4{
	private static final String TAG = "RenderBox.Matrix4";
	public float[] val = new float[16];
	public Matrix4() {
		Matrix.setIdentityM(val, 0);
	}
	public Matrix4(float[] matrix){
		val[0] = matrix[0];
		val[1] = matrix[1];
		val[2] = matrix[2];
		val[3] = matrix[3];
		val[4] = matrix[4];
		val[5] = matrix[5];
		val[6] = matrix[6];
		val[7] = matrix[7];
		val[8] = matrix[8];
		val[9] = matrix[9];
		val[10] = matrix[10];
		val[11] = matrix[11];
		val[12] = matrix[12];
		val[13] = matrix[13];
		val[14] = matrix[14];
		val[15] = matrix[15];
	}
	public Matrix4(Matrix4 matrix) {
		val[0] = matrix.val[0];
		val[1] = matrix.val[1];
		val[2] = matrix.val[2];
		val[3] = matrix.val[3];
		val[4] = matrix.val[4];
		val[5] = matrix.val[5];
		val[6] = matrix.val[6];
		val[7] = matrix.val[7];
		val[8] = matrix.val[8];
		val[9] = matrix.val[9];
		val[10] = matrix.val[10];
		val[11] = matrix.val[11];
		val[12] = matrix.val[12];
		val[13] = matrix.val[13];
		val[14] = matrix.val[14];
		val[15] = matrix.val[15];
	}
	public Matrix4 toIdentity(){
		Matrix.setIdentityM(val, 0);
		return this;
	}
	//public static Matrix4 TRS(Vector3 position, Quaternion rotation, Vector3 scale) {
	public static Matrix4 TRS(Vector3 position, Quaternion rotation, Vector3 scale) {
		Matrix4 result = new Matrix4();
		result.translate(position);
		result.setRotate(rotation);
		result.scale(scale);
		return result;
	}
	public static Matrix4 TR(Vector3 position, Quaternion rotation) {
		Matrix4 result = new Matrix4();
		result.translate(position);
		result.setRotate(rotation);
		return result;
	}
	public Matrix4 translate(Vector3 position){
		Matrix.translateM(val, 0, position.x, position.y, position.z);
		return this;
	}

	/**
	 * Renamed to setRotate, since it won't actually take initial rotation into account
	 * To do this correctly, we would want a 3x3 matrix class, multiply the quaternion matrix by
	 * the 3x3 component of this matrix, and set it back.
	 * @param rotation the rotation quaternion
	 * @return this matrix for chaining
	 */
	public Matrix4 setRotate(Quaternion rotation){;
		Matrix4 rotMat = rotation.toMatrix4();
		//Set the 3x3 component of the matrix equal to the quaternion matrix
		val[0] = rotMat.val[0];
		val[1] = rotMat.val[1];
		val[2] = rotMat.val[2];
		val[4] = rotMat.val[4];
		val[5] = rotMat.val[5];
		val[6] = rotMat.val[6];
		val[8] = rotMat.val[8];
		val[9] = rotMat.val[9];
		val[10] = rotMat.val[10];
		return this;
	}

	public Matrix4 scale(Vector3 scale){
		Matrix.scaleM(val, 0, scale.x, scale.y, scale.z);
		return this;
	}
	public Vector3 multiplyPoint3x4(Vector3 v){
		float[] vec = v.toFloat4();
		Matrix.multiplyMV(vec, 0, val, 0, vec, 0);
		return new Vector3(vec);
	}
	public Matrix4 multiply(Matrix4 matrix){
		return multiply(matrix.val);
	}
	public Matrix4 multiply(float[] matrix){
		Matrix.multiplyMM(val, 0, val, 0, matrix, 0);
		return this;
	}
	public String toString(){
		String out = "MATRIX =================\n";
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++){
				out += String.format("%.5f, ", val[i * 4 + j]);
			}
			out += "\n";
		}
		return out;
	}


	/** XX: Typically the unrotated X component for scaling, also the cosine of the angle when rotated on the Y and/or Z axis. On
	 * Vector3 multiplication this value is multiplied with the source X component and added to the target X component. */
	public static final int M00 = 0;
	/** XY: Typically the negative sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied
	 * with the source Y component and added to the target X component. */
	public static final int M01 = 4;
	/** XZ: Typically the sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied with the
	 * source Z component and added to the target X component. */
	public static final int M02 = 8;
	/** XW: Typically the translation of the X component. On Vector3 multiplication this value is added to the target X component. */
	public static final int M03 = 12;
	/** YX: Typically the sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied with the
	 * source X component and added to the target Y component. */
	public static final int M10 = 1;
	/** YY: Typically the unrotated Y component for scaling, also the cosine of the angle when rotated on the X and/or Z axis. On
	 * Vector3 multiplication this value is multiplied with the source Y component and added to the target Y component. */
	public static final int M11 = 5;
	/** YZ: Typically the negative sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied
	 * with the source Z component and added to the target Y component. */
	public static final int M12 = 9;
	/** YW: Typically the translation of the Y component. On Vector3 multiplication this value is added to the target Y component. */
	public static final int M13 = 13;
	/** ZX: Typically the negative sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied
	 * with the source X component and added to the target Z component. */
	public static final int M20 = 2;
	/** ZY: Typical the sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied with the
	 * source Y component and added to the target Z component. */
	public static final int M21 = 6;
	/** ZZ: Typically the unrotated Z component for scaling, also the cosine of the angle when rotated on the X and/or Y axis. On
	 * Vector3 multiplication this value is multiplied with the source Z component and added to the target Z component. */
	public static final int M22 = 10;
	/** ZW: Typically the translation of the Z component. On Vector3 multiplication this value is added to the target Z component. */
	public static final int M23 = 14;
	/** WX: Typically the value zero. On Vector3 multiplication this value is ignored. */
	public static final int M30 = 3;
	/** WY: Typically the value zero. On Vector3 multiplication this value is ignored. */
	public static final int M31 = 7;
	/** WZ: Typically the value zero. On Vector3 multiplication this value is ignored. */
	public static final int M32 = 11;
	/** WW: Typically the value one. On Vector3 multiplication this value is ignored. */
	public static final int M33 = 15;
}
