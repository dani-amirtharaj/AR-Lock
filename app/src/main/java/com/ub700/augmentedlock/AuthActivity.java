package com.ub700.augmentedlock;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Material;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.FootprintSelectionVisualizer;


public class AuthActivity extends AppCompatActivity {

    private static final String TAG = AuthActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private Scene scene;
    private static int deviceHeight;
    private static int deviceWidth;
    private boolean firstSelectionDelay;
    private int hitCount;
    private Node selectedNode;
    private Node previousSelection;
    private ImageView buttonSelectorView;
    private Material buttonMaterial;
    private static int buttonColor;
    private ValueAnimator buttonAnimator;
    private ModelRenderable[] buttons;
    private boolean isKeypadRendered;
    private String key;
    private StringBuilder enteredKey;
    private static final int NUM_BUTTONS = 9;
    private static final int HIT_WAIT = 15;


    public AuthActivity() {
        hitCount = 0;
        isKeypadRendered = false;
        firstSelectionDelay = false;
        buttons = new ModelRenderable[9];
        enteredKey = new StringBuilder();
        buttonColor = android.graphics.Color.argb(255,89, 139, 214);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        setContentView(R.layout.activity_auth);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        buildButtonMaterial(buttonColor, 1, 1);

        Intent intent = getIntent();
        key = intent.getExtras().getString("Key");

        scene = arFragment.getArSceneView().getScene();
        buttonSelectorView = findViewById(R.id.button_selector);
        setDisplayHeightWidth();

        /* Plane listener to allow user to place keypad on surface of their choice.*/
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (!buildButtons()) {
                        return;
                    }
                    if (!isKeypadRendered) {
                        Anchor anchor = hitResult.createAnchor();
                        AnchorNode anchorNode = new AnchorNode(anchor);
                        anchorNode.setParent(arFragment.getArSceneView().getScene());

                        buildKeypad(anchorNode);
                        this.runOnUiThread(() -> {
                            buttonSelectorView.setVisibility(View.VISIBLE);
                        });

                        /* To ensure plane detection is stopped when buttons are rendered once. */
                        isKeypadRendered = true;
                        disablePlaneDetection(arFragment);
                    }
                });

        scene.addOnUpdateListener(frameTime -> {
            /* Activated only after buttons are rendered on screen. */
            if (isKeypadRendered) {
                /* To add delay the before the first selection. */
                if (firstSelectionDelay) {
                    hitCount++;
                    if (hitCount > HIT_WAIT+5) {
                        hitCount = 0;
                        firstSelectionDelay = false;
                    }
                } else {
                    /* HitTestResult holds any node in the scene hit by a ray. */
                    HitTestResult hitTestResult = scene.hitTest(scene.getCamera().screenPointToRay(deviceWidth / 2, deviceHeight / 2));
                    if (hitTestResult != null && hitTestResult.getNode() != null) {
                        /* To ensure same node is hit multiple times to get selected, to avoid over sensitivity in selection. */
                        if (hitCount == 0) {
                            selectedNode = hitTestResult.getNode();
                            hitCount = 1;
                        } else {
                            if (selectedNode.equals(hitTestResult.getNode())) {
                                hitCount++;
                            } else {
                                selectedNode = hitTestResult.getNode();
                                hitCount = 0;
                            }
                        }

                        if (hitCount > HIT_WAIT) {
                            if (buttonAnimator != null)  {
                                previousSelection.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR, new Color(buttonColor));
                            }
                            previousSelection = selectedNode;
                            buttonAnimation(selectedNode);
                            /* Get selection and compare to actual key. */
                            enteredKey.append(selectedNode.getName());
                            Log.e(TAG, enteredKey.toString());
                            if (enteredKey.toString().equals(key)) {
                                startNextActivity();
                            }
                            hitCount = 0;
                        }
                    }
                }
            }
        });
    }

    /* Building material to be applied to buttons. */
    private void buildButtonMaterial(int color, float metallic, float reflectance) {
        MaterialFactory.makeOpaqueWithColor(this, new Color(color))
                .thenAccept(
                        material -> {
                            material.setFloat(MaterialFactory.MATERIAL_METALLIC, metallic);
                            material.setFloat(MaterialFactory.MATERIAL_REFLECTANCE, reflectance);
                            buttonMaterial = material;
                        });
    }

    /* Build all buttons for keypad. */
    private boolean buildButtons() {
        if (buttonMaterial == null) {
            return false;
        }
        for (int i = 0; i < NUM_BUTTONS; i++) {
            buttons[i] = ShapeFactory.makeCylinder(0.07f, 0.05f,
                    new Vector3(0.0f, 0.15f, 0.0f),  buttonMaterial.makeCopy());
        }
        return true;
    }

    private void buildKeypad(AnchorNode anchorNode) {
        Vector3 localPosition = new Vector3();
        Node buttonNode;
        int index = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                localPosition.set(0.25f * j, 0.0f, 0.25f * i);
                buttonNode = new Node();
                buttonNode.setParent(anchorNode);
                buttonNode.setLocalPosition(localPosition);
                buttonNode.setRenderable(buttons[index]);
                buttonNode.setName(Integer.toString(index + 1));
                buttonNode.setOnTapListener(((HitTestResult hitTestResult, MotionEvent motionEve) -> {
                    if (hitTestResult != null || hitTestResult.getNode() != null)
                        hitTestResult.getNode().getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR,
                                new Color(android.graphics.Color.RED));
                }));
                index++;
            }
        }
    }

    private void disablePlaneDetection(ArFragment arFragment) {
        arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getPlaneDiscoveryController().setInstructionView(null);
        arFragment.getTransformationSystem().setSelectionVisualizer(new FootprintSelectionVisualizer());
    }

    /* Get screen resolution of device. */
    private void setDisplayHeightWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
    }

    /* Animation to show button selection. */
    private void buttonAnimation(Node selectedNode) {
        buttonAnimator = new ValueAnimator();
        buttonAnimator.setIntValues(android.graphics.Color.GREEN, buttonColor);
        buttonAnimator.setEvaluator(new ArgbEvaluator());
        buttonAnimator.setInterpolator(new LinearInterpolator());
        buttonAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
                selectedNode.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR, new Color(buttonColor));
            }});
        buttonAnimator.addUpdateListener((ValueAnimator valueButtonAnimatorator) -> {
            selectedNode.getRenderable().getMaterial().setFloat3(MaterialFactory.MATERIAL_COLOR,
                    new Color((int) valueButtonAnimatorator.getAnimatedValue()));
        });
        buttonAnimator.setDuration(150);
        buttonAnimator.start();
    }

    /* Start next activity. */
    private void startNextActivity() {
        Intent intent2 = new Intent(getApplicationContext(), WelcomeActivity.class);
        startActivity(intent2);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    /*
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

}

