package com.electivechaos.claimsadjuster.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.electivechaos.claimsadjuster.R;

import java.io.File;


public class PdfViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_viewer_layout);

        ImageView shareBottomButton = findViewById(R.id.shareBottomButton);



        String filePath = getIntent().getExtras().getString("filePath");
        final File file = new File(filePath);

        shareBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getApplicationContext(),
                        getApplicationContext().getPackageName() + ".fileprovider",
                        file);
                Intent share = new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.setType("application/pdf");
                share.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(share);
            }
        });
    }
}
