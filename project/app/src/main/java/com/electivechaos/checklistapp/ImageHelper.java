package com.electivechaos.checklistapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.electivechaos.checklistapp.Pojo.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by krishna on 11/16/17.
 */
    public class ImageHelper {
        private static final String TAG = "ImageHelper";

        public static String getNameFromFilePath(String path) {
            if (path.contains(File.separator)) {
                return path.substring(path.lastIndexOf(File.separator) + 1);
            }
            return path;
        }

        public static void grantAppPermission(Context context, Intent intent, Uri fileUri) {
            List<ResolveInfo> resolvedIntentActivities = context.getPackageManager()
                    .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;
                context.grantUriPermission(packageName, fileUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }

        public static void revokeAppPermission(Context context, Uri fileUri) {
            context.revokeUriPermission(fileUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        public static List<Image> singleListFromPath(String path) {
            List<Image> images = new ArrayList<>();
            images.add(new Image(0, getNameFromFilePath(path), path));
            return images;
        }

        public static boolean isGifFormat(Image image) {
            String extension = image.getPath().substring(image.getPath().lastIndexOf(".") + 1, image.getPath().length());
            return extension.equalsIgnoreCase("gif");
        }


    }
