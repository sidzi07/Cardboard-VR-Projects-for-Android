package com.cardbookvr.renderbox.components;

import com.cardbookvr.renderbox.materials.DayNightMaterial;
import com.cardbookvr.renderbox.materials.DiffuseLightingMaterial;
import com.cardbookvr.renderbox.materials.SolidColorLightingMaterial;
import com.cardbookvr.renderbox.materials.UnlitTexMaterial;
import com.cardbookvr.renderbox.math.MathUtils;
import com.cardbookvr.renderbox.math.Vector2;
import com.cardbookvr.renderbox.math.Vector3;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Schoen and Jonathan on 4/21/2016.
 */
public class Sphere extends RenderObject {
    private static final String TAG = "RenderBox.Sphere";

    public static FloatBuffer vertexBuffer;
    public static FloatBuffer normalBuffer;
    public static FloatBuffer texCoordBuffer;
    public static ShortBuffer indexBuffer;
    public static int numIndices;

    public Sphere() {
        super();
        allocateBuffers();
    }

    public Sphere(float[] color) {
        super();
        allocateBuffers();
        createSolidColorLightingMaterial(color);
    }

    public Sphere(int textureId) {
        super();
        allocateBuffers();
        createDiffuseMaterial(textureId);
    }

    public Sphere(int textureId, int nightTextureId){
        super();
        allocateBuffers();
        createDayNightMaterial(textureId, nightTextureId);
    }

    public Sphere(int textureId, boolean lighting) {
        super();
        allocateBuffers();
        if (lighting) {
            createDiffuseMaterial(textureId);
        } else {
            createUnlitTexMaterial(textureId);
        }
    }


    public Sphere createSolidColorLightingMaterial(float[] color){
        SolidColorLightingMaterial mat = new SolidColorLightingMaterial(color);
        mat.setBuffers(vertexBuffer, normalBuffer, indexBuffer, numIndices);
        material = mat;
        return this;
    }

    public Sphere createDiffuseMaterial(int textureId){
        DiffuseLightingMaterial mat = new DiffuseLightingMaterial(textureId);
        mat.setBuffers(vertexBuffer, normalBuffer, texCoordBuffer, indexBuffer, numIndices);
        material = mat;
        return this;
    }

    public Sphere createDayNightMaterial(int textureId, int nightTextureId) {
        DayNightMaterial mat = new DayNightMaterial(textureId, nightTextureId);
        mat.setBuffers(vertexBuffer, normalBuffer, texCoordBuffer, indexBuffer, numIndices);
        material = mat;
        return this;
    }

    public Sphere createUnlitTexMaterial(int textureId) {
        UnlitTexMaterial mat = new UnlitTexMaterial(textureId);
        mat.setBuffers(vertexBuffer, texCoordBuffer, indexBuffer, numIndices);
        material = mat;
        return this;
    }


    public static void allocateBuffers() {
        //Already allocated?
        if (vertexBuffer != null) return;
        //Generate a sphere model
        float radius = 1f;
        // Longitude |||
        int nbLong = 24;
        // Latitude ---
        int nbLat = 16;

        Vector3[] vertices = new Vector3[(nbLong + 1) * nbLat + nbLong * 2];
        float _pi = MathUtils.PI;
        float _2pi = MathUtils.PI2;

        //Top and bottom vertices are duplicated
        for (int i = 0; i < nbLong; i++) {
            vertices[i] = new Vector3(Vector3.up).multiply(radius);
            vertices[vertices.length - i - 1] = new Vector3(Vector3.up).multiply(-radius);
        }

        for (int lat = 0; lat < nbLat; lat++) {
            float a1 = _pi * (float) (lat + 1) / (nbLat + 1);
            float sin1 = (float) Math.sin(a1);
            float cos1 = (float) Math.cos(a1);

            for (int lon = 0; lon <= nbLong; lon++) {
                float a2 = _2pi * (float) (lon == nbLong ? 0 : lon) / nbLong;
                float sin2 = (float) Math.sin(a2);
                float cos2 = (float) Math.cos(a2);

                vertices[lon + lat * (nbLong + 1) + nbLong] =
                        new Vector3(sin1 * cos2, cos1, sin1 * sin2).multiply(radius);
            }
        }

        Vector3[] normals = new Vector3[vertices.length];
        for (int n = 0; n < vertices.length; n++) {
            normals[n] = new Vector3(vertices[n]).normalize();
        }

        Vector2[] uvs = new Vector2[vertices.length];
        float uvStart = 1.0f / (nbLong * 2);
        float uvStride = 1.0f / nbLong;

        for (int i = 0; i < nbLong; i++) {
            uvs[i] = new Vector2(uvStart + i * uvStride, 1f);
            uvs[uvs.length - i - 1] = new Vector2(1 - (uvStart + i * uvStride), 0f);
        }

        for (int lat = 0; lat < nbLat; lat++) {
            for (int lon = 0; lon <= nbLong; lon++) {
                uvs[lon + lat * (nbLong + 1) + nbLong] =
                        new Vector2((float) lon / nbLong, 1f - (float) (lat + 1) / (nbLat + 1));
            }
        }

        int nbFaces = (nbLong + 1) * nbLat + 2;
        int nbTriangles = nbFaces * 2;
        int nbIndexes = nbTriangles * 3;
        numIndices = nbIndexes;
        short[] triangles = new short[nbIndexes];

        //Top Cap
        int i = 0;
        for (short lon = 0; lon < nbLong; lon++) {
            triangles[i++] = lon;
            triangles[i++] = (short) (nbLong + lon + 1);
            triangles[i++] = (short) (nbLong + lon);
        }

        //Middle
        for (short lat = 0; lat < nbLat - 1; lat++) {
            for (short lon = 0; lon < nbLong; lon++) {
                short current = (short) (lon + lat * (nbLong + 1) + nbLong);
                short next = (short) (current + nbLong + 1);

                triangles[i++] = current;
                triangles[i++] = (short) (current + 1);
                triangles[i++] = (short) (next + 1);

                triangles[i++] = current;
                triangles[i++] = (short) (next + 1);
                triangles[i++] = next;
            }
        }

        //Bottom Cap
        for (short lon = 0; lon < nbLong; lon++) {
            triangles[i++] = (short) (vertices.length - lon - 1);
            triangles[i++] = (short) (vertices.length - nbLong - (lon + 1) - 1);
            triangles[i++] = (short) (vertices.length - nbLong - (lon) - 1);
        }

        //convert Vector3[] to float[]
        float[] vertexArray = new float[vertices.length * 3];
        for(i = 0; i < vertices.length; i++) {
            int step = i * 3;
            vertexArray[step] = vertices[i].x;
            vertexArray[step + 1] = vertices[i].y;
            vertexArray[step + 2] = vertices[i].z;
        }

        float[] normalArray = new float[normals.length * 3];
        for(i = 0; i < normals.length; i++) {
            int step = i * 3;
            normalArray[step] = normals[i].x;
            normalArray[step + 1] = normals[i].y;
            normalArray[step + 2] = normals[i].z;
        }

        float[] texCoordArray = new float[uvs.length * 2];
        for(i = 0; i < uvs.length; i++) {
            int step = i * 2;
            texCoordArray[step] = uvs[i].x;
            texCoordArray[step + 1] = uvs[i].y;
        }

        vertexBuffer = allocateFloatBuffer(vertexArray);
        normalBuffer = allocateFloatBuffer(normalArray);
        texCoordBuffer = allocateFloatBuffer(texCoordArray);
        indexBuffer = allocateShortBuffer(triangles);
    }

}
