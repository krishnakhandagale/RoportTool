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
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;

import java.io.File;


public class PdfViewerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_viewer_layout);

        ImageView shareBottomButton = findViewById(R.id.shareBottomButton);



//        getSupportActionBar().hide();
        final PDFView pdfView = findViewById(R.id.pdfView);
        String filePath = getIntent().getExtras().getString("report_path");
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
        pdfView.fromFile(file).spacing(5).onLoad(new OnLoadCompleteListener() {
            @Override
            public void loadComplete(int nbPages) {
//                numberOfPagesInPdf = nbPages;
                pdfView.setBackgroundColor(getResources().getColor(R.color.imagepicker_grey));
            }
        }).onPageChange(new OnPageChangeListener() {

            @Override
            public void onPageChanged(int page, int pageCount) {
//                Toast toast = Toast.makeText(PdfViewerActivity.this,(page+1)+"/"+numberOfPagesInPdf,Toast.LENGTH_SHORT);
//                toast.setGravity(Gravity.TOP| Gravity.RIGHT, 20, 100);
//                toast.show();
            }
        }).onPageScroll(new OnPageScrollListener() {
            @Override
            public void onPageScrolled(int page, float positionOffset) {
//                positionYOffset = (int) positionOffset;

            }
        })
                .load();
    }
}
