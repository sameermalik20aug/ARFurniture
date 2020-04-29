package com.example.vgvee.arfurniture;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private ImageButton removeBtn,captureBtn;
    private Anchor anchor;
    private Anchor x;
    private TransformableNode y;

    ArrayList<Anchor> anchorList;
    ArrayList<TransformableNode> transformableNodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }

        anchorList = new ArrayList<>();
        transformableNodes = new ArrayList<>();

        setContentView(R.layout.activity_main);
        removeBtn = findViewById(R.id.removeBtn);
        captureBtn = findViewById(R.id.capBtn);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                anchor.detach();
//                if((anchorList.size())<=0){
//                    Toast.makeText(MainActivity.this, "No Anchors to Delete!", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                x = anchorList.get(anchorList.size()-1);
//                x.detach();
//                anchorList.remove(anchorList.size()-1);

//                ((AnchorNode)andy.getParent()).getAnchor().detach();
                if(transformableNodes.size()<=0){
                    Toast.makeText(MainActivity.this, "No Anchors to Delete!", Toast.LENGTH_SHORT).show();
                    return;
                }
                y = transformableNodes.get(transformableNodes.size()-1);
                ((AnchorNode)y.getParent()).getAnchor().detach();
                transformableNodes.remove(transformableNodes.size()-1);
            }
        });

        initializeGallery();
    }


    private String generateFilename() {
        String date =
                new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault()).format(new Date());
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + File.separator + "Sceneform/" + date + "_screenshot.jpg";
    }

    private void saveBitmapToDisk(Bitmap bitmap, String filename) throws IOException {

        File out = new File(filename);
        if (!out.getParentFile().exists()) {
            out.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(filename);
             ByteArrayOutputStream outputData = new ByteArrayOutputStream()) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputData);
            outputData.writeTo(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException ex) {
            throw new IOException("Failed to save bitmap to disk", ex);
        }
    }

    private void takePhoto() {
        final String filename = generateFilename();
        ArSceneView view = arFragment.getArSceneView();

        // Create a bitmap the size of the scene view.
        final Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);

        // Create a handler thread to offload the processing of the image.
        final HandlerThread handlerThread = new HandlerThread("PixelCopier");
        handlerThread.start();
        // Make the request to copy.
        PixelCopy.request(view, bitmap, (copyResult) -> {
            if (copyResult == PixelCopy.SUCCESS) {
                try {
                    saveBitmapToDisk(bitmap, filename);
                } catch (IOException e) {
                    Toast toast = Toast.makeText(MainActivity.this, e.toString(),
                            Toast.LENGTH_LONG);
                    toast.show();
                    return;
                }
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                        "Photo saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("Open in Photos", v -> {
                    File photoFile = new File(filename);

                    Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                            MainActivity.this.getPackageName() + ".ar.codelab.name.provider",
                            photoFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW, photoURI);
                    intent.setDataAndType(photoURI, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);

                });
                snackbar.show();
            } else {
                Toast toast = Toast.makeText(MainActivity.this,
                        "Failed to copyPixels: " + copyResult, Toast.LENGTH_LONG);
                toast.show();
            }
            handlerThread.quitSafely();
        }, new Handler(handlerThread.getLooper()));
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

//        ImageView2 armchair = new ImageView2(this);
//        armchair.setImageResource(R.drawable.armchair);
//        armchair.setContentDescription("armchair");
//        armchair.setOnClickListener(view ->{addObject(Uri.parse("Armchair_01.sfb"));});
//        gallery.addView(armchair);
//
        ImageView2 natuzzi = new ImageView2(this);
        natuzzi.setImageResource(R.drawable.natuzzithumb);
        natuzzi.setContentDescription("natuzzi");
        natuzzi.setOnClickListener(view ->{addObject(Uri.parse("natuzzi.sfb"));});
        gallery.addView(natuzzi);

        ImageView2 bed = new ImageView2(this);
        bed.setImageResource(R.drawable.bed);
        bed.setContentDescription("bed");
        bed.setOnClickListener(view ->{addObject(Uri.parse("Bed_01.sfb"));});
        gallery.addView(bed);
//
//        ImageView2 bedroom = new ImageView2(this);
//        bedroom.setImageResource(R.drawable.bedroom);
//        bedroom.setContentDescription("bedroom");
//        bedroom.setOnClickListener(view ->{addObject(Uri.parse("Bedroom.sfb"));});
//        gallery.addView(bedroom);
//
//        ImageView2 bookcase = new ImageView2(this);
//        bookcase.setImageResource(R.drawable.bookcase);
//        bookcase.setContentDescription("bookcase");
//        bookcase.setOnClickListener(view ->{addObject(Uri.parse("bookcase.sfb"));});
//        gallery.addView(bookcase);
//
//        ImageView2 breakfast = new ImageView2(this);
//        breakfast.setImageResource(R.drawable.breakfastbar);
//        breakfast.setContentDescription("breakfastbar");
//        breakfast.setOnClickListener(view ->{addObject(Uri.parse("BreakFastBar.sfb"));});
//        gallery.addView(breakfast);
//
//        ImageView2 cornertable = new ImageView2(this);
//        cornertable.setImageResource(R.drawable.cornertable);
//        cornertable.setContentDescription("andy");
//        cornertable.setOnClickListener(view ->{addObject(Uri.parse("CornerTable.sfb"));});
//        gallery.addView(cornertable);
//
//        ImageView2 couchred = new ImageView2(this);
//        couchred.setImageResource(R.drawable.couchred);
//        couchred.setContentDescription("andy");
//        couchred.setOnClickListener(view ->{addObject(Uri.parse("CouchRed.sfb"));});
//        gallery.addView(couchred);
//
        ImageView2 couchwide = new ImageView2(this);
        couchwide.setImageResource(R.drawable.couchwide);
        couchwide.setContentDescription("couchwide");
        couchwide.setOnClickListener(view ->{addObject(Uri.parse("CouchWide.sfb"));});
        gallery.addView(couchwide);
//
//        ImageView2 credenza = new ImageView2(this);
//        credenza.setImageResource(R.drawable.credenza);
//        credenza.setContentDescription("credenza");
//        credenza.setOnClickListener(view ->{addObject(Uri.parse("Credenza.sfb"));});
//        gallery.addView(credenza);
//
//        ImageView2 tabletennis = new ImageView2(this);
//        tabletennis.setImageResource(R.drawable.tabletennis);
//        tabletennis.setContentDescription("TableTennis Table");
//        tabletennis.setOnClickListener(view ->{addObject(Uri.parse("TTtable.sfb"));});
//        gallery.addView(tabletennis);

        ImageView2 lc302 = new ImageView2(this);
        lc302.setImageResource(R.drawable.lc302);
        lc302.setContentDescription("LC302");
        lc302.setOnClickListener(view ->{addObject(Uri.parse("Mare+LC302.sfb"));});
        gallery.addView(lc302);

        ImageView2 lc306 = new ImageView2(this);
        lc306.setImageResource(R.drawable.lc306);
        lc306.setContentDescription("LC306");
        lc306.setOnClickListener(view ->{addObject(Uri.parse("Mare+LC306.sfb"));});
        gallery.addView(lc306);

        ImageView2 lc309 = new ImageView2(this);
        lc309.setImageResource(R.drawable.lc309);
        lc309.setContentDescription("LC309");
        lc309.setOnClickListener(view ->{addObject(Uri.parse("Mare+LC309.sfb"));});
        gallery.addView(lc309);

        ImageView2 lc351 = new ImageView2(this);
        lc351.setImageResource(R.drawable.lc351);
        lc351.setContentDescription("LC351");
        lc351.setOnClickListener(view ->{addObject(Uri.parse("Mare+LC351.sfb"));});
        gallery.addView(lc351);

        ImageView2 lc363 = new ImageView2(this);
        lc363.setImageResource(R.drawable.lc363);
        lc363.setContentDescription("LC363");
        lc363.setOnClickListener(view ->{addObject(Uri.parse("Mare+LC363.sfb"));});
        gallery.addView(lc363);

        ImageView2 sofaNumber9 = new ImageView2(this);
        sofaNumber9.setImageResource(R.drawable.sofanumber9);
        sofaNumber9.setContentDescription("SofaNumber9");
        sofaNumber9.setOnClickListener(view ->{addObject(Uri.parse("sofa+number+9.sfb"));});
        gallery.addView(sofaNumber9);

        ImageView2 beds = new ImageView2(this);
        beds.setImageResource(R.drawable.beds);
        beds.setContentDescription("Beds");
        beds.setOnClickListener(view ->{addObject(Uri.parse("bed.sfb"));});
        gallery.addView(beds);

        ImageView2 bailu = new ImageView2(this);
        bailu.setImageResource(R.drawable.bailu);
        bailu.setContentDescription("Bailu");
        bailu.setOnClickListener(view ->{addObject(Uri.parse("bailu.sfb"));});
        gallery.addView(bailu);

        ImageView2 bs = new ImageView2(this);
        bs.setImageResource(R.drawable.blacksofa);
        bs.setContentDescription("Black Sofa");
        bs.setOnClickListener(view ->{addObject(Uri.parse("blackSofa.sfb"));});
        gallery.addView(bs);

        ImageView2 europa = new ImageView2(this);
        europa.setImageResource(R.drawable.europa);
        europa.setContentDescription("europa");
        europa.setOnClickListener(view ->{addObject(Uri.parse("europa.sfb"));});
        gallery.addView(europa);

        ImageView2 gra = new ImageView2(this);
        gra.setImageResource(R.drawable.gra);
        gra.setContentDescription("gra");
        gra.setOnClickListener(view ->{addObject(Uri.parse("gra.sfb"));});
        gallery.addView(gra);

        ImageView2 ms = new ImageView2(this);
        ms.setImageResource(R.drawable.meshseat);
        ms.setContentDescription("ms");
        ms.setOnClickListener(view ->{addObject(Uri.parse("meshseat.sfb"));});
        gallery.addView(ms);

        ImageView2 p = new ImageView2(this);
        p.setImageResource(R.drawable.papilio);
        p.setContentDescription("papilio");
        p.setOnClickListener(view ->{addObject(Uri.parse("papilio.sfb"));});
        gallery.addView(p);

        ImageView2 w = new ImageView2(this);
        w.setImageResource(R.drawable.whitechair);
        w.setContentDescription("white chair");
        w.setOnClickListener(view ->{addObject(Uri.parse("whitechair.sfb"));});
        gallery.addView(w);
    }

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
                    anchor = hitResult.createAnchor();
                    anchorList.add(anchor);
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(arFragment.getTransformationSystem());
                    andy.setRenderable(andyRenderable);
//                    andy.getScaleController().setMinScale(5.0f);
                    andy.getScaleController().setMaxScale(15.0f);
                    andy.setParent(anchorNode);
                    andy.select();
                    transformableNodes.add(andy);
//                    ((AnchorNode)andy.getParent()).getAnchor().detach();
                });
    }
}
