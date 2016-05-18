package com.cardbookvr.gallery360;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.cardbookvr.gallery360.RenderBoxExt.components.Plane;
import com.cardbookvr.gallery360.RenderBoxExt.materials.BorderMaterial;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.math.Quaternion;
import com.google.vrtoolkit.cardboard.CardboardView;

import java.io.IOException;

/**
 * Created by Schoen and Jonathan on 4/21/2016.
 */
public class Image {
    final static String TAG = "image";

    String path;
    int textureHandle;
    Quaternion rotation;
    int height, width;
    public boolean isPhotosphere;
    public static boolean loadLock = false;

    public Image(String path) {
        this.path = path;
        isPhotosphere = path.toLowerCase().contains("pano");
    }

    public static boolean isValidImage(String path) {
        String extension = getExtension(path);
        if (extension == null)
            return false;
        switch (extension) {
            case "jpg":
                return true;
            case "jpeg":
                return true;
            case "png":
                return true;
        }
        return false;
    }

    static String getExtension(String path) {
        String[] split = path.split("\\.");
        if (split == null || split.length < 2)
            return null;
        return split[split.length - 1].toLowerCase();
    }

    public void loadTexture(CardboardView cardboardView, int sampleSize) {
        if (textureHandle != 0)
            return;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        final Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        if (bitmap == null) {
            throw new RuntimeException("Error loading bitmap.");
        }
        width = options.outWidth;
        height = options.outHeight;

        loadLock = true;
        cardboardView.queueEvent(new Runnable() {
            @Override
            public void run() {
                if (MainActivity.cancelUpdate)
                    return;
                textureHandle = bitmapToTexture(bitmap);
                bitmap.recycle();
                loadLock = false;
            }
        });

        while (loadLock) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void loadFullTexture(CardboardView cardboardView) {
        // search for best size
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        do {
            options.inSampleSize = sampleSize;
            BitmapFactory.decodeFile(path, options);
            sampleSize *= 2;
        } while (options.outWidth > MainActivity.MAX_TEXTURE_SIZE
                || options.outHeight > MainActivity.MAX_TEXTURE_SIZE);
        sampleSize /= 2;
        loadTexture(cardboardView, sampleSize);
    }

    public static int bitmapToTexture(Bitmap bitmap) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        RenderBox.checkGLError("Bitmap GenTexture");

        if (textureHandle[0] != 0) {
            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public void show(CardboardView cardboardView, Plane screen) {
        loadFullTexture(cardboardView);
        BorderMaterial material = (BorderMaterial) screen.getMaterial();
        material.setTexture(textureHandle);
        calcRotation(screen);
        calcScale(screen);
    }

    public void showThumbnail(CardboardView cardboardView, Plane thumb) {
        loadTexture(cardboardView, 4);
        BorderMaterial material = (BorderMaterial) thumb.getMaterial();
        material.setTexture(textureHandle);
        calcRotation(thumb);
        calcScale(thumb);
    }

    void calcRotation(Plane screen) {
        rotation = new Quaternion();

        // use Exif tags to determine orientation, only available in jpg (and jpeg)
        String ext = getExtension(path);
        if (ext.equals("jpg") || ext.equals("jpeg")) {
            try {
                ExifInterface exif = new ExifInterface(path);
                switch (exif.getAttribute(ExifInterface.TAG_ORIENTATION)) {
                    // Correct orientation, but flipped on the horizontal axis
                    case "2":
                        rotation = new Quaternion().setEulerAngles(180, 0, 0);
                        break;
                    // Upside-down
                    case "3":
                        rotation = new Quaternion().setEulerAngles(0, 0, 180);
                        break;
                    // Upside-Down & Flipped along horizontal axis
                    case "4":
                        rotation = new Quaternion().setEulerAngles(180, 0, 180);
                        break;
                    // Turned 90 deg to the left and flipped
                    case "5":
                        rotation = new Quaternion().setEulerAngles(0, 180, 90);
                        break;
                    // Turned 90 deg to the left
                    case "6":
                        rotation = new Quaternion().setEulerAngles(0, 0, -90);
                        break;
                    // Turned 90 deg to the right and flipped
                    case "7":
                        rotation = new Quaternion().setEulerAngles(0, 180, 90);
                        break;
                    // Turned 90 deg to the right
                    case "8":
                        rotation = new Quaternion().setEulerAngles(0, 0, 90);
                        break;
                    //Correct orientation--do nothing
                    default:
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        screen.transform.setLocalRotation(rotation);
    }

    void calcScale(Plane screen) {
        if (width > 0 && width > height) {
            screen.transform.setLocalScale(1, (float) height / width, 1);
        } else if (height > 0) {
            screen.transform.setLocalScale((float) width / height, 1, 1);
        }
    }

}
