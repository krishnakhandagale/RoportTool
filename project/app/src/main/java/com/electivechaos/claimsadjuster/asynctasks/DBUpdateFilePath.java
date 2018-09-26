package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.media.ExifInterface;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.Constants;
import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.PropertyDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.google.android.gms.common.util.IOUtils;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by nafeesa on 5/18/18.
 */

public class DBUpdateFilePath extends AsyncTask<Integer,Void,Void> {
    private WeakReference<Context> contextWeakReference;
    private WeakReference<View> viewWeakReference;
    private boolean isProgressBar;
    private CategoryListDBHelper categoryListDBHelper;
    private ReportPOJO reportPOJO, originalReportPojo;
    private Context mContext;

    public  DBUpdateFilePath(Context context, View progressBarLayout, ReportPOJO reportPOJO, ReportPOJO originalReportPojo, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper) {
        this.mContext = context;
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.reportPOJO = reportPOJO;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBarLayout);
        this.originalReportPojo = originalReportPojo;
    }

    @Override
    protected void onPreExecute() {

        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();

        if(context == null){
            return;
        }
        CommonUtils.lockOrientation((Activity) context);
        if(isProgressBar)
        {
            if (progressBarLayout != null && progressBarLayout.getVisibility() == View.GONE) {
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        // If report already exists , delete it
        if(!reportPOJO.getFilePath().trim().isEmpty()){
            File file = new File(reportPOJO.getFilePath());
            if (file.exists()) {
                file.delete();
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy-hh:mm:ss");
        String dateString = simpleDateFormat.format(new Date());
        File destination = new File(Environment.getExternalStorageDirectory(), dateString + ".pdf");

        FileOutputStream fo;


        int numberOfImagesPerPage = integers[0];

        PDFDocHeader event = new PDFDocHeader("");
        event.setHeader(reportPOJO.getReportTitle(), 16);
        try{
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            final Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            addMetaData(document);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fo);
            document.open();
            pdfWriter.setPageEvent(event);


            PdfPTable propertyTable = new PdfPTable(2);

            PdfPCell defaultCell = propertyTable.getDefaultCell();
            defaultCell.setBorder(PdfPCell.NO_BORDER);

            propertyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            propertyTable.setWidthPercentage(100);


            propertyTable.addCell(getCellReportDetails(Element.ALIGN_LEFT,document));
            propertyTable.addCell(getCellForRightSide(document));

            propertyTable.completeRow();
            document.add(propertyTable);

            ArrayList<Label> labels = originalReportPojo.getLabelArrayList();
            boolean newPage = false;

            for(int i = 0 ; i< labels.get(0).getSelectedElevationImages().size(); i++){
                if(!labels.get(0).getSelectedElevationImages().get(i).getImageUrl().isEmpty()){
                    newPage = true;
                    break;
                }
            }

            if(newPage){
                document.newPage();
                event.setHeader("Elevation Images", -1);

                PdfPTable elevationTable = new PdfPTable(2);

                elevationTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                elevationTable.setWidthPercentage(100);


                Label label1 = labels.get(0);
                ArrayList<ImageDetailsPOJO> selectedElevationImagesList1 = label1.getSelectedElevationImages();

                double remainingHeight = Math.abs(document.bottom() -  pdfWriter.getVerticalPosition(false));

                PdfPCell defaultCellEleImages = elevationTable.getDefaultCell();
                defaultCellEleImages.setBorder(PdfPCell.NO_BORDER);


                int count  = 0;
                for(int i = 0 ; i<selectedElevationImagesList1.size() ; i++){
                    if (!selectedElevationImagesList1.get(i).getImageUrl().isEmpty()) {
                        byte[] imageBytesResized;
                        imageBytesResized = resizeImage(selectedElevationImagesList1.get(i).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2 - 100)), (int) ((remainingHeight / 2) -50));
                        try {
                            count++ ;
                            com.itextpdf.text.Image img;

                            img = com.itextpdf.text.Image.getInstance(imageBytesResized);
                            PdfPCell parentCell = new PdfPCell();
                            parentCell.setPadding(0);
                            parentCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            parentCell.setBorder(Rectangle.NO_BORDER);
                            parentCell.setFixedHeight((float) (remainingHeight) / 2);


                            PdfPTable imageTable = new PdfPTable(1);
                            imageTable.setWidthPercentage(100f);

                            PdfPCell innerCell1 = new PdfPCell(img, true);
                            innerCell1.setPadding(0);
                            innerCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                            innerCell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
                            innerCell1.setBorder(Rectangle.NO_BORDER);
                            innerCell1.setFixedHeight((float) (remainingHeight / 2) - 50 );


                            PdfPCell innerCell2 = new PdfPCell();
                            innerCell2.setPadding(0);
                            innerCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                            innerCell2.setVerticalAlignment(Element.ALIGN_TOP);
                            innerCell2.setBorder(Rectangle.NO_BORDER);
                            innerCell2.setFixedHeight(50);

                            if(i == 0)
                                innerCell2.addElement(new Paragraph("Front"));

                            if(i == 1)
                                innerCell2.addElement(new Paragraph("Back"));

                            if(i == 2)
                                innerCell2.addElement(new Paragraph("Left"));

                            if(i == 3)
                                innerCell2.addElement(new Paragraph("Right"));

                            imageTable.addCell(innerCell1);
                            imageTable.addCell(innerCell2);

                            parentCell.addElement(imageTable);

                            elevationTable.addCell(parentCell);

                        } catch (BadElementException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(count == 1 || count == 3){
                    elevationTable.completeRow();
                }
                document.add(elevationTable);

            }

            //Now read labels and show images accordingly.
            ArrayList<Label> mLabels = reportPOJO.getLabelArrayList();
            for(int p=0;p <mLabels.size() ;p++) {

                Label label = mLabels.get(p);
                if(!label.getName().equalsIgnoreCase("Risk Overview")) {
                    document.newPage();
                    event.setHeader(label.getName(), -1);
                }

                ArrayList<ImageDetailsPOJO> selectedElevationImagesList = label.getSelectedElevationImages();
                ArrayList<ImageDetailsPOJO> selectedImageList = label.getSelectedImages();

                int j = 0, k = 0;
                if (!label.getName().equals("Risk Overview")){

                    while (j < selectedElevationImagesList.size()) {
                        if (!selectedElevationImagesList.get(j).getImageUrl().isEmpty()) {
                            try {
                                PdfPTable table = new PdfPTable(3);
                                byte[] imageBytesResized;
                                table.setWidths(new float[]{1, 5, 4});
                                imageBytesResized = resizeImage(selectedElevationImagesList.get(j).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2) - 100), (int) ((document.getPageSize().getHeight() / numberOfImagesPerPage) - 100));
                                com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);
                                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                                table.setWidthPercentage(100);
                                table.addCell(getImageNumberPdfPCell("", PdfPCell.ALIGN_LEFT));
                                table.addCell(getCellImageCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                                table.addCell(getCell(selectedElevationImagesList.get(j).getTitle(), selectedElevationImagesList.get(j).getDescription(), PdfPCell.LEFT, document, numberOfImagesPerPage));

                                document.add(table);
                                document.add(new Paragraph(" "));

                                if ((k + 1) % numberOfImagesPerPage == 0) {
                                    document.newPage();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            k++;
                        }
                        j++;
                    }

                if (k + 1 <= selectedElevationImagesList.size()) {
                    document.newPage();
                }

                    for (int i = 0; i < selectedImageList.size(); i++) {
                    try {
                        PdfPTable table = new PdfPTable(3);
                        byte[] imageBytesResized;
                        table.setWidths(new float[]{1, 5, 4});
                        imageBytesResized = resizeImage(selectedImageList.get(i).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2) - 100), (int) ((document.getPageSize().getHeight() / numberOfImagesPerPage) - 100));
                        com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                        table.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.setWidthPercentage(100);

                        table.addCell(getImageNumberPdfPCell((i + 1) + ".", PdfPCell.ALIGN_LEFT));
                        table.addCell(getCellImageCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                        table.addCell(getCell(selectedImageList.get(i).getTitle(), selectedImageList.get(i).getDescription(), selectedImageList.get(i).isPointOfOrigin(), selectedImageList.get(i).isOverview(), selectedImageList.get(i).isDamage(), PdfPCell.LEFT, document, numberOfImagesPerPage));
                        document.add(table);
                        document.add(new Paragraph(" "));

                        if ((i + 1) % numberOfImagesPerPage == 0) {
                            document.newPage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }
            }

            document.newPage();
            event.setHeader(reportPOJO.getReportTitle(), 16);
            Font signatureFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.FOOTER_FONT_SIZE, Font.ITALIC);

            PdfContentByte cb = pdfWriter.getDirectContent();

            Phrase signature = new Phrase(new Phrase("Signature")+""+new Phrase("____________________________  "), signatureFont);
            ColumnText.showTextAligned(cb,Element.ALIGN_LEFT,
                    signature,
                    (document.left()),
                    document.bottom() +100, 0);
            Phrase date = new Phrase(new Phrase("Date")+""+new Phrase("____________________________  "), signatureFont);
            ColumnText.showTextAligned(cb,Element.ALIGN_RIGHT,
                    date,
                    (document.right()),
                    document.bottom() +100, 0);

            document.newPage();

            reportPOJO.setFilePath(destination.getAbsolutePath());
            categoryListDBHelper.updateFilePath(reportPOJO.getFilePath(),reportPOJO.getId());


            document.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



    @Override
    protected void onPostExecute(Void result) {
        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();

        if(context == null){
            return;
        }
        if(progressBarLayout != null && progressBarLayout.getVisibility() == View.VISIBLE){
            progressBarLayout.setVisibility(View.GONE);
        }
        Toast.makeText(context, R.string.report_gen_success_msg, Toast.LENGTH_SHORT).show();
        CommonUtils.unlockOrientation((Activity)context);

    }

    private byte[] resizeImage(String imagePath, int maxWidth, int maxHeight) {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
         BitmapFactory.decodeFile(imagePath, options);

        if(CommonUtils.getReportQuality(mContext).equals("low")){
            options.inSampleSize = calculateInSampleSize(options,128,96);
        }else if(CommonUtils.getReportQuality(mContext).equals("high")){
            options.inSampleSize = calculateInSampleSize(options,400,300);
        }else {
            options.inSampleSize = calculateInSampleSize(options,256,192);
        }


        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, orientation);

    }


    private byte[] resizeImageLogo(String imagePath) {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculateInSampleSize(options,256,192);

        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, orientation);


    }

    private byte[] resizeGoogleImage(String imagePath, int maxWidth, int maxHeight) {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        final BitmapFactory.Options options = new BitmapFactory.Options();

        BitmapFactory.decodeFile(imagePath, options);

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculateInSampleSize(options,352,240);

        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, orientation);

    }



    private PdfPCell getCell(String title, String description, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
        Font font=new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_VALUE_SIZE, Font.NORMAL);
        cell.addElement(new Phrase(title,font));
        cell.addElement(new Phrase(description,font));
        cell.setPaddingLeft(25f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);
        return cell;

    }
    private PdfPCell getCell(String title, String description, boolean isPointOfOrigin, boolean isOverview, boolean isDamage, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
        Font font=new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_VALUE_SIZE, Font.NORMAL);
        Font boldFont=new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_SIZE, Font.BOLD);
        if(isPointOfOrigin){
            cell.addElement(new Phrase("Point Of Origin",boldFont));
        }
        if(isOverview){
            cell.addElement(new Phrase("Overview",boldFont));
        }
        if(isDamage){
            cell.addElement(new Phrase("Damage",boldFont));
        }

        cell.addElement(new Phrase(title,font));
        cell.addElement(new Phrase(description,font));
        cell.setPaddingLeft(25f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(document.getPageSize().getHeight() / 4 - 50);
        return cell;

    }


    private PdfPCell getCellImageCell(com.itextpdf.text.Image img, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell(img, true);
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(document.getPageSize().getHeight()/ perPage - 100);
        return cell;
    }

    private PdfPCell getCellReportDetails(int alignment, Document document) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);


        PropertyDetailsPOJO propertyDetailsPOJO = reportPOJO.getPropertyDetailsPOJO();

        Font fontTitles = new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_SIZE, Font.BOLD);
        Font fontForValue = new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_VALUE_SIZE, Font.NORMAL);

        cell.addElement(new Paragraph("Report Description", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getReportDescription(),fontForValue));

        cell.addElement(new Paragraph("Inspection Date", fontTitles));

        if(propertyDetailsPOJO.getPropertyDate().isEmpty()) {
            cell.addElement(new Paragraph("No date specified.",fontForValue));
        }else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getPropertyDate(),fontForValue));
        }
        cell.addElement(new Paragraph("Insured Name", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getInsuredName(),fontForValue));

        cell.addElement(new Paragraph("Claim Number", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getClaimNumber(),fontForValue));

        cell.addElement(new Paragraph("Report By", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getReportBy(),fontForValue));


        cell.addElement(Chunk.NEWLINE);


        Chunk propDetailsTitleChunk = new Chunk("Property Details", fontTitles);
        propDetailsTitleChunk.setUnderline(0.1f, -2f);

        cell.addElement(propDetailsTitleChunk);

        cell.addElement(Chunk.NEWLINE);


        cell.addElement(new Paragraph("Risk Address", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getAddressLine(),fontForValue));




        cell.addElement(new Paragraph("Square footage", fontTitles));
        if(propertyDetailsPOJO.getSquareFootage().isEmpty()){
            cell.addElement(new Paragraph("No square footage specified.",fontForValue));
        }else {
            cell.addElement(new Paragraph(String.valueOf(propertyDetailsPOJO.getSquareFootage()),fontForValue));
        }

        cell.addElement(new Paragraph("Roof System", fontTitles));
        if(propertyDetailsPOJO.getRoofSystem().isEmpty()) {
            cell.addElement(new Paragraph("No roof system specified.",fontForValue));
        }else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getRoofSystem(),fontForValue));
        }

        cell.addElement(new Paragraph("Siding", fontTitles));

        if(propertyDetailsPOJO.getSiding().isEmpty()) {
            cell.addElement(new Paragraph("No siding type specified.",fontForValue));
        }else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getSiding(),fontForValue));
        }

        cell.addElement(new Paragraph("Foundation", fontTitles));
        if(propertyDetailsPOJO.getFoundation().isEmpty()) {
            cell.addElement(new Paragraph("No foundation type specified.",fontForValue));
        }else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getFoundation(),fontForValue));
        }

        cell.addElement(new Paragraph("Building Type", fontTitles));
        if(propertyDetailsPOJO.getBuildingType().isEmpty()) {
            cell.addElement(new Paragraph("No building type specified.",fontForValue));
        }else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getBuildingType(),fontForValue));
        }

        cell.setFixedHeight(document.getPageSize().getHeight() - 100);

        return cell;
    }


    private PdfPCell getCellForRightSide(Document document) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(0f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight((document.getPageSize().getHeight()) - 100);

        PdfPTable tableForRightSide = new PdfPTable(1);
        tableForRightSide.setWidthPercentage(100f);
        if(!CommonUtils.getGoogleMap(mContext).equalsIgnoreCase("none")) {
            PdfPCell googleMapImageCell = getGoogleMapCell(document);
            if(googleMapImageCell != null){
                tableForRightSide.addCell(getGoogleMapCell(document));
            }
        }
        PdfPCell houseNumberCell = getHouseNumberCell(document);

        if(houseNumberCell != null){
            tableForRightSide.addCell(houseNumberCell);
        }

        cell.addElement(tableForRightSide);
        return cell;
    }


    private PdfPCell getGoogleMapCell(Document document){

        PdfPCell cell = null;

        com.itextpdf.text.Image imgMap;
        if(!reportPOJO.getGoogleMapSnapShotFilePath().isEmpty()){

            File file = new File(reportPOJO.getGoogleMapSnapShotFilePath());
            if (file.exists()) {

                byte[] imgResized = resizeGoogleImage(reportPOJO.getGoogleMapSnapShotFilePath(),  (int) ((document.getPageSize().getWidth() / 2) - 100), (int)((document.getPageSize().getHeight() / 2) - 100));
                try {
                    imgMap = com.itextpdf.text.Image.getInstance(imgResized);
                    cell = new PdfPCell(imgMap,true);
                    cell.setPadding(0f);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_TOP);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight((document.getPageSize().getHeight()/2) - 100);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BadElementException e) {
                    e.printStackTrace();
                }
            }

        }


        return  cell;
    }


    private PdfPCell getHouseNumberCell(Document document){


        PdfPCell cell = null;
        if(!originalReportPojo.getLabelArrayList().get(0).getHouseNumber().isEmpty()){

            File file = new File(originalReportPojo.getLabelArrayList().get(0).getHouseNumber());
            if (file.exists()) {
                byte[] imageBytesResized;
                imageBytesResized = resizeImage(originalReportPojo.getLabelArrayList().get(0).getHouseNumber(), (int) ((document.getPageSize().getWidth() / 4) - 100), (int) ((document.getPageSize().getHeight() / 4)));
                com.itextpdf.text.Image img;
                try {
                    img = com.itextpdf.text.Image.getInstance(imageBytesResized);
                    cell = new PdfPCell();
                    cell.setPadding(0f);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight((document.getPageSize().getHeight() / 4));

                    PdfPTable innerTable = new PdfPTable(2);
                    innerTable.setWidthPercentage(100);

                    PdfPCell defaultCell = innerTable.getDefaultCell();
                    defaultCell.setBorder(PdfPCell.NO_BORDER);

                    PdfPCell innerCell = new PdfPCell(img,true);
                    innerCell.setPaddingLeft(0f);
                    innerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    innerCell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    innerCell.setBorder(Rectangle.NO_BORDER);
                    innerCell.setFixedHeight((document.getPageSize().getHeight() / 4));

                    innerTable.addCell(innerCell);
                    innerTable.completeRow();
                    cell.addElement(innerTable);


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BadElementException e) {
                    e.printStackTrace();
                }
            }
        }

        return  cell;
    }

    private byte[] resizeImage(Bitmap image, int orientation) {

            Matrix matrix = new Matrix();
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
            }


                image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                return outStream.toByteArray();

    }


    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private PdfPCell getImageNumberPdfPCell(String number, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(new Phrase(number));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }



    class PDFDocHeader extends PdfPageEventHelper {
        Font footerFont;
        Font headerFont;
        String reportTitle;
        PDFDocHeader(String reportTitle) {
            this.reportTitle = reportTitle;
        }
        private void setHeader(String reportTitle, int fontSize) {

            this.reportTitle = reportTitle;
            if (fontSize != -1) {
                headerFont = new Font(Font.FontFamily.TIMES_ROMAN, fontSize, Font.BOLD,BaseColor.BLACK);

            }else{
                headerFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.HEADER_FONT_SIZE, Font.BOLD,BaseColor.GRAY);
            }
            footerFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.FOOTER_FONT_SIZE, Font.ITALIC);
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            PdfContentByte cb = writer.getDirectContent();

            Phrase header = new Phrase(reportTitle, headerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 20, 0);

            Font headerCompanyNameFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.FOOTER_FONT_SIZE, Font.ITALIC);

            String companyName = CommonUtils.getCompanyName(mContext);
            if(!companyName.isEmpty()) {
                Phrase headerCompanyName = new Phrase(companyName, headerCompanyNameFont);
                ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                        headerCompanyName,
                         document.left(),
                        document.top()+3, 0);
            }

            Phrase footer = new Phrase(reportPOJO.getClaimNumber()+" | "+reportPOJO.getInsuredName(), footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    footer,
                    (document.left()),
                    document.bottom() - 20, 0);


            Phrase footerPageNumber = new Phrase("Page " + writer.getPageNumber() + "", footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footerPageNumber,
                    (document.right()),
                    document.bottom() - 20, 0);


            com.itextpdf.text.Image img= null;

            String imageUrl = CommonUtils.getImageLogoUrl(mContext);
            if(!imageUrl.isEmpty()) {
                try {
                    img = com.itextpdf.text.Image.getInstance(resizeImageLogo(imageUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BadElementException e) {
                    e.printStackTrace();
                }
                img.scalePercent(100);
                img.scaleToFit(35, 35);
                img.setAbsolutePosition(document.left() , document.top() +10);

                try {
                    document.add(img);
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private static void addMetaData(Document document) {
        document.addTitle("PDF Report");
        document.addSubject("Created By ElectiveChaos");
        document.addKeywords("ElectiveChaos");
        document.addAuthor("ElectiveChaos");
        document.addCreator("ElectiveChaos");
    }
}