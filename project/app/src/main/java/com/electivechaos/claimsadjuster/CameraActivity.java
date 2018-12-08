package com.electivechaos.claimsadjuster;

import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import com.camerakit.CameraKit;
import com.camerakit.CameraKitView;
import com.camerakit.CameraPreview;
import com.camerakit.type.CameraFlash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class CameraActivity extends AppCompatActivity {

    private CameraKitView cameraKitView;
    private FloatingActionButton btnCapture;
    private ImageButton btnFrontBack;
    private ImageView btnFlashOn;
    private ImageView btnFlashOff;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        cameraKitView = findViewById(R.id.camera);
        btnCapture = findViewById(R.id.capture);
        btnFrontBack = findViewById(R.id.btn_front_back);
        btnFlashOn = findViewById(R.id.btn_flash_on);
        btnFlashOff = findViewById(R.id.btn_flash_off);

        cameraKitView.setFlash(CameraKit.FLASH_OFF);


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.captureImage(new CameraKitView.ImageCallback() {
                    @Override
                    public void onImage(CameraKitView cameraKitView, final byte[] capturedImage) {
                        String fileUri =  getIntent().getExtras().getString(MediaStore.EXTRA_OUTPUT);
                        if(fileUri == null){
                            return;
                        }
                        File file =  new File(fileUri);

                        if(!file.exists()){
                            try {
                                 file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            FileOutputStream outputStream = new FileOutputStream(fileUri,false);
                            outputStream.write(capturedImage);
                            outputStream.close();
                        } catch (java.io.IOException e) {
                            e.printStackTrace();
                        }


                        Intent intent = new Intent();
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
            }
        });




        btnFrontBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cameraKitView.getFacing() ==CameraKit.FACING_BACK) {
                    cameraKitView.setFacing(CameraKit.FACING_FRONT);
                }else {
                    cameraKitView.setFacing(CameraKit.FACING_BACK);
                }
            }
        });

        btnFlashOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.setFlash(CameraKit.FLASH_OFF);
                btnFlashOn.setVisibility(View.GONE);
                btnFlashOff.setVisibility(View.VISIBLE);
            }
        });

        btnFlashOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraKitView.setFlash(CameraKit.FLASH_ON);
                btnFlashOff.setVisibility(View.GONE);
                btnFlashOn.setVisibility(View.VISIBLE);
            }
        });


        cameraKitView.setGestureListener(new CameraKitView.GestureListener() {
            @Override
            public void onTap(CameraKitView cameraKitView, float v, float v1) {
                cameraKitView.setFocusable(true);

            }

            @Override
            public void onLongTap(CameraKitView cameraKitView, float v, float v1) {
            }

            @Override
            public void onDoubleTap(CameraKitView cameraKitView, float v, float v1) {

            }

            @Override
            public void onPinch(CameraKitView cameraKitView, float v, float v1, float v2) {
            }

        });
    }



    @Override
    protected void onStart() {
        super.onStart();
        cameraKitView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraKitView.onResume();
    }

    @Override
    protected void onPause() {
        cameraKitView.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        cameraKitView.onStop();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}