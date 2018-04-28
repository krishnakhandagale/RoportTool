package com.electivechaos.checklistapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.ImageDetailsPOJO;
import com.electivechaos.checklistapp.pojo.ReportItemPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krishna on 11/20/17.
 */

public class ReportsListDBHelper extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 14;

    // Database Name
    private static final String DATABASE_NAME = "reports_list";

    // Reports list table name
    private static final String TABLE_REPORTS_LIST = "generated_reports";
//    private static final String TEMP_TABLE_REPORTS_LIST = "temp_generated_reports";

    // Reports list Table Columns names
    private static final String KEY_REPORT_ID = "report_id";
    private static final String KEY_REPORT_NAME = "report_name";
    private static final String KEY_REPORT_DESCRIPTION  = "report_description";
    private static final String KEY_CLIENT_NAME = "report_client_name";
    private static final String KEY_CLAIM_NUMBER = "report_claim_number";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_DATE_CREATED = "created_date";
    private static final String KEY_FILE_PATH = "file_path";


    // Reports list table name
    private static final String TABLE_REPORTS_IMAGE_DETAILS = "report_image_details";
    private static final String TABLE_REPORTS_ELEVATION_IMAGE_DETAILS = "report_elevation_image_details";
    private static final String TABLE_CATEGORY_LIST = "report_category_list";


    // private static final String TEMP_TABLE_REPORTS_IMAGE_DETAILS = "temp_report_image_details";
    // Reports list Table Columns names
    private static final String KEY_IMAGE_TITLE = "image_title";
    private static final String KEY_IMAGE_DESCRIPTION = "image_description";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_IMAGE_CATEGORY = "image_category";
    private static final String KEY_FK_REPORT_ID = "report_id_fk";


    private static final String KEY_CATEGORYID = "category_id"; // can be used in case we need
    private static final String KEY_CATEGORY_NAME = "category_name";
    private static final String KEY_CATEGORY_DESCRIPTION = "category_description";




    private static ReportsListDBHelper sInstance;

    public static synchronized ReportsListDBHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new ReportsListDBHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public ReportsListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REPORTS_LIST_TABLE = "CREATE TABLE " + TABLE_REPORTS_LIST + "("
                + KEY_REPORT_ID + " TEXT PRIMARY KEY," + KEY_REPORT_NAME + " TEXT,"+ KEY_REPORT_DESCRIPTION + " TEXT,"
                + KEY_CLIENT_NAME + " TEXT,"+ KEY_CLAIM_NUMBER + " TEXT," + KEY_ADDRESS + " TEXT,"+ KEY_DATE_CREATED + " TEXT,"+ KEY_FILE_PATH + " TEXT"+ " )";


        String CREATE_IMAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_REPORTS_IMAGE_DETAILS + "("
                + KEY_IMAGE_TITLE + " TEXT," + KEY_IMAGE_DESCRIPTION + " TEXT,"+ KEY_IMAGE_URL+ " TEXT," + KEY_IMAGE_CATEGORY+ " TEXT,"
                + KEY_FK_REPORT_ID + " TEXT,"+ "FOREIGN KEY("+ KEY_FK_REPORT_ID +") REFERENCES "+TABLE_REPORTS_LIST+"("+KEY_REPORT_ID+ ")"+ " ON DELETE CASCADE)";

        String CREATE_ELEVATION_IMAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS + "("
                + KEY_IMAGE_TITLE + " TEXT," + KEY_IMAGE_DESCRIPTION + " TEXT,"+ KEY_IMAGE_URL+ " TEXT," + KEY_IMAGE_CATEGORY+ " TEXT,"
                + KEY_FK_REPORT_ID + " TEXT,"+ "FOREIGN KEY("+ KEY_FK_REPORT_ID +") REFERENCES "+TABLE_REPORTS_LIST+"("+KEY_REPORT_ID+ ")"+ " ON DELETE CASCADE)";

        String CREATE_CATEGORY_DETAILS_TABLE = "CREATE TABLE " + TABLE_CATEGORY_LIST + "("
                +KEY_CATEGORYID +" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"+ KEY_CATEGORY_NAME + " TEXT,"+ KEY_CATEGORY_DESCRIPTION +" TEXT "+")";

        final String options[] = {"Elevations",
                "Roof",
                "Kitchen",
                "Living Room",
                "Den",
                "Foyer",
                "Bedroom #1",
                "Bedroom #2",
                "Bedroom #3",
                "Bedroom #4",
                "Bedroom #5",
                "Bedroom #6",
                "Bathroom",
                "Hall Bathroom",
                "Office",
                "Guest room",
                "Master Bathroom",
                "Master Bedroom",
                "Closet",
                "Master Closet",
                "Fence",
                "Shed",
                "Guest House",
                "Barn",
                "Detached Garage",
                "Underwriting Risk"};


        db.execSQL(CREATE_REPORTS_LIST_TABLE);
        db.execSQL(CREATE_IMAGE_DETAILS_TABLE);
        db.execSQL(CREATE_ELEVATION_IMAGE_DETAILS_TABLE);
        db.execSQL(CREATE_CATEGORY_DETAILS_TABLE);

        for(int i=0;i<options.length;i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_CATEGORY_NAME, options[i]);
            db.insert(TABLE_CATEGORY_LIST,null,contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_IMAGE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_LIST);
        // Create tables again
        onCreate(db);
    }

    public int deleteReportEntry(String reportId){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_REPORTS_LIST,"report_id=?",new String[]{reportId});
    }

    public int deleteCategoryEntry(String categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_CATEGORY_LIST,"category_id=?",new String[]{categoryId});
    }
     public long addCategory(Category category){
      SQLiteDatabase db = this.getWritableDatabase();
      ContentValues contentValues = new ContentValues();
      contentValues.put(KEY_CATEGORY_NAME, category.getCategoryName());
      contentValues.put(KEY_CATEGORY_DESCRIPTION, category.getCategoryDescription());
      return  db.insert(TABLE_CATEGORY_LIST,null,contentValues);
    }

    public int updateCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(KEY_CATEGORY_DESCRIPTION, category.getCategoryDescription());
        return  db.update(TABLE_CATEGORY_LIST, contentValues,KEY_CATEGORYID+"="+category.getCategoryId(),null);
    }
    public ArrayList<Category> getCategoryList(){

        ArrayList<Category> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_CATEGORY_LIST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Category category = new Category();
                category.setCategoryId(cursor.getInt(0));
                category.setCategoryName(cursor.getString(1));
                category.setCategoryDescription(cursor.getString(2));
                tempList.add(category);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }
    public void addReportEntry(ReportItemPOJO reportItemPOJO){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        String reportId = reportItemPOJO.getId();
        values.put(KEY_REPORT_ID, reportId); //reportId
        values.put(KEY_REPORT_NAME, reportItemPOJO.getReportTitle());
        values.put(KEY_REPORT_DESCRIPTION, reportItemPOJO.getReportDescription());
        values.put(KEY_CLIENT_NAME,reportItemPOJO.getClientName());
        values.put(KEY_CLAIM_NUMBER,reportItemPOJO.getClaimNumber());
        values.put(KEY_ADDRESS,reportItemPOJO.getAddress());
        values.put(KEY_DATE_CREATED,reportItemPOJO.getCreatedDate());
        values.put(KEY_FILE_PATH,reportItemPOJO.getFilePath());
        long insertIntoReportList = db.insert(TABLE_REPORTS_LIST,null, values);
        if(insertIntoReportList != -1){
            ArrayList<ImageDetailsPOJO> reportsImageList = reportItemPOJO.getSelectedImagesList();
            if(reportsImageList != null && reportsImageList.size() >0){
                for(int index =0; index< reportsImageList.size();index++){
                    ImageDetailsPOJO imageItem =  reportsImageList.get(index);
                    ContentValues imageEntry = new ContentValues();
                    imageEntry.put(KEY_IMAGE_TITLE,imageItem.getTitle());
                    imageEntry.put(KEY_IMAGE_DESCRIPTION,imageItem.getDescription());
                    imageEntry.put(KEY_IMAGE_URL,imageItem.getImageUrl());
                    imageEntry.put(KEY_IMAGE_CATEGORY,imageItem.getCategory());
                    imageEntry.put(KEY_FK_REPORT_ID,reportId);
                    db.insert(TABLE_REPORTS_IMAGE_DETAILS,null, imageEntry);
                }
            }

            ArrayList<ImageDetailsPOJO> reportsElevationImageList = reportItemPOJO.getSelectedElevationImagesList();
            if(reportsImageList != null && reportsElevationImageList.size() >0){
                for(int index =0; index< reportsElevationImageList.size();index++){
                    ImageDetailsPOJO imageItem =  reportsElevationImageList.get(index);
                    ContentValues imageEntry = new ContentValues();
                    imageEntry.put(KEY_IMAGE_TITLE,imageItem.getTitle());
                    imageEntry.put(KEY_IMAGE_DESCRIPTION,imageItem.getDescription());
                    imageEntry.put(KEY_IMAGE_URL,imageItem.getImageUrl());
                    imageEntry.put(KEY_IMAGE_CATEGORY,imageItem.getCategory());
                    imageEntry.put(KEY_FK_REPORT_ID,reportId);
                    db.insert(TABLE_REPORTS_ELEVATION_IMAGE_DETAILS,null, imageEntry);
                }
            }


        }


    }

    // Getting All Reports
    public List<ReportItemPOJO> getAllReports() {
        List<ReportItemPOJO> reportsList = new ArrayList<>();
        // Select All Query
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_REPORTS_LIST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String reportId = cursor.getString(0);
                String reportTitle = cursor.getString(1);
                String reportDescription =  cursor.getString(2);
                String clientName =  cursor.getString(3);
                String claimNumber =  cursor.getString(4);
                String address = cursor.getString(5);
                String createdDate = cursor.getString(6);
                String filePath = cursor.getString(7);
                ReportItemPOJO reportItem = new ReportItemPOJO();
                reportItem.setId(reportId);
                reportItem.setReportTitle(reportTitle);
                reportItem.setReportDescription(reportDescription);
                reportItem.setClientName(clientName);
                reportItem.setClaimNumber(claimNumber);
                reportItem.setAddress(address);
                reportItem.setCreatedDate(createdDate);
                reportItem.setFilePath(filePath);

                String selectQueryImagesTable = "SELECT  * FROM " + TABLE_REPORTS_IMAGE_DETAILS+" where report_id_fk = '"+reportId+"'";
                ArrayList<ImageDetailsPOJO> selectedImagesList = new ArrayList<>();
                Cursor imagesCursor = db.rawQuery(selectQueryImagesTable, null);
                if (imagesCursor.moveToFirst()) {
                    do {

                        ImageDetailsPOJO imgDetails = new ImageDetailsPOJO();
                        imgDetails.setTitle(imagesCursor.getString(0));
                        imgDetails.setDescription(imagesCursor.getString(1));
                        imgDetails.setImageUrl(imagesCursor.getString(2));
                        imgDetails.setCategory(imagesCursor.getString(3));
                        selectedImagesList.add(imgDetails);

                    }while (imagesCursor.moveToNext());
                }
                reportItem.setSelectedImagesList(selectedImagesList);

                String selectQueryElevationImagesTable = "SELECT  * FROM " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS+" where report_id_fk = '"+reportId+"'";
                ArrayList<ImageDetailsPOJO> selectedElevationImagesList = new ArrayList<>();
                Cursor imagesElevationCursor = db.rawQuery(selectQueryElevationImagesTable, null);
                if (imagesElevationCursor.moveToFirst()) {
                    do {

                        ImageDetailsPOJO imgDetails = new ImageDetailsPOJO();
                        imgDetails.setTitle(imagesElevationCursor.getString(0));
                        imgDetails.setDescription(imagesElevationCursor.getString(1));
                        imgDetails.setImageUrl(imagesElevationCursor.getString(2));
                        imgDetails.setCategory(imagesElevationCursor.getString(3));
                        selectedElevationImagesList.add(imgDetails);

                    }while (imagesElevationCursor.moveToNext());
                }



                reportItem.setSelectedElevationImagesList(selectedElevationImagesList);
                reportsList.add(reportItem);

            } while (cursor.moveToNext());
        }

        // return report list
        return reportsList;
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }
}

