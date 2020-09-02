package com.electivechaos.claimsadjuster.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

/**
 * Created by krishna on 10/13/17.
 */
public class PermissionUtilities {
    public static final int MY_APP_TAKE_PHOTO_PERMISSIONS = 126;
    public static final int MY_APP_BROWSE_PHOTO_PERMISSIONS = 127;
    public static final int MY_APP_GENERATE_REPORT_PERMISSIONS = 128;

    public static final int MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS = 129;
    public static final int MY_APP_TAKE_BACK_PHOTO_PERMISSIONS = 130;
    public static final int MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS = 131;
    public static final int MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS = 132;

    public static final int MY_APP_LOCATION_PERMISSIONS = 133;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context, final Fragment
            fragment, final int type) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (type == MY_APP_TAKE_PHOTO_PERMISSIONS || type == MY_APP_TAKE_FRONT_PHOTO_PERMISSIONS || type == MY_APP_TAKE_BACK_PHOTO_PERMISSIONS || type == MY_APP_TAKE_LEFT_PHOTO_PERMISSIONS || type == MY_APP_TAKE_RIGHT_PHOTO_PERMISSIONS) {
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission Required");
                        alertBuilder.setMessage("Camera and external storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, type);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, type);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else if (type == MY_APP_BROWSE_PHOTO_PERMISSIONS) {
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission Required");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_APP_BROWSE_PHOTO_PERMISSIONS);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_APP_BROWSE_PHOTO_PERMISSIONS);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else if (type == MY_APP_GENERATE_REPORT_PERMISSIONS) {
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission Required");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_APP_GENERATE_REPORT_PERMISSIONS);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_APP_GENERATE_REPORT_PERMISSIONS);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        } else if (type == MY_APP_LOCATION_PERMISSIONS) {
            if (currentAPIVersion >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission Required");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_APP_LOCATION_PERMISSIONS);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();

                    } else {
                        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_APP_LOCATION_PERMISSIONS);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }

        return false;
    }
}