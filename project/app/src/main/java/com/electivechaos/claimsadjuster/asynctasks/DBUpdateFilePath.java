package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.electivechaos.claimsadjuster.R;
import com.electivechaos.claimsadjuster.database.CategoryListDBHelper;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.PropertyDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
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
    private ReportPOJO reportPOJO;

    public  DBUpdateFilePath(Context context, View progressBarLayout, ReportPOJO reportPOJO, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper) {
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.reportPOJO = reportPOJO;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBarLayout);
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

       // File destination = new File(Environment.getExternalStorageDirectory(), reportPOJO.getClientName() + ".pdf");

        FileOutputStream fo;
        Font fontTitles = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);


        int numberOfImagesPerPage = integers[0];

        PDFDocHeader event = new PDFDocHeader(reportPOJO.getReportTitle());
        try{
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            final Document document = new Document(PageSize.A4, 50, 50, 50, 50);
            addMetaData(document);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fo);
            document.open();
            pdfWriter.setPageEvent(event);
            document.add(new Paragraph("Report Description", fontTitles));
            document.add(new Paragraph(reportPOJO.getReportDescription()));
            document.add(new Paragraph(""));
            document.add(new Paragraph("Client Name", fontTitles));
            document.add(new Paragraph(reportPOJO.getClientName()));
            document.add(new Paragraph(""));

            document.add(new Paragraph("Claim Number", fontTitles));
            document.add(new Paragraph(reportPOJO.getClaimNumber()));
            document.add(new Paragraph(""));

            LineSeparator ls = new LineSeparator();
            ls.setLineColor(new BaseColor(99,100,99));
            document.add(new Chunk(ls));
            document.add(new Paragraph("Address", fontTitles));
            document.add(new Paragraph(reportPOJO.getAddressLine()));

            if(!reportPOJO.getGoogleMapSnapShotFilePath().isEmpty()){

                File file = new File(reportPOJO.getGoogleMapSnapShotFilePath());
                if (file.exists()) {
                    double remainingHeight = Math.abs(document.bottom() -  pdfWriter.getVerticalPosition(true));
                    byte[] imgResized = resizeImage(reportPOJO.getGoogleMapSnapShotFilePath(), (int) document.getPageSize().getWidth()/2, (int) remainingHeight/2);
                    com.itextpdf.text.Image imgMap = com.itextpdf.text.Image.getInstance(imgResized);
                    document.add(imgMap);

                }

            }

            PropertyDetailsPOJO propertyDetailsPOJO = reportPOJO.getPropertyDetailsPOJO();
            document.newPage();
            // Now read property details
            event.setHeader("Property Details");
            document.add(new Paragraph("Inspection Date", fontTitles));
            if(propertyDetailsPOJO.getPropertyDate().isEmpty()) {
                document.add(new Paragraph("No date specified."));
            }else {
                document.add(new Paragraph(propertyDetailsPOJO.getPropertyDate()));
            }
            document.add(new Paragraph(""));


            document.add(new Paragraph("Square footage", fontTitles));
            if(propertyDetailsPOJO.getSquareFootage().isEmpty()){
                document.add(new Paragraph("No square footage specified."));
            }else {
                document.add(new Paragraph(String.valueOf(propertyDetailsPOJO.getSquareFootage())));
            }
            document.add(new Paragraph(""));

            document.add(new Paragraph("Roof System", fontTitles));
            if(propertyDetailsPOJO.getRoofSystem().isEmpty()) {
                document.add(new Paragraph("No roof system specified."));
            }else {
                document.add(new Paragraph(propertyDetailsPOJO.getRoofSystem()));
            }
            document.add(new Paragraph(""));

            document.add(new Paragraph("Siding", fontTitles));

            if(propertyDetailsPOJO.getSiding().isEmpty()) {
                document.add(new Paragraph("No siding type specified."));
            }else {
                document.add(new Paragraph(propertyDetailsPOJO.getSiding()));
            }
            document.add(new Paragraph(""));

            document.add(new Paragraph("Foundation", fontTitles));
            if(propertyDetailsPOJO.getFoundation().isEmpty()) {
                document.add(new Paragraph("No foundation type specified."));
            }else {
                document.add(new Paragraph(propertyDetailsPOJO.getFoundation()));
            }
            document.add(new Paragraph(""));

            document.add(new Paragraph("Building Type", fontTitles));
            if(propertyDetailsPOJO.getBuildingType().isEmpty()) {
                document.add(new Paragraph("No building type specified."));
            }else {
                document.add(new Paragraph(propertyDetailsPOJO.getBuildingType()));
            }
            document.add(new Paragraph(""));


            document.newPage();

            //Now read labels and show images accordingly.

            ArrayList<Label> labels = reportPOJO.getLabelArrayList();
            for(int p=0;p <labels.size() ;p++){

                Label label = labels.get(p);
                event.setHeader(label.getName());


                ArrayList<ImageDetailsPOJO> selectedElevationImagesList =  label.getSelectedElevationImages();
                ArrayList<ImageDetailsPOJO> selectedImageList = label.getSelectedImages();

                int j =0, k= 0;
                while( j< selectedElevationImagesList.size()){
                    if(!selectedElevationImagesList.get(j).getImageUrl().isEmpty()){
                        try {
                            PdfPTable table = new PdfPTable(3);
                            byte[] imageBytesResized;
                            table.setWidths(new float[]{1, 5, 4});
                            imageBytesResized = resizeImage(selectedElevationImagesList.get(j).getImageUrl(), (int) ((document.getPageSize().getWidth() / 2) - 100), (int) ((document.getPageSize().getHeight() / 2) - 100));
                            com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                            table.setHorizontalAlignment(Element.ALIGN_LEFT);
                            table.setWidthPercentage(100);
                            table.addCell(getImageNumberPdfPCell("", PdfPCell.ALIGN_LEFT));
                            table.addCell(getCellImagCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
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

                if(k +1 < selectedElevationImagesList.size()){
                    document.newPage();
                }

                if(!TextUtils.isEmpty(label.getHouseNumber())){

                    PdfPTable table = new PdfPTable(3);
                    byte[] imageBytesResized;
                    table.setWidths(new float[]{1, 5, 4});
                    imageBytesResized = resizeImage(label.getHouseNumber(), (int) ((document.getPageSize().getWidth()/2) - 100), (int) ((document.getPageSize().getHeight()/2) - 100));
                    com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                    table.setHorizontalAlignment(Element.ALIGN_LEFT);
                    table.setWidthPercentage(100);
                    table.addCell(getImageNumberPdfPCell("", PdfPCell.ALIGN_LEFT));
                    table.addCell(getCellImagCell(img, PdfPCell.ALIGN_LEFT, document, 2));
                    table.addCell(getCell("House Number", "House Number Image", PdfPCell.LEFT, document, 2));
                    document.add(table);
                    document.add(new Paragraph(" "));
                }


                for (int i = 0; i < selectedImageList.size(); i++) {
                    try {
                        PdfPTable table = new PdfPTable(3);
                        byte[] imageBytesResized;
                        table.setWidths(new float[]{1, 5, 4});
                        imageBytesResized = resizeImage(selectedImageList.get(i).getImageUrl(), (int) ((document.getPageSize().getWidth()/ 2) - 100), (int) ((document.getPageSize().getHeight() / numberOfImagesPerPage) - 100));
                        com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                        table.setHorizontalAlignment(Element.ALIGN_LEFT);
                        table.setWidthPercentage(100);
                        table.addCell(getImageNumberPdfPCell((i + 1) + ".", PdfPCell.ALIGN_LEFT));
                        table.addCell(getCellImagCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                        table.addCell(getCell(selectedImageList.get(i).getTitle(), selectedImageList.get(i).getDescription(),selectedImageList.get(i).isPointOfOrigin(),selectedImageList.get(i).isOverview(),selectedImageList.get(i).isDamage(), PdfPCell.LEFT, document, numberOfImagesPerPage));
                        document.add(table);
                        document.add(new Paragraph(" "));

                        if ((i + 1) % numberOfImagesPerPage == 0) {
                            document.newPage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                document.newPage();

            }
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

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, maxWidth, maxHeight, orientation);

    }

    public PdfPCell getCell(String title, String description, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
        Font font=new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        cell.addElement(new Phrase(title,font));
        cell.addElement(new Phrase(description,font));
        cell.setPadding(0);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(document.getPageSize().getHeight() / perPage - 100);
        return cell;

    }public PdfPCell getCell(String title, String description,boolean isPointOfOrigin, boolean isOverview,boolean isDamage, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
        Font font=new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
        Font boldFont=new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
        if(isPointOfOrigin){
            cell.addElement(new Phrase("P.O.O",boldFont));
        }
        if(isOverview){
            cell.addElement(new Phrase("Overview",boldFont));
        }
        if(isDamage){
            cell.addElement(new Phrase("Damage",boldFont));
        }

        cell.addElement(new Phrase(title,font));
        cell.addElement(new Phrase(description,font));
        cell.setPadding(0);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(document.getPageSize().getHeight() / 4 - 50);
        Log.d("DON",""+(document.getPageSize().getHeight() / perPage - 100));
        return cell;

    }


    public PdfPCell getCellImagCell(com.itextpdf.text.Image img, int alignment, Document document, int perPage) {

        PdfPCell cell = new PdfPCell(img);
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight(document.getPageSize().getHeight()/ perPage - 100);
        return cell;
    }


    private byte[] resizeImage(Bitmap image, int maxWidth, int maxHeight, int orientation) {

        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxHeight / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            Bitmap rotatedBitmap;
            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(image, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(image, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(image, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    rotatedBitmap = image;
            }
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return outStream.toByteArray();
        } else {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            return outStream.toByteArray();
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public PdfPCell getImageNumberPdfPCell(String number, int alignment) {
        PdfPCell cell = new PdfPCell();
        cell.addElement(new Phrase(number));
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    class PDFDocHeader extends PdfPageEventHelper {
        Font footerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.ITALIC);
        Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
        String reportTitle;

        PDFDocHeader(String reportTitle) {
            this.reportTitle = reportTitle;
        }
        public void setHeader(String reportTitle) {
            this.reportTitle = reportTitle;
        }



        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase(reportTitle, headerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 10, 0);
            Phrase footer = new Phrase("Page " + writer.getPageNumber() + "", footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footer,
                    (document.right()),
                    document.bottom() - 10, 0);

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