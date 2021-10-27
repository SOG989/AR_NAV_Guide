package com.android_project.augmentedrealityimage;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {
    //variable for ArFragment
    private ArFragment arFragment;
    private ModelRenderable modelRenderable;
    //URL for 3D Model. glb file
    //link 1 for 3D Astronaut model
   // private String MODEL_URL="https://modelviewer.dev/shared-assets/models/Astronaut.glb?raw=true";
    ////link 2 for 3D navigation arrow
   private String MODEL_URL="https://github.com/SOG989/3DMODEL/blob/b70c27510c2a7a1a37b0d087a03c2afcbd506195/AnyConv.com__Arrow2.glb?raw=true";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    //initializing fragment, load Model, setup AR plane
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        setUpModel();
        setUpPlane();
    }



    //method for loading up model and other renderable resources in the background
    private void setUpModel() {
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(this,
                        Uri.parse(MODEL_URL),
                        RenderableSource.SourceType.GLB)
                        //sets size of model
                        .setScale(0.05f)
                        .setRecenterMode(RenderableSource.RecenterMode.ROOT)
                        .build())
                .setRegistryId(MODEL_URL)
                .build()
                .thenAccept(renderable -> modelRenderable = renderable)
                //display error message incase error occurs
                .exceptionally(throwable -> {
                    Toast.makeText(MainActivity.this,"Unable to load Model", Toast.LENGTH_SHORT).show();
                    return null;
                });

    }
    //set up 3D plane
    private void setUpPlane() {
        //setOnTapArPlaneListener checks if user has tapped on screen and returns hitresult
        arFragment.setOnTapArPlaneListener(((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();
            //Anchornode postions 3D model in AR plane
            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arFragment.getArSceneView().getScene());
            createModel(anchorNode);
        }));
    }

    //create  3D Model
    private void createModel(AnchorNode anchorNode) {
        //Transformable node used to allow user drag, scale or transform 3D Model
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(modelRenderable);
        node.select();
    }
}