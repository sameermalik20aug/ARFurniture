package com.example.vgvee.arfurniture;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

//    private PointerDrawable pointer = new PointerDrawable();
//    private boolean isTracking;
//    private boolean isHitting;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment arFragment;
    private ModelRenderable andyRenderable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        setContentView(R.layout.activity_main);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        initializeGallery();
    }

    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < 24) {
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

    private void initializeGallery() {
        LinearLayout gallery = findViewById(R.id.gallery_layout);

        ImageView2 armchair = new ImageView2(this);
        armchair.setImageResource(R.drawable.armchair);
        armchair.setContentDescription("armchair");
        armchair.setOnClickListener(view ->{addObject(Uri.parse("Armchair_01.sfb"));});
        gallery.addView(armchair);

        ImageView2 bed = new ImageView2(this);
        bed.setImageResource(R.drawable.bed);
        bed.setContentDescription("bed");
        bed.setOnClickListener(view ->{addObject(Uri.parse("Bed_01.sfb"));});
        gallery.addView(bed);

        ImageView2 bedroom = new ImageView2(this);
        bedroom.setImageResource(R.drawable.bedroom);
        bedroom.setContentDescription("bedroom");
        bedroom.setOnClickListener(view ->{addObject(Uri.parse("Bedroom.sfb"));});
        gallery.addView(bedroom);

        ImageView2 bookcase = new ImageView2(this);
        bookcase.setImageResource(R.drawable.bookcase);
        bookcase.setContentDescription("bookcase");
        bookcase.setOnClickListener(view ->{addObject(Uri.parse("bookcase.sfb"));});
        gallery.addView(bookcase);

        ImageView2 breakfast = new ImageView2(this);
        breakfast.setImageResource(R.drawable.breakfastbar);
        breakfast.setContentDescription("breakfastbar");
        breakfast.setOnClickListener(view ->{addObject(Uri.parse("BreakFastBar.sfb"));});
        gallery.addView(breakfast);

        ImageView2 cornertable = new ImageView2(this);
        cornertable.setImageResource(R.drawable.cornertable);
        cornertable.setContentDescription("andy");
        cornertable.setOnClickListener(view ->{addObject(Uri.parse("CornerTable.sfb"));});
        gallery.addView(cornertable);

        ImageView2 couchred = new ImageView2(this);
        couchred.setImageResource(R.drawable.couchred);
        couchred.setContentDescription("andy");
        couchred.setOnClickListener(view ->{addObject(Uri.parse("CouchRed.sfb"));});
        gallery.addView(couchred);

        ImageView2 couchwide = new ImageView2(this);
        couchwide.setImageResource(R.drawable.couchwide);
        couchwide.setContentDescription("couchwide");
        couchwide.setOnClickListener(view ->{addObject(Uri.parse("CouchWide.sfb"));});
        gallery.addView(couchwide);

        ImageView2 credenza = new ImageView2(this);
        credenza.setImageResource(R.drawable.credenza);
        credenza.setContentDescription("credenza");
        credenza.setOnClickListener(view ->{addObject(Uri.parse("Credenza.sfb"));});
        gallery.addView(credenza);

        ImageView2 tabletennis = new ImageView2(this);
        tabletennis.setImageResource(R.drawable.tabletennis);
        tabletennis.setContentDescription("TableTennis Table");
        tabletennis.setOnClickListener(view ->{addObject(Uri.parse("TTtable.sfb"));});
        gallery.addView(tabletennis);
    }

//    private void addObject2(Uri model) {
//        Frame frame = arFragment.getArSceneView().getArFrame();
//        android.graphics.Point pt = getScreenCenter();
//        List<HitResult> hits;
//        if (frame != null) {
//            hits = frame.hitTest(pt.x, pt.y);
//            for (HitResult hit : hits) {
//                Trackable trackable = hit.getTrackable();
//                if (trackable instanceof Plane &&
//                        ((Plane) trackable).isPoseInPolygon(hit.getHitPose())) {
//                    placeObject(arFragment, hit.createAnchor(), model);
//                    break;
//
//                }
//            }
//        }
//    }

    private void addObject(Uri model){
        ModelRenderable.builder()
                .setSource(this, model)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (andyRenderable == null) {
                        Toast.makeText(this, "Select a model to load", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Create the Anchor.
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    andy.setRenderable(andyRenderable);
                    andy.select();
                });
    }

//    private void placeObject(ArFragment fragment, Anchor anchor, Uri model) {
//        CompletableFuture<Void> renderableFuture =
//                ModelRenderable.builder()
//                        .setSource(fragment.getContext(), model)
//                        .build()
//                        .thenAccept(renderable -> addNodeToScene(fragment, anchor, renderable))
//                        .exceptionally((throwable -> {
//                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                            builder.setMessage(throwable.getMessage())
//                                    .setTitle("Codelab error!");
//                            AlertDialog dialog = builder.create();
//                            dialog.show();
//                            return null;
//                        }));
//    }

//    private void addNodeToScene(ArFragment fragment, Anchor anchor, Renderable renderable) {
//        AnchorNode anchorNode = new AnchorNode(anchor);
//        TransformableNode node = new TransformableNode(fragment.getTransformationSystem());
//        node.setRenderable(renderable);
//        node.setParent(anchorNode);
//        fragment.getArSceneView().getScene().addChild(anchorNode);
//        node.select();
//    }
}
