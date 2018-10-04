package com.electivechaos.claimsadjuster;

/**
 * Created by krishna on 11/18/17.
 */

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

import com.electivechaos.claimsadjuster.listeners.OnMediaScannerListener;

import java.io.File;

public class SingleMediaScanner implements MediaScannerConnectionClient {

    private MediaScannerConnection mMs;
    private File mFile;
    private OnMediaScannerListener onMediaScannerListener;

    public SingleMediaScanner(Context context, File f, OnMediaScannerListener onMediaScannerListener) {
        mFile = f;
        this.onMediaScannerListener = onMediaScannerListener;
        mMs = new MediaScannerConnection(context, this);
        mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(mFile.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String path, Uri uri) {
        onMediaScannerListener.onMediaScanComplete(path,uri);
        mMs.disconnect();
    }

}
