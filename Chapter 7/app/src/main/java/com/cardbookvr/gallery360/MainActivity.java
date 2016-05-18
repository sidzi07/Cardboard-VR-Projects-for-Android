package com.cardbookvr.gallery360;

import android.content.Context;
import android.database.Cursor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;

import com.cardbookvr.gallery360.RenderBoxExt.components.Plane;
import com.cardbookvr.gallery360.RenderBoxExt.components.Triangle;
import com.cardbookvr.gallery360.RenderBoxExt.materials.BorderMaterial;
import com.cardbookvr.renderbox.IRenderBox;
import com.cardbookvr.renderbox.RenderBox;
import com.cardbookvr.renderbox.Time;
import com.cardbookvr.renderbox.Transform;
import com.cardbookvr.renderbox.components.Camera;
import com.cardbookvr.renderbox.components.Sphere;
import com.cardbookvr.renderbox.materials.UnlitTexMaterial;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends CardboardActivity implements IRenderBox {
    private static final String TAG = "Gallery360";

    CardboardView cardboardView;

    final int DEFAULT_BACKGROUND = R.drawable.bg;

    Sphere photosphere;
    Plane screen;
    int bgTextureHandle;

    final List<Image> images = new ArrayList<>();
    final String imagesPath = "/storage/emulated/0/DCIM/Camera";

    final int GRID_X = 5;
    final int GRID_Y = 3;

    final List<Thumbnail> thumbnails = new ArrayList<>();

    final float[] selectedColor = new float[]{0, 0.5f, 0.5f, 1};
    final float[] invalidColor = new float[]{0.5f, 0, 0, 1};
    final float[] normalColor = new float[]{0, 0, 0, 1};
    Thumbnail selectedThumbnail = null;
    private Vibrator vibrator;

    Triangle up, down;
    BorderMaterial upMaterial, downMaterial;
    boolean upSelected, downSelected;
    static int thumbOffset = 0;

    public static boolean cancelUpdate = false;
    static boolean gridUpdateLock = false;

    static boolean setupComplete = false;

    boolean interfaceVisible = true;
    OrientationEventListener orientationEventListener;
    int orientThreshold = 10;
    boolean orientFlip = false;
    long tiltTime;

    int tiltDamper = 250;

    final float selectedScale = 1.25f;
    final float normalScale = 0.85f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cancelUpdate = false;
        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(new RenderBox(this, this));
        setCardboardView(cardboardView);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void setup() {
        BorderMaterial.destroy();
        setupMaxTextureSize();
        setupBackground();
        setupScreen();
        loadImageList(imagesPath);
        setupThumbnailGrid();
        setupScrollButtons();
        Uri intentUri = getIntent().getData();
        if (intentUri != null) {
            showUriImage(intentUri);
        }
        updateThumbnails();
        setupOrientationListener();
    }

    void setupBackground() {
        photosphere = new Sphere(DEFAULT_BACKGROUND, false);
        new Transform()
                .setLocalScale(Camera.Z_FAR * 0.99f, -Camera.Z_FAR * 0.99f, Camera.Z_FAR * 0.99f)
                .addComponent(photosphere);
        UnlitTexMaterial mat = (UnlitTexMaterial) photosphere.getMaterial();
        bgTextureHandle = mat.getTexture();
    }

    void setupScreen() {
        Transform screenRoot = new Transform()
                .setLocalScale(4, 4, 1)
                .setLocalRotation(0, -90, 0)
                .setLocalPosition(-5, 0, 0);

        screen = new Plane();
        BorderMaterial screenMaterial = new BorderMaterial();
        screen.setupBorderMaterial(screenMaterial);

        new Transform()
                .setParent(screenRoot, false)
                .addComponent(screen);
    }

    void setupThumbnailGrid() {
        int count = 0;
        for (int i = 0; i < GRID_Y; i++) {
            for (int j = 0; j < GRID_X; j++) {
                if (count < images.size()) {
                    Thumbnail thumb = new Thumbnail(cardboardView);
                    thumbnails.add(thumb);

                    Transform image = new Transform();
                    image.setLocalPosition(-4 + j * 2.1f, 3 - i * 3, -5);
                    Plane imgPlane = new Plane();
                    thumb.plane = imgPlane;
                    BorderMaterial material = new BorderMaterial();
                    imgPlane.setupBorderMaterial(material);
                    image.addComponent(imgPlane);

                    Transform sphere = new Transform();
                    sphere.setLocalPosition(-4 + j * 2.1f, 3 - i * 3, -5);
                    sphere.setLocalRotation(180, 0, 0);
                    sphere.setLocalScale(normalScale, normalScale, normalScale);
                    //This is an alternative to calling setBuffers  yourself,
                    // but I don''t know if I like setting it up with some arbitrary image
                    Sphere imgSphere = new Sphere(R.drawable.bg, false);
                    thumb.sphere = imgSphere;
                    sphere.addComponent(imgSphere);

                }
                count++;
            }
        }
    }

    void updateThumbnails() {
        gridUpdateLock = true;
        new Thread() {
            @Override
            public void run() {

                int count = thumbOffset;
                for (Thumbnail thumb : thumbnails) {
                    if (count < images.size()) {
                        thumb.setImage(images.get(count));
                        thumb.setVisible(true);
                    } else {
                        thumb.setVisible(false);
                    }
                    count++;
                }
                cancelUpdate = false;
                gridUpdateLock = false;
                setupComplete = true;
            }
        }.start();
     }

    void selectObject() {
        float deltaTime = Time.getDeltaTime();
        selectedThumbnail = null;
        for (Thumbnail thumb : thumbnails) {
            if (thumb.image == null)
                return;

            if(thumb.image.isPhotosphere) {
                Sphere sphere = thumb.sphere;
                if (sphere.isLooking) {
                    selectedThumbnail = thumb;
                    if (!gridUpdateLock)
                        sphere.transform.setLocalScale(selectedScale, selectedScale, selectedScale);
                } else {
                    sphere.transform.setLocalScale(normalScale, normalScale, normalScale);
                }
                sphere.transform.rotate(0, 10 * deltaTime, 0);

            } else {

                Plane plane = thumb.plane;
                BorderMaterial material = (BorderMaterial) plane.getMaterial();
                if (plane.isLooking) {
                    selectedThumbnail = thumb;
                    if (gridUpdateLock)
                        material.borderColor = invalidColor;
                    else
                        material.borderColor = selectedColor;
                } else {
                    material.borderColor = normalColor;
                }
            }
        }

        if (up.isLooking) {
            upSelected = true;
            upMaterial.borderColor = selectedColor;
        } else {
            upSelected = false;
            upMaterial.borderColor = normalColor;
        }

        if (down.isLooking) {
            downSelected = true;
            downMaterial.borderColor = selectedColor;
        } else {
            downSelected = false;
            downMaterial.borderColor = normalColor;
        }
    }

    void setupScrollButtons() {
        up = new Triangle();
        upMaterial = new BorderMaterial();
        up.setupBorderMaterial(upMaterial);
        new Transform()
                .setLocalPosition(0, 6, -5)
                .addComponent(up);

        down = new Triangle();
        downMaterial = new BorderMaterial();
        down.setupBorderMaterial(downMaterial);
        new Transform()
                .setLocalPosition(0, -6, -5)
                .setLocalRotation(0, 0, 180)
                .addComponent(down);
    }

    void setupOrientationListener() {
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (gridUpdateLock || !setupComplete)
                    return;
                if (System.currentTimeMillis() - tiltTime > tiltDamper) {
                    if (Math.abs(orientation) < orientThreshold ||
                            Math.abs(orientation - 180) < orientThreshold) {
                        //"close enough" to portrait mode
                        if (!orientFlip) {
                            Log.d(TAG, "tilt up! " + orientation);
                            vibrator.vibrate(25);
                            toggleGridMenu();
                        }
                        orientFlip = true;
                    }
                    if (Math.abs(orientation - 90) < orientThreshold ||
                            Math.abs(orientation - 270) < orientThreshold) {
                        //"close enough" to landscape mode
                        orientFlip = false;
                    }
                    tiltTime = System.currentTimeMillis();
                }
            }
        };
        if (orientationEventListener.canDetectOrientation())
            orientationEventListener.enable();
    }


    @Override
    protected void onStart() {
        super.onStart();
        cancelUpdate = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancelUpdate = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelUpdate = true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        orientationEventListener.disable();
    }


    @Override
    public void preDraw() {

    }

    @Override
    public void postDraw() {
        selectObject();
    }

    @Override
    public void onCardboardTrigger() {
        if (gridUpdateLock) {
            vibrator.vibrate(new long[]{0, 50, 30, 50}, -1);
            return;
        }

        if (selectedThumbnail != null) {
            vibrator.vibrate(25);
            showImage(selectedThumbnail.image);
        }

        if (upSelected) {
            // scroll up
            thumbOffset -= GRID_X;
            if (thumbOffset < 0) {
                thumbOffset = images.size() - GRID_X;
            }
            vibrator.vibrate(25);
            updateThumbnails();
        }

        if (downSelected) {
            // scroll down
            if (thumbOffset < images.size()) {
                thumbOffset += GRID_X;
            } else {
                thumbOffset = 0;
            }
            vibrator.vibrate(25);
            updateThumbnails();
        }
    }

    int loadImageList(String path) {
        File f = new File(path);
        File[] file = f.listFiles();
        if (file == null)
            return 0;
        for (int j = 0; j < 2; j++) { // artificially add extra images to demonstrate scrolling
            for (int i = 0; i < file.length; i++) {
                if (Image.isValidImage(file[i].getName())) {
                    Log.d(TAG, file[i].getName());
                    Image img = new Image(path + "/" + file[i].getName());
                    images.add(img);
                }
            }
        }
        return file.length;
    }

    void showImage(final Image image) {
        new Thread() {
            @Override
            public void run() {

                UnlitTexMaterial bgMaterial = (UnlitTexMaterial) photosphere.getMaterial();
                image.loadFullTexture(cardboardView);
                if (image.isPhotosphere) {
                    bgMaterial.setTexture(image.textureHandle);
                    screen.enabled = false;
                } else {
                    bgMaterial.setTexture(bgTextureHandle);
                    screen.enabled = true;
                    image.show(cardboardView, screen);
                }

            }
        }.start();
    }

    void showUriImage(final Uri uri) {
        Log.d(TAG, "intent data " + uri.getPath());
        File file = new File(uri.getPath());

        if(file.exists()){
            Image img = new Image(uri.getPath());
            showImage(img);

        } else {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor == null)
                return;
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String yourRealPath = cursor.getString(columnIndex);
                Image img = new Image(yourRealPath);
                showImage(img);
            }
            // else report image not found error?
            cursor.close();
        }
    }

    void toggleGridMenu() {
        interfaceVisible = !interfaceVisible;
        if (up != null)
            up.enabled = !up.enabled;
        if (down != null)
            down.enabled = !down.enabled;
        int texCount = thumbOffset;
        for (Thumbnail thumb : thumbnails) {
            if (texCount < images.size() && thumb != null) {
                thumb.setVisible(interfaceVisible);
            }
            texCount++;
        }
    }


    static int MAX_TEXTURE_SIZE = 2048;

    void setupMaxTextureSize() {
        //get max texture size
        int[] maxTextureSize = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
        MAX_TEXTURE_SIZE = maxTextureSize[0];
        Log.i(TAG, "Max texture size = " + MAX_TEXTURE_SIZE);
    }

}
