package com.electivechaos.checklistapp.listeners;

import android.net.Uri;

/**
 * Created by krishna on 11/18/17.
 */

public interface OnMediaScannerListener {
    void onMediaScanComplete(String path, Uri uri);
}
