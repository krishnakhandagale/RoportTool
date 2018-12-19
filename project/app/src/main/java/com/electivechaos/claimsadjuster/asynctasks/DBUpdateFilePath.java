package com.electivechaos.claimsadjuster.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.media.ExifInterface;
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
import com.itextpdf.text.BadElementException;
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

public class DBUpdateFilePath extends AsyncTask<Integer, Void, Void> {
    private WeakReference<Context> contextWeakReference;
    private WeakReference<View> viewWeakReference;
    private boolean isProgressBar;
    private CategoryListDBHelper categoryListDBHelper;
    private ReportPOJO reportPOJO, originalReportPojo;
    private Context mContext;

    public DBUpdateFilePath(Context context, View progressBarLayout, ReportPOJO reportPOJO, ReportPOJO originalReportPojo, boolean isProgressBar, CategoryListDBHelper categoryListDBHelper) {
        this.mContext = context;
        this.isProgressBar = isProgressBar;
        this.categoryListDBHelper = categoryListDBHelper;
        this.reportPOJO = reportPOJO;
        this.contextWeakReference = new WeakReference<>(context);
        this.viewWeakReference = new WeakReference<>(progressBarLayout);
        this.originalReportPojo = originalReportPojo;
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

    private static void addMetaData(Document document) {
        document.addTitle("PDF Report");
        document.addSubject("Created By ElectiveChaos");
        document.addKeywords("ElectiveChaos");
        document.addAuthor("ElectiveChaos");
        document.addCreator("ElectiveChaos");
    }

    @Override
    protected void onPreExecute() {

        Context context = contextWeakReference.get();
        View progressBarLayout = viewWeakReference.get();

        if (context == null) {
            return;
        }
        CommonUtils.lockOrientation((Activity) context);
        if (isProgressBar) {
            if (progressBarLayout != null && progressBarLayout.getVisibility() == View.GONE) {
                progressBarLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        // If report already exists , delete it
        if (!reportPOJO.getFilePath().trim().isEmpty()) {
            File file = new File(reportPOJO.getFilePath());
            if (file.exists()) {
                boolean isDeleted = file.delete();
                Log.d("LOG", String.valueOf(isDeleted));
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-mm-yyyy-hh:mm:ss");
        String dateString = simpleDateFormat.format(new Date());
        File destination = new File(Environment.getExternalStorageDirectory(), dateString + ".pdf");

        FileOutputStream fo;


        int numberOfImagesPerPage = integers[0];

        PDFDocHeader event = new PDFDocHeader("");
        event.setHeader(reportPOJO.getReportTitle(), 14);
        try {

            if (!destination.createNewFile()) {
                return null;
            }
            fo = new FileOutputStream(destination);
            final Document document = new Document(PageSize.A4, 50, 50, 80, 50);
            addMetaData(document);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, fo);
            document.open();
            pdfWriter.setPageEvent(event);


            PdfPTable propertyTable = new PdfPTable(2);

            PdfPCell defaultCell = propertyTable.getDefaultCell();
            defaultCell.setBorder(PdfPCell.NO_BORDER);

            propertyTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            propertyTable.setWidthPercentage(100);


            propertyTable.addCell(getCellReportDetails(Element.ALIGN_LEFT, document));
            propertyTable.addCell(getCellForRightSide(document));

            propertyTable.completeRow();
            document.add(propertyTable);

            ArrayList<Label> labels = originalReportPojo.getLabelArrayList();
            boolean newPage = false;

            for (int i = 0; i < labels.get(0).getSelectedElevationImages().size(); i++) {
                if (!labels.get(0).getSelectedElevationImages().get(i).getImageUrl().isEmpty()) {
                    newPage = true;
                    break;
                }
            }

            if (newPage) {
                document.newPage();
                event.setHeader("Elevation Images", -1);

                PdfPTable elevationTable = new PdfPTable(2);

                elevationTable.setHorizontalAlignment(Element.ALIGN_LEFT);
                elevationTable.setWidthPercentage(100);


                Label label1 = labels.get(0);
                ArrayList<ImageDetailsPOJO> selectedElevationImagesList1 = label1.getSelectedElevationImages();

                double remainingHeight = Math.abs(document.bottom() - pdfWriter.getVerticalPosition(false));

                PdfPCell defaultCellEleImages = elevationTable.getDefaultCell();
                defaultCellEleImages.setBorder(PdfPCell.NO_BORDER);


                int count = 0;
                for (int i = 0; i < selectedElevationImagesList1.size(); i++) {
                    if (!selectedElevationImagesList1.get(i).getImageUrl().isEmpty()) {
                        File file = new File(selectedElevationImagesList1.get(i).getImageUrl());
                        if (file.exists()) {
                            byte[] imageBytesResized;
                            imageBytesResized = resizeImage(selectedElevationImagesList1.get(i).getImageUrl());
                            try {
                                count++;
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
                                innerCell1.setPaddingLeft(5f);
                                innerCell1.setHorizontalAlignment(Element.ALIGN_LEFT);
                                innerCell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
                                innerCell1.setBorder(Rectangle.NO_BORDER);
                                innerCell1.setFixedHeight((float) (remainingHeight / 2) - 50);


                                PdfPCell innerCell2 = new PdfPCell();
                                innerCell2.setPaddingLeft(5f);
                                innerCell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                                innerCell2.setVerticalAlignment(Element.ALIGN_TOP);
                                innerCell2.setBorder(Rectangle.NO_BORDER);
                                innerCell2.setFixedHeight(50);

                                if (i == 0)
                                    innerCell2.addElement(new Paragraph("Front"));

                                if (i == 1)
                                    innerCell2.addElement(new Paragraph("Back"));

                                if (i == 2)
                                    innerCell2.addElement(new Paragraph("Left"));

                                if (i == 3)
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
                }
                if (count == 1 || count == 3) {
                    elevationTable.completeRow();
                }
                document.add(elevationTable);

            }

            //Now read labels and show images accordingly.
            ArrayList<Label> mLabels = reportPOJO.getLabelArrayList();
            for (int p = 0; p < mLabels.size(); p++) {

                Label label = mLabels.get(p);
                if (!label.getName().equalsIgnoreCase("Risk Overview")) {
                    document.newPage();
                    event.setHeader(label.getName(), -1);
                }

                ArrayList<ImageDetailsPOJO> selectedElevationImagesList = label.getSelectedElevationImages();
                ArrayList<ImageDetailsPOJO> selectedImageList = label.getSelectedImages();

                int j = 0, k = 0;
                if (!label.getName().equals("Risk Overview")) {

                    while (j < selectedElevationImagesList.size()) {
                        if (!selectedElevationImagesList.get(j).getImageUrl().isEmpty()) {
                            File file = new File(selectedElevationImagesList.get(j).getImageUrl());
                            if (file.exists()) {
                                try {
                                    PdfPTable table = new PdfPTable(3);
                                    byte[] imageBytesResized;
                                    table.setWidths(new float[]{1, 5, 4});
                                    imageBytesResized = resizeImage(selectedElevationImagesList.get(j).getImageUrl());
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
                        }
                        j++;
                    }

                    if (k + 1 <= selectedElevationImagesList.size()) {
                        document.newPage();
                    }

                    for (int i = 0, m = 0; i < selectedImageList.size(); i++, m++) {
                        File file = new File(selectedImageList.get(i).getImageUrl());
                        if (file.exists()) {
                            try {
                                PdfPTable table = new PdfPTable(3);
                                byte[] imageBytesResized;
                                table.setWidths(new float[]{1, 5, 4});
                                imageBytesResized = resizeImage(selectedImageList.get(i).getImageUrl());
                                com.itextpdf.text.Image img = com.itextpdf.text.Image.getInstance(imageBytesResized);

                                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                                table.setWidthPercentage(100);

                                table.addCell(getImageNumberPdfPCell((m + 1) + ".", PdfPCell.ALIGN_LEFT));
                                table.addCell(getCellImageCell(img, PdfPCell.ALIGN_LEFT, document, numberOfImagesPerPage));
                                table.addCell(getCell(selectedImageList.get(i).getTitle(), selectedImageList.get(i).getDescription(), selectedImageList.get(i).isPointOfOrigin(), selectedImageList.get(i).isOverview(), selectedImageList.get(i).isDamage(), PdfPCell.LEFT, document, numberOfImagesPerPage));
                                document.add(table);
                                document.add(new Paragraph(" "));

                                if ((m + 1) % numberOfImagesPerPage == 0) {
                                    document.newPage();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            m--;
                        }

                    }

                }
            }

            document.newPage();
            event.setHeader(reportPOJO.getReportTitle(), 14);
            Font signatureFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.FOOTER_FONT_SIZE, Font.ITALIC);

            PdfPTable signatureTable = new PdfPTable(2);
            signatureTable.setTotalWidth(500f);
            signatureTable.setWidthPercentage(100);

            PdfContentByte cb = pdfWriter.getDirectContent();

            Phrase signature = new Phrase("Signature" + " : " + "____________________________  ", signatureFont);

            Phrase date = new Phrase("Date" + " : " + "____________________________  ", signatureFont);

            Phrase name = null;
            if (!CommonUtils.getReportByField(mContext).isEmpty()) {
                name = new Phrase("Name" + "  :  " + CommonUtils.getReportByField(mContext), signatureFont);
            }

            Phrase officeAddress = null;
            if (!CommonUtils.getAddress(mContext).isEmpty()) {
                officeAddress = new Phrase(CommonUtils.getAddress(mContext), signatureFont);
            }

            PdfPCell signCell = new PdfPCell();
            signCell.setFixedHeight(20f);
            signCell.setBorder(Rectangle.NO_BORDER);
            signCell.addElement(signature);

            PdfPCell dateCell = new PdfPCell();
            dateCell.setRowspan(3);
            dateCell.setFixedHeight(20f);
            dateCell.setBorder(Rectangle.NO_BORDER);
            dateCell.addElement(date);

            PdfPCell nameCell = new PdfPCell();
            nameCell.setFixedHeight(20f);
            nameCell.setBorder(Rectangle.NO_BORDER);
            nameCell.addElement(name);

            PdfPCell addressCell = new PdfPCell();
            addressCell.setFixedHeight(50f);
            addressCell.setBorder(Rectangle.NO_BORDER);
            addressCell.addElement(officeAddress);

            signatureTable.addCell(signCell);
            signatureTable.addCell(dateCell);
            signatureTable.addCell(nameCell);
            signatureTable.addCell(addressCell);


            signatureTable.completeRow();
            signatureTable.writeSelectedRows(0, -1, document.getPageSize().getLeft() + 50, signatureTable.getTotalHeight() + 100, cb);

            document.newPage();

            reportPOJO.setFilePath(destination.getAbsolutePath());
            categoryListDBHelper.updateFilePath(reportPOJO.getFilePath(), reportPOJO.getId());


            document.close();
        } catch (FileNotFoundException e) {
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

        if (context == null) {
            return;
        }
        if (progressBarLayout != null && progressBarLayout.getVisibility() == View.VISIBLE) {
            progressBarLayout.setVisibility(View.GONE);
        }
        Toast.makeText(context, R.string.report_gen_success_msg, Toast.LENGTH_SHORT).show();
        CommonUtils.unlockOrientation((Activity) context);

    }

    private byte[] resizeImage(String imagePath) {

        int orientation = getOrientation(imagePath);

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        if (CommonUtils.getReportQuality(mContext).equals("low")) {
            options.inSampleSize = calculateInSampleSize(options, 128, 96);
        } else if (CommonUtils.getReportQuality(mContext).equals("high")) {
            options.inSampleSize = calculateInSampleSize(options, 400, 300);
        } else {
            options.inSampleSize = calculateInSampleSize(options, 256, 192);
        }


        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, orientation);

    }

    private byte[] resizeImageLogo(String imagePath) {

        int orientation = getOrientation(imagePath);

        if (orientation == -1) {
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculateInSampleSize(options, 256, 192);

        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, orientation);


    }

    private int getOrientation(String imagePath) {
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ei == null) {
            return -1;
        }
        return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
    }

    private byte[] resizeGoogleImage(String imagePath) {

        int orientation = getOrientation(imagePath);

        final BitmapFactory.Options options = new BitmapFactory.Options();

        BitmapFactory.decodeFile(imagePath, options);

        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inSampleSize = calculateInSampleSize(options, 352, 240);

        options.inJustDecodeBounds = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        return resizeImage(bmp, orientation);

    }

    private PdfPCell getCell(String title, String description, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
        cell.setPaddingTop(0f);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setHorizontalAlignment(Rectangle.ALIGN_LEFT);
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_VALUE_SIZE, Font.NORMAL);

        cell.addElement(new Phrase(title, font));
        cell.addElement(new Phrase(description, font));
        cell.setPaddingLeft(25f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight((document.getPageSize().getHeight() - 220) / perPage);
        return cell;

    }

    private PdfPCell getCell(String title, String description, boolean isPointOfOrigin, boolean isOverview, boolean isDamage, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell();
        Font font = new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_VALUE_SIZE, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.LABEL_FONT_SIZE, Font.BOLD);
        if (isPointOfOrigin) {
            cell.addElement(new Phrase("Point Of Origin", boldFont));
        }
        if (isOverview) {
            cell.addElement(new Phrase("Overview", boldFont));
        }
        if (isDamage) {
            cell.addElement(new Phrase("Damage", boldFont));
        }

        cell.addElement(new Phrase(title, font));
        cell.addElement(new Phrase(description, font));
        cell.setPaddingLeft(25f);
        cell.setPaddingTop(0f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight((document.getPageSize().getHeight() - 220) / perPage);

        return cell;

    }

    private PdfPCell getCellImageCell(com.itextpdf.text.Image img, int alignment, Document document, int perPage) {
        PdfPCell cell = new PdfPCell(img, true);
        cell.setHorizontalAlignment(Rectangle.LEFT);
        cell.setVerticalAlignment(Rectangle.TOP);
        cell.setPadding(0);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight((document.getPageSize().getHeight() - 220) / perPage);

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
        cell.addElement(new Paragraph(reportPOJO.getReportDescription(), fontForValue));

        cell.addElement(new Paragraph("Inspection Date", fontTitles));

        if (propertyDetailsPOJO.getPropertyDate().isEmpty()) {
            cell.addElement(new Paragraph("No date specified.", fontForValue));
        } else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getPropertyDate(), fontForValue));
        }
        cell.addElement(new Paragraph("Insured Name", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getInsuredName(), fontForValue));

        cell.addElement(new Paragraph("Claim Number", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getClaimNumber(), fontForValue));

        cell.addElement(new Paragraph("Report By", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getReportBy(), fontForValue));

        cell.addElement(Chunk.NEWLINE);

        Chunk propDetailsTitleChunk = new Chunk("Property Details", fontTitles);
        propDetailsTitleChunk.setUnderline(0.1f, -2f);

        cell.addElement(propDetailsTitleChunk);

        cell.addElement(new Paragraph("Risk Address", fontTitles));
        cell.addElement(new Paragraph(reportPOJO.getAddressLine(), fontForValue));

        cell.addElement(new Paragraph("Square footage", fontTitles));
        if (propertyDetailsPOJO.getSquareFootage().isEmpty()) {
            cell.addElement(new Paragraph("No square footage specified.", fontForValue));
        } else {
            cell.addElement(new Paragraph(String.valueOf(propertyDetailsPOJO.getSquareFootage()), fontForValue));
        }

        cell.addElement(new Paragraph("Roof System", fontTitles));
        if (propertyDetailsPOJO.getRoofSystem().isEmpty()) {
            cell.addElement(new Paragraph("No roof system specified.", fontForValue));
        } else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getRoofSystem(), fontForValue));
        }

        cell.addElement(new Paragraph("Siding", fontTitles));

        if (propertyDetailsPOJO.getSiding().isEmpty()) {
            cell.addElement(new Paragraph("No siding type specified.", fontForValue));
        } else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getSiding(), fontForValue));
        }

        cell.addElement(new Paragraph("Foundation", fontTitles));
        if (propertyDetailsPOJO.getFoundation().isEmpty()) {
            cell.addElement(new Paragraph("No foundation type specified.", fontForValue));
        } else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getFoundation(), fontForValue));
        }

        cell.addElement(new Paragraph("Building Type", fontTitles));
        if (propertyDetailsPOJO.getBuildingType().isEmpty()) {
            cell.addElement(new Paragraph("No building type specified.", fontForValue));
        } else {
            cell.addElement(new Paragraph(propertyDetailsPOJO.getBuildingType(), fontForValue));
        }

        cell.setFixedHeight(document.getPageSize().getHeight() - 130);

        return cell;
    }

    private PdfPCell getCellForRightSide(Document document) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(0f);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_TOP);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setFixedHeight((document.getPageSize().getHeight()) - 130);

        PdfPTable tableForRightSide = new PdfPTable(1);
        tableForRightSide.setWidthPercentage(100f);
        if (!CommonUtils.getGoogleMap(mContext).equalsIgnoreCase("none")) {
            PdfPCell googleMapImageCell = getGoogleMapCell(document);
            if (googleMapImageCell != null) {
                tableForRightSide.addCell(getGoogleMapCell(document));
            }
        }
        PdfPCell houseNumberCell = getHouseNumberCell(document);

        if (houseNumberCell != null) {
            tableForRightSide.addCell(houseNumberCell);
        }

        cell.addElement(tableForRightSide);
        return cell;
    }

    private PdfPCell getGoogleMapCell(Document document) {

        PdfPCell cell = null;

        com.itextpdf.text.Image imgMap;
        if (!reportPOJO.getGoogleMapSnapShotFilePath().isEmpty()) {
            File file = new File(reportPOJO.getGoogleMapSnapShotFilePath());
            if (file.exists()) {
                byte[] imgResized = resizeGoogleImage(reportPOJO.getGoogleMapSnapShotFilePath());
                if (imgResized != null) {
                    try {
                        imgMap = com.itextpdf.text.Image.getInstance(imgResized);
                        cell = new PdfPCell(imgMap, true);
                        cell.setPaddingTop(5f);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_TOP);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setFixedHeight((document.getPageSize().getHeight() / 2) - 130);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

            }

        }


        return cell;
    }

    private PdfPCell getHouseNumberCell(Document document) {


        PdfPCell cell = null;
        if (!originalReportPojo.getLabelArrayList().get(0).getHouseNumber().isEmpty()) {

            File file = new File(originalReportPojo.getLabelArrayList().get(0).getHouseNumber());
            if (file.exists()) {
                byte[] imageBytesResized;
                imageBytesResized = resizeImage(originalReportPojo.getLabelArrayList().get(0).getHouseNumber());
                com.itextpdf.text.Image img;
                try {
                    img = com.itextpdf.text.Image.getInstance(imageBytesResized);
                    cell = new PdfPCell();
                    cell.setPaddingTop(5f);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    cell.setBorder(Rectangle.NO_BORDER);
                    cell.setFixedHeight((document.getPageSize().getHeight() / 4) - 30);

                    PdfPTable innerTable = new PdfPTable(2);
                    innerTable.setWidthPercentage(100);

                    PdfPCell defaultCell = innerTable.getDefaultCell();
                    defaultCell.setBorder(PdfPCell.NO_BORDER);

                    PdfPCell innerCell = new PdfPCell(img, true);
                    innerCell.setPaddingLeft(0f);
                    innerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    innerCell.setVerticalAlignment(Element.ALIGN_BASELINE);
                    innerCell.setBorder(Rectangle.NO_BORDER);
                    innerCell.setFixedHeight((document.getPageSize().getHeight() / 4) - 30);

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

        return cell;
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
                break;
            default:
        }


        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
        return outStream.toByteArray();

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
                headerFont = new Font(Font.FontFamily.TIMES_ROMAN, fontSize, Font.BOLD, BaseColor.BLACK);

            } else {
                headerFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.HEADER_FONT_SIZE, Font.BOLD, BaseColor.GRAY);
            }
            footerFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.FOOTER_FONT_SIZE, Font.ITALIC);


        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {

            PdfContentByte cb = writer.getDirectContent();

            PdfPTable table = new PdfPTable(3);
            table.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.setTotalWidth(490f);
            table.setWidthPercentage(100);

            String imageUrl = CommonUtils.getImageLogoUrl(mContext);
            boolean logoFlag = false;
            boolean companyNameFlag = false;
            boolean companyAddressFlag = false;
            int count = 0;

            if (!CommonUtils.getImageLogoUrl(mContext).isEmpty()) {
                logoFlag = true;
            }
            if (!CommonUtils.getCompanyName(mContext).isEmpty()) {
                companyNameFlag = true;
            }
            if (!CommonUtils.getAddress(mContext).isEmpty()) {
                companyAddressFlag = true;
            }

            try {
                if (logoFlag && companyNameFlag && companyAddressFlag) {
                    table.setWidths(new float[]{0.3f, 2f, 2.7f});
                    count = 1;
                } else if (logoFlag && companyNameFlag && !companyAddressFlag) {
                    table.setWidths(new float[]{0.5f, 2f, 2.5f});
                    count = 2;
                } else if (logoFlag && !companyNameFlag && !companyAddressFlag) {
                    table.setWidths(new float[]{0.5f, 2f, 2.5f});
                    count = 3;
                } else if (!logoFlag && companyNameFlag && companyAddressFlag) {
                    table.setWidths(new float[]{2f, 0.3f, 2.7f});
                    count = 4;
                } else if (!logoFlag && companyNameFlag && !companyAddressFlag) {
                    table.setWidths(new float[]{2f, 0.3f, 2.7f});
                    count = 5;
                } else if (logoFlag && !companyNameFlag && companyAddressFlag) {
                    table.setWidths(new float[]{0.3f, 2f, 2.7f});
                    count = 6;
                } else if (!logoFlag && !companyNameFlag && !companyAddressFlag) {
                    table.setWidths(new float[]{0.3f, 2f, 2.7f});
                    count = 7;
                } else if (!logoFlag && !companyNameFlag && companyAddressFlag) {
                    table.setWidths(new float[]{0.3f, 2f, 2.7f});
                    count = 8;
                }
            } catch (DocumentException e) {
                e.printStackTrace();
            }


            com.itextpdf.text.Image img = null;
            PdfPCell imageLogoCell = null;

            if (!imageUrl.isEmpty()) {
                File logoFile = new File(imageUrl);
                if (logoFile.exists()) {
                    try {
                        img = com.itextpdf.text.Image.getInstance(resizeImageLogo(imageUrl));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BadElementException e) {
                        e.printStackTrace();
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    if (img != null) {
                        imageLogoCell = new PdfPCell(img, true);
                        imageLogoCell.setPaddingTop(10f);
                        imageLogoCell.setPaddingBottom(5f);
                        imageLogoCell.setBorderWidthLeft(0);
                        imageLogoCell.setBorderWidthRight(0);
                        imageLogoCell.setBorderWidthTop(0);
                        imageLogoCell.setBorderColor(BaseColor.GRAY);
                        imageLogoCell.setFixedHeight(50f);
                        img.scaleToFit(35, 35);

                    }

                } else {
                    imageLogoCell = new PdfPCell();
                    try {
                        table.setWidths(new float[]{0.0f, 2.3f, 2.7f});
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                    imageLogoCell.setBorderWidthLeft(0);
                    imageLogoCell.setBorderWidthRight(0);
                    imageLogoCell.setBorderWidthTop(0);
                    imageLogoCell.setFixedHeight(50f);
                }

            }


            //Company Name
            Font headerCompanyNameFont = new Font(Font.FontFamily.TIMES_ROMAN, Constants.FOOTER_FONT_SIZE, Font.ITALIC);
            String companyName = CommonUtils.getCompanyName(mContext);

            PdfPCell companyNameCell = new PdfPCell(new Phrase(companyName, headerCompanyNameFont));
            companyNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            companyNameCell.setBorderWidthLeft(0);
            companyNameCell.setBorderWidthRight(0);
            companyNameCell.setBorderWidthTop(0);
            companyNameCell.setFixedHeight(50f);
            companyNameCell.setPaddingTop(20f);
            companyNameCell.setBorderColor(BaseColor.GRAY);


            //Company Address
            PdfPCell companyAddressCell = new PdfPCell(new Phrase(CommonUtils.getAddress(mContext), headerCompanyNameFont));
            companyAddressCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            companyAddressCell.setPaddingTop(20f);
            companyAddressCell.setBorderWidthLeft(0);
            companyAddressCell.setBorderWidthRight(0);
            companyAddressCell.setBorderWidthTop(0);
            companyAddressCell.setFixedHeight(50f);
            companyAddressCell.setBorderColor(BaseColor.GRAY);


            //Page header
            PdfPCell pageHeaderCell = new PdfPCell(new Phrase(reportTitle, headerFont));
            pageHeaderCell.setBorderWidthLeft(0);
            pageHeaderCell.setBorderWidthRight(0);
            pageHeaderCell.setBorderWidthTop(0);
            pageHeaderCell.setBorderWidthBottom(0);
            pageHeaderCell.setHorizontalAlignment(Element.ALIGN_CENTER);


            //Empty cell
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setFixedHeight(50f);
            emptyCell.setBorderWidthTop(0);
            emptyCell.setBorderWidthLeft(0);
            emptyCell.setBorderWidthRight(0);
            emptyCell.setBorderColor(BaseColor.GRAY);

            //Add all cell to table
            if (count == 1) {
                table.addCell(imageLogoCell);
                table.addCell(companyNameCell);
                table.addCell(companyAddressCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            } else if (count == 2) {
                table.addCell(imageLogoCell);
                table.addCell(companyNameCell);
                table.addCell(emptyCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            } else if (count == 3) {
                table.addCell(imageLogoCell);
                table.addCell(emptyCell);
                table.addCell(emptyCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            } else if (count == 4) {
                table.addCell(companyNameCell);
                table.addCell(emptyCell);
                table.addCell(companyAddressCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            } else if (count == 5) {
                table.addCell(companyNameCell);
                table.addCell(emptyCell);
                table.addCell(emptyCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            } else if (count == 6) {
                table.addCell(imageLogoCell);
                table.addCell(emptyCell);
                table.addCell(companyAddressCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            } else if (count == 7) {
                table.addCell(emptyCell);
                table.addCell(emptyCell);
                table.addCell(emptyCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            } else if (count == 8) {
                table.addCell(emptyCell);
                table.addCell(emptyCell);
                table.addCell(companyAddressCell);
                pageHeaderCell.setColspan(3);
                table.addCell(pageHeaderCell);
            }


            //Add Header table
            table.writeSelectedRows(0, -1, 50, 842, cb);


            cb.setColorStroke(BaseColor.LIGHT_GRAY);
            cb.moveTo(50, document.bottom());
            cb.lineTo(document.getPageSize().getWidth() - 50, document.bottom());
            cb.stroke();

            Phrase footer = new Phrase(reportPOJO.getClaimNumber() + " | " + reportPOJO.getInsuredName(), footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                    footer,
                    (document.left()),
                    document.bottom() - 20, 0);


            Phrase footerPageNumber = new Phrase("Page " + writer.getPageNumber() + "", footerFont);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                    footerPageNumber,
                    (document.right()),
                    document.bottom() - 20, 0);


        }
    }
}