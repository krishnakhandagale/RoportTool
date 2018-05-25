package com.electivechaos.claimsadjuster;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.electivechaos.claimsadjuster.pojo.Image;

import java.util.List;

/**
 * Created by krishna on 11/16/17.
 */
    public class ImageHelper {
        private static final String TAG = "ImageHelper";


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

        public static boolean isGifFormat(Image image) {
            String extension = image.getPath().substring(image.getPath().lastIndexOf(".") + 1, image.getPath().length());
            return extension.equalsIgnoreCase("gif");
        }


    }
