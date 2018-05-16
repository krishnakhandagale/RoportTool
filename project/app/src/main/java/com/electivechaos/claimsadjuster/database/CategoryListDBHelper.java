package com.electivechaos.claimsadjuster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.CauseOfLoss;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ReportItemPOJO;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class CategoryListDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 38;


    // Database Name
    private static final String DATABASE_NAME = "master_categories_list";


    // Database table name
    private static final String TABLE_REPORTS_LIST = "generated_reports";
    private static final String TABLE_MASTER_CATEGORY = "master_category";
    private static final String TABLE_CATEGORY_LABELS = "category_label";
    private static final String TABLE_CAUSE_OF_LOSS = "cause_of_loss";
    private static final String TABLE_REPORTS_IMAGE_DETAILS = "report_image_details";
    private static final String TABLE_REPORTS_ELEVATION_IMAGE_DETAILS = "report_elevation_image_details";




    // Reports list Table Columns names
    private static final String KEY_REPORT_ID = "report_id";
    private static final String KEY_REPORT_NAME = "report_name";
    private static final String KEY_REPORT_DESCRIPTION  = "report_description";
    private static final String KEY_CLIENT_NAME = "report_client_name";
    private static final String KEY_CLAIM_NUMBER = "report_claim_number";
    private static final String KEY_DATE_CREATED = "created_date";
    private static final String KEY_FILE_PATH = "file_path";
    private static final String KEY_LOCATION_LAT = "location_lat";
    private static final String KEY_LOCATION_LONG = "location_lang";
    private static final String KEY_CAUSE_OF_LOSS="cause_of_loss";



    // Master Category list Table Columns names
    private static final String KEY_CATEGORY_ID = "_id";
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_DESCRIPTION  = "description";


    // Label Table Column Names
    private static final String KEY_LABEL_ID = "_id";
    private static final String KEY_LABEL_NAME = "name";
    private static final String KEY_LABEL_DESCRIPTION  = "description";

    private static final String KEY_FK_LABEL_REPORT_ID  = "report_id_fk";

    private static final String KEY_FK_LABEL_ID = "label_id_fk";


    // Image Table Column Names
    private static final String KEY_IMAGE_ID= "image_id";
    private static final String KEY_IMAGE_TITLE = "image_title";
    private static final String KEY_IMAGE_DESCRIPTION = "image_description";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_IS_DAMAGE = "is_damage";
    private static final String KEY_IS_OVERVIEW= "is_overview";





    //Elevation image table columns name
    private static final String KEY_ELEVATION_IMAGE_ID="elevation_image_id";

    private static final String KEY_CAUSE_OF_LOSS_ID = "_id";
    private static final String KEY_CAUSE_OF_LOSS_NAME = "name";
    private static final String KEY_CAUSE_OF_LOSS_DESCRIPTION = "description";

    private static CategoryListDBHelper categoryListDBHelperInstance;

    public static synchronized CategoryListDBHelper getInstance(Context context) {

        // don't accidentally leak an Activity's context.
        if (categoryListDBHelperInstance == null) {
            categoryListDBHelperInstance = new CategoryListDBHelper(context.getApplicationContext());
        }
        return categoryListDBHelperInstance;
    }



    public CategoryListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_REPORTS_LIST_TABLE = "CREATE TABLE " + TABLE_REPORTS_LIST + "("
                + KEY_REPORT_ID + " TEXT PRIMARY KEY,"
                + KEY_REPORT_NAME + " TEXT,"
                + KEY_REPORT_DESCRIPTION + " TEXT,"
                + KEY_CLIENT_NAME + " TEXT,"
                + KEY_CLAIM_NUMBER + " TEXT,"
                + KEY_DATE_CREATED + " TEXT,"
                + KEY_FILE_PATH + " TEXT,"
                + KEY_LOCATION_LAT + " TEXT,"
                + KEY_LOCATION_LONG + " TEXT,"
                + KEY_CAUSE_OF_LOSS + " TEXT"+")";


        String CREATE_CATEGORY_DETAILS_TABLE = "CREATE TABLE " + TABLE_MASTER_CATEGORY + "("
                +KEY_CATEGORY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_CATEGORY_DESCRIPTION +" TEXT "+")";

        String CATEGORY_LABELS_TABLE = "CREATE TABLE " + TABLE_CATEGORY_LABELS + "("
                + KEY_LABEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_LABEL_NAME + " TEXT,"
                + KEY_LABEL_DESCRIPTION + " TEXT,"

                + KEY_FK_LABEL_REPORT_ID + " TEXT,"

                + "FOREIGN KEY("+ KEY_FK_LABEL_REPORT_ID +") REFERENCES "+TABLE_REPORTS_LIST+"("+ KEY_REPORT_ID +")"+ " ON DELETE CASCADE)";

        String CREATE_CAUSE_OF_LOSS_TABLE = "CREATE TABLE "
                + TABLE_CAUSE_OF_LOSS + "("
                + KEY_CAUSE_OF_LOSS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_CAUSE_OF_LOSS_NAME + " TEXT,"
                + KEY_CAUSE_OF_LOSS_DESCRIPTION + " TEXT"+")";


        String CREATE_IMAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_REPORTS_IMAGE_DETAILS + "("
                + KEY_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_IMAGE_TITLE + " TEXT,"
                + KEY_IMAGE_DESCRIPTION + " TEXT,"

                + KEY_IMAGE_URL+ " TEXT,"
                + KEY_IS_DAMAGE+ " BOOLEAN,"
                + KEY_IS_OVERVIEW+ " BOOLEAN,"
                + KEY_FK_LABEL_ID+ " INTEGER,"
                + "FOREIGN KEY("+ KEY_FK_LABEL_ID +") REFERENCES "+TABLE_CATEGORY_LABELS+"("+KEY_LABEL_ID+ ")"+ " ON DELETE CASCADE )";


        String CREATE_ELEVATION_IMAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS + "("
                + KEY_ELEVATION_IMAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_IMAGE_TITLE + " TEXT,"
                + KEY_IMAGE_DESCRIPTION + " TEXT,"
                + KEY_IMAGE_URL+ " TEXT,"
                + KEY_FK_LABEL_ID+ " INTEGER,"

                + "FOREIGN KEY("+ KEY_FK_LABEL_ID +") REFERENCES "+TABLE_CATEGORY_LABELS+"("+KEY_LABEL_ID+ ")"+ " ON DELETE CASCADE )";;


        final String categories[] = {
                "Elevations",
                "Roof",
                "Kitchen",
                "Living Room",
                "Den",
                "Foyer",
                "Bedroom",
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
                "Underwriting Risk"
        };
        db.execSQL(CREATE_CAUSE_OF_LOSS_TABLE);
        db.execSQL(CREATE_REPORTS_LIST_TABLE);

        db.execSQL(CREATE_CATEGORY_DETAILS_TABLE);
        db.execSQL(CREATE_IMAGE_DETAILS_TABLE);
        db.execSQL(CREATE_ELEVATION_IMAGE_DETAILS_TABLE);

        db.execSQL(CATEGORY_LABELS_TABLE);



        for(int i=0;i<categories.length;i++){
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_CATEGORY_NAME, categories[i]);
            contentValues.put(KEY_CATEGORY_DESCRIPTION, categories[i]);
            db.insert(TABLE_MASTER_CATEGORY,null,contentValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASTER_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_LABELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAUSE_OF_LOSS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_IMAGE_DETAILS);
        onCreate(db);
    }

    public int deleteCategoryEntry(String categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_CATEGORY_ID + "=?";
        return  db.delete(TABLE_MASTER_CATEGORY,whereClause,new String[]{categoryId});
    }

    public long addCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(KEY_CATEGORY_DESCRIPTION, category.getCategoryDescription());
        return  db.insert(TABLE_MASTER_CATEGORY,null,contentValues);
    }

    public int updateCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(KEY_CATEGORY_DESCRIPTION, category.getCategoryDescription());
        return  db.update(TABLE_MASTER_CATEGORY, contentValues,KEY_CATEGORY_ID+"="+category.getCategoryId(),null);
    }

    public ArrayList<Category> getCategoryList(){

        ArrayList<Category> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_MASTER_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);

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

    public int deleteLabel(String labelID){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_LABEL_ID+"=?";
        return  db.delete(TABLE_CATEGORY_LABELS, whereClause,new String[]{labelID});
    }

    public long addLabel(Label label){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LABEL_NAME, label.getName());
        contentValues.put(KEY_LABEL_DESCRIPTION, label.getDescription());
        contentValues.put(KEY_FK_LABEL_REPORT_ID, label.getReportId());
        return  db.insert(TABLE_CATEGORY_LABELS,null,contentValues);
    }

    public int updateLabel(Label label){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_NAME, label.getName());
        contentValues.put(KEY_CATEGORY_DESCRIPTION, label.getDescription());
        return  db.update(TABLE_CATEGORY_LABELS, contentValues,KEY_LABEL_ID+"="+label.getId(),null);
    }
    public ArrayList<Label> getLabelList(){

        ArrayList<Label> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_CATEGORY_LABELS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);
        if (cursor.moveToFirst()) {
            do {

                Label label = new Label();
                label.setId(cursor.getInt(0));
                label.setName(cursor.getString(1));
                label.setDescription(cursor.getString(2));
                label.setCategoryID(cursor.getInt(3));
                tempList.add(label);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }

    public int deleteCauseOfLoss(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_CAUSE_OF_LOSS,"_id=?",new String[]{id});
    }
    public long addCauseOfLoss(CauseOfLoss causeOfLoss){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CAUSE_OF_LOSS_NAME, causeOfLoss.getName());
        contentValues.put(KEY_CAUSE_OF_LOSS_DESCRIPTION, causeOfLoss.getDescription());
        return  db.insert(TABLE_CAUSE_OF_LOSS,null,contentValues);
    }

    public int updateCauseOfLoss(CauseOfLoss causeOfLoss){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CAUSE_OF_LOSS_NAME, causeOfLoss.getName());
        contentValues.put(KEY_CAUSE_OF_LOSS_DESCRIPTION, causeOfLoss.getDescription());
        return  db.update(TABLE_CAUSE_OF_LOSS, contentValues,KEY_CAUSE_OF_LOSS_ID+"="+causeOfLoss.getID(),null);
    }

    public ArrayList<CauseOfLoss> getCauseOfLosses(){

        ArrayList<CauseOfLoss> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_CAUSE_OF_LOSS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);
        if (cursor.moveToFirst()) {
            do {
                CauseOfLoss causeOfLoss = new CauseOfLoss();
                causeOfLoss.setID(cursor.getInt(0));
                causeOfLoss.setName(cursor.getString(1));
                causeOfLoss.setDescription(cursor.getString(2));
                tempList.add(causeOfLoss);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }

    public void addReportEntry(ReportPOJO reportItemPOJO) {

        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues values = new ContentValues();
        String reportId = reportItemPOJO.getId();

        //Delete id any record exist and add it again instead of update
        deleteReportEntry(reportId);
        values.put(KEY_REPORT_ID, reportId);
        values.put(KEY_REPORT_NAME, reportItemPOJO.getReportTitle());
        values.put(KEY_REPORT_DESCRIPTION, reportItemPOJO.getReportDescription());
        values.put(KEY_CLIENT_NAME, reportItemPOJO.getClientName());
        values.put(KEY_CLAIM_NUMBER, reportItemPOJO.getClaimNumber());
        values.put(KEY_DATE_CREATED, reportItemPOJO.getCreatedDate());
        values.put(KEY_FILE_PATH, reportItemPOJO.getFilePath());
        values.put(KEY_LOCATION_LAT, reportItemPOJO.getLocationLat());
        values.put(KEY_LOCATION_LONG, reportItemPOJO.getLocationLong());
        values.put(KEY_CAUSE_OF_LOSS, reportItemPOJO.getCauseOfLoss());
        long insertIntoReportList = db.insert(TABLE_REPORTS_LIST, null, values);
        if (insertIntoReportList != -1) {
            ArrayList<Label> labelArrayList = reportItemPOJO.getLabelArrayList();
            Iterator itr = labelArrayList.iterator();
            while (itr.hasNext()) {
                Label label = (Label) itr.next();
                ArrayList<ImageDetailsPOJO> reportsImageList = label.getSelectedImages();
                if (reportsImageList != null && reportsImageList.size() > 0) {
                    for (int index = 0; index < reportsImageList.size(); index++) {
                        ImageDetailsPOJO imageItem = reportsImageList.get(index);
                        ContentValues imageEntry = new ContentValues();
                        imageEntry.put(KEY_IMAGE_TITLE, imageItem.getTitle());
                        imageEntry.put(KEY_IMAGE_DESCRIPTION, imageItem.getDescription());
                        imageEntry.put(KEY_IMAGE_URL, imageItem.getImageUrl());
                        imageEntry.put(KEY_IS_DAMAGE, imageItem.isDamage());
                        imageEntry.put(KEY_IS_OVERVIEW, imageItem.isOverview());
                        imageEntry.put(KEY_FK_LABEL_ID, label.getId());
                        db.insert(TABLE_REPORTS_IMAGE_DETAILS, null, imageEntry);
                    }
                }
                ArrayList<ImageDetailsPOJO> reportsElevationImageList = label.getSelectedElevationImages();
                if (reportsImageList != null && reportsElevationImageList.size() > 0) {
                    for (int index = 0; index < reportsElevationImageList.size(); index++) {
                        ImageDetailsPOJO imageItem = reportsElevationImageList.get(index);
                        ContentValues imageEntry = new ContentValues();
                        imageEntry.put(KEY_IMAGE_TITLE, imageItem.getTitle());
                        imageEntry.put(KEY_IMAGE_DESCRIPTION, imageItem.getDescription());
                        imageEntry.put(KEY_IMAGE_URL, imageItem.getImageUrl());
                        imageEntry.put(KEY_FK_LABEL_ID, label.getId());
                        db.insert(TABLE_REPORTS_ELEVATION_IMAGE_DETAILS, null, imageEntry);
                    }
                }
            }

        }
    }

    public ArrayList<ReportItemPOJO> getReports(){

        ArrayList<ReportItemPOJO> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_REPORTS_LIST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);
        if (cursor.moveToFirst()) {
            do {
                ReportItemPOJO reportItemPOJO = new ReportItemPOJO();
                reportItemPOJO.setId(cursor.getString(0));
                reportItemPOJO.setReportTitle(cursor.getString(1));
                reportItemPOJO.setReportDescription(cursor.getString(2));
                reportItemPOJO.setCreatedDate(cursor.getString(5));
                reportItemPOJO.setFilePath(cursor.getString(6));

                tempList.add(reportItemPOJO);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }


    public int deleteReportEntry(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_REPORTS_LIST,KEY_REPORT_ID+"=?",new String[]{id});
    }



    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }


}
