package com.electivechaos.claimsadjuster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.CauseOfLoss;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.ReportItemPOJO;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class CategoryListDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 57;


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
    private static final String KEY_ADDRESS_LINE = "address_line";
    private static final String KEY_CAUSE_OF_LOSS="cause_of_loss";



    // Master Category list Table Columns names
    private static final String KEY_CATEGORY_ID = "_id";
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_DESCRIPTION  = "description";


    // Label Table Column Names
    private static final String KEY_LABEL_ID = "label_id";
    private static final String KEY_LABEL_NAME = "name";
    private static final String KEY_LABEL_DESCRIPTION  = "description";

    private static final String KEY_FK_LABEL_REPORT_ID  = "report_id_fk";




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
    private static final String KEY_FK_LABEL_ID = "label_id_fk";

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
                + KEY_ADDRESS_LINE + " TEXT,"
                + KEY_CAUSE_OF_LOSS + " TEXT"+")";


        String CREATE_CATEGORY_DETAILS_TABLE = "CREATE TABLE " + TABLE_MASTER_CATEGORY + "("
                +KEY_CATEGORY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_CATEGORY_DESCRIPTION +" TEXT "+")";

        String CATEGORY_LABELS_TABLE = "CREATE TABLE " + TABLE_CATEGORY_LABELS + "("
                + KEY_LABEL_ID + " TEXT PRIMARY KEY,"
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
                + KEY_IMAGE_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE_TITLE + " TEXT,"
                + KEY_IMAGE_DESCRIPTION + " TEXT,"

                + KEY_IMAGE_URL+ " TEXT,"
                + KEY_IS_DAMAGE+ " BOOLEAN,"
                + KEY_IS_OVERVIEW+ " BOOLEAN,"
                + KEY_FK_LABEL_ID+ " TEXT,"
                + "FOREIGN KEY("+ KEY_FK_LABEL_ID +") REFERENCES "+TABLE_CATEGORY_LABELS+"("+KEY_LABEL_ID+ ")"+ " ON DELETE CASCADE )";


        String CREATE_ELEVATION_IMAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS + "("
                + KEY_ELEVATION_IMAGE_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE_TITLE + " TEXT,"
                + KEY_IMAGE_DESCRIPTION + " TEXT,"
                + KEY_IMAGE_URL+ " TEXT,"
                + KEY_FK_LABEL_ID+ " TEXT,"

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

    public String addLabel(Label label){
        SQLiteDatabase db = this.getWritableDatabase();
        String id =  CommonUtils.generateId();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LABEL_ID,id);
        contentValues.put(KEY_LABEL_NAME, label.getName());
        contentValues.put(KEY_LABEL_DESCRIPTION, label.getDescription());
        contentValues.put(KEY_FK_LABEL_REPORT_ID, label.getReportId());
        db.insert(TABLE_CATEGORY_LABELS,null,contentValues);
        return  id;
    }

    public int updateLabel(Label label){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_NAME, label.getName());
        contentValues.put(KEY_CATEGORY_DESCRIPTION, label.getDescription());
        return  db.update(TABLE_CATEGORY_LABELS, contentValues,KEY_LABEL_ID+"="+label.getId(),null);
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
        values.put(KEY_ADDRESS_LINE, reportItemPOJO.getAddressLine());
        values.put(KEY_CAUSE_OF_LOSS, reportItemPOJO.getCauseOfLoss());
        long insertIntoReportList = db.insert(TABLE_REPORTS_LIST, null, values);
        if (insertIntoReportList != -1) {
            ArrayList<Label> labelArrayList = reportItemPOJO.getLabelArrayList();
            Iterator itr = labelArrayList.iterator();
            while (itr.hasNext()) {
                Label label = (Label) itr.next();

                String labelId= addLabel(label);
                ArrayList<ImageDetailsPOJO> reportsImageList = label.getSelectedImages();

                if (reportsImageList != null && reportsImageList.size() > 0) {
                    for (int index = 0; index < reportsImageList.size(); index++) {
                        ImageDetailsPOJO imageItem = reportsImageList.get(index);
                        ContentValues imageEntry = new ContentValues();
                        String id = CommonUtils.generateId();
                        imageEntry.put(KEY_IMAGE_ID, id);
                        imageEntry.put(KEY_IMAGE_TITLE, imageItem.getTitle());
                        imageEntry.put(KEY_IMAGE_DESCRIPTION, imageItem.getDescription());
                        imageEntry.put(KEY_IMAGE_URL, imageItem.getImageUrl());
                        imageEntry.put(KEY_IS_DAMAGE, imageItem.isDamage());
                        imageEntry.put(KEY_IS_OVERVIEW, imageItem.isOverview());
                        imageEntry.put(KEY_FK_LABEL_ID, labelId);
                      long count=  db.insert(TABLE_REPORTS_IMAGE_DETAILS, null, imageEntry);
                      if(count!=-1) {
                          Log.d("Error in insertion", String.valueOf(count));
                      }
                    }
                }
                ArrayList<ImageDetailsPOJO> reportsElevationImageList = label.getSelectedElevationImages();
                if (reportsElevationImageList != null && reportsElevationImageList.size() > 0) {
                    for (int index = 0; index < reportsElevationImageList.size(); index++) {
                        ImageDetailsPOJO imageItem = reportsElevationImageList.get(index);
                        ContentValues imageEntry = new ContentValues();
                        String id = CommonUtils.generateId();
                        imageEntry.put(KEY_IMAGE_ID, id);
                        imageEntry.put(KEY_IMAGE_TITLE, imageItem.getTitle());
                        imageEntry.put(KEY_IMAGE_DESCRIPTION, imageItem.getDescription());
                        imageEntry.put(KEY_IMAGE_URL, imageItem.getImageUrl());
                        imageEntry.put(KEY_FK_LABEL_ID, labelId);
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

    public ReportPOJO getReportItem(String id){


        ReportPOJO reportPOJO = new ReportPOJO();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM generated_reports WHERE report_id = '"+id+"'", null);
        if (c.moveToFirst()) {
            do {
                reportPOJO.setId(c.getString(0));
                reportPOJO.setReportTitle(c.getString(1));
                reportPOJO.setReportDescription(c.getString(2));
                reportPOJO.setClientName(c.getString(3));
                reportPOJO.setClaimNumber(c.getString(4));
                reportPOJO.setCreatedDate(c.getString(5));
                reportPOJO.setFilePath(c.getString(6));
                reportPOJO.setLocationLat(c.getString(7));
                reportPOJO.setLocationLong(c.getString(8));
                reportPOJO.setAddressLine(c.getString(9));
                reportPOJO.setCauseOfLoss(c.getString(10));
            }while (c.moveToNext());
        }

        Cursor cLabelList = db.rawQuery("SELECT * FROM category_label WHERE report_id_fk = '"+id+"'", null);
        ArrayList<Label> labelList=new ArrayList<>();

            if (cLabelList.moveToFirst()) {
            do {
                Label label =new Label();
                label.setId(cLabelList.getString(0));
                label.setName(cLabelList.getString(1));
                label.setDescription(cLabelList.getString(2));
                label.setReportId(cLabelList.getString(3));

                Cursor cElevationImages = db.rawQuery("SELECT * FROM report_elevation_image_details  WHERE  label_id_fk = '"+cLabelList.getString(0)+"'", null);
                ArrayList<ImageDetailsPOJO> elevationImagesList=new ArrayList<>();

                if (cElevationImages.moveToFirst()) {
                    do {
                        ImageDetailsPOJO eImageDetailsPOJO = new ImageDetailsPOJO();
                        eImageDetailsPOJO.setTitle(cElevationImages.getString(1));
                        eImageDetailsPOJO.setDescription(cElevationImages.getString(2));
                        eImageDetailsPOJO.setImageUrl(cElevationImages.getString(3));
                        elevationImagesList.add(eImageDetailsPOJO);
                    } while (cElevationImages.moveToNext());
                }
                    label.setSelectedElevationImages(elevationImagesList);

                    Cursor cSelectedImages = db.rawQuery("SELECT * FROM report_image_details  WHERE  label_id_fk = '" + cLabelList.getString(0) + "'", null);
                    ArrayList<ImageDetailsPOJO> selectedImagesList = new ArrayList<>();

                    if (cSelectedImages.moveToFirst()) {
                        do {
                            ImageDetailsPOJO sImageDetailsPOJO = new ImageDetailsPOJO();
                            sImageDetailsPOJO.setTitle(cSelectedImages.getString(1));
                            sImageDetailsPOJO.setDescription(cSelectedImages.getString(2));
                            sImageDetailsPOJO.setImageUrl(cSelectedImages.getString(3));
                            sImageDetailsPOJO.setIsDamage(cSelectedImages.getString(4).equals("1"));
                            sImageDetailsPOJO.setOverview(cSelectedImages.getString(5).equals("1"));
                            selectedImagesList.add(sImageDetailsPOJO);

                        } while (cSelectedImages.moveToNext());
                    }

                    label.setSelectedImages(selectedImagesList);
                 labelList.add(label);

            } while (cLabelList.moveToNext());
        }
        reportPOJO.setLabelArrayList(labelList);

        return  reportPOJO;
    }

    public void updateReportTitle(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
       db.execSQL("UPDATE generated_reports SET report_name='"+value+"' WHERE report_id ='"+reportId+"'");
    }

    public void updateReportDecription(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET report_description='"+value+"' WHERE report_id='"+reportId+"'");
    }

    public void updateClientName(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET report_client_name='"+value+"' WHERE report_id='"+reportId+"'");
    }
    public void updateClaimNumber(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET report_claim_number='"+value+"' WHERE report_id='"+reportId+"'");
    }
    public void updateAddressLine(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET address_line='"+value+"' WHERE report_id='"+reportId+"'");
    }


    public void updateElevationImages(Label label) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPORTS_ELEVATION_IMAGE_DETAILS, KEY_FK_LABEL_ID + "=" + label.getId(), null);


            ArrayList<ImageDetailsPOJO> labelSelectedElevationImages = label.getSelectedElevationImages();
            if (labelSelectedElevationImages != null && labelSelectedElevationImages.size() > 0) {
                for (int index = 0; index < labelSelectedElevationImages.size(); index++) {
                    ImageDetailsPOJO imageItem = labelSelectedElevationImages.get(index);
                    ContentValues imageEntry = new ContentValues();
                    imageEntry.put(KEY_ELEVATION_IMAGE_ID, CommonUtils.generateId());
                    imageEntry.put(KEY_IMAGE_TITLE, imageItem.getTitle());
                    imageEntry.put(KEY_IMAGE_DESCRIPTION, imageItem.getDescription());
                    imageEntry.put(KEY_IMAGE_URL, imageItem.getImageUrl());
                    imageEntry.put(KEY_FK_LABEL_ID, label.getId());
                    db.insert(TABLE_REPORTS_ELEVATION_IMAGE_DETAILS, null, imageEntry);
                }
            }
    }

    public void updateSelectedImages(Label label){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_REPORTS_IMAGE_DETAILS, KEY_FK_LABEL_ID + "=" + label.getId(), null);



            ArrayList<ImageDetailsPOJO> labelSelectedImages = label.getSelectedImages();
            if (labelSelectedImages != null && labelSelectedImages.size() > 0) {
                for (int index = 0; index < labelSelectedImages.size(); index++) {
                    ImageDetailsPOJO imageItem = labelSelectedImages.get(index);
                    ContentValues imageEntry = new ContentValues();
                    imageEntry.put(KEY_IMAGE_ID, CommonUtils.generateId());
                    imageEntry.put(KEY_IMAGE_TITLE, imageItem.getTitle());
                    imageEntry.put(KEY_IMAGE_DESCRIPTION, imageItem.getDescription());
                    imageEntry.put(KEY_IMAGE_URL, imageItem.getImageUrl());
                    imageEntry.put(KEY_IS_DAMAGE, imageItem.isDamage());
                    imageEntry.put(KEY_IS_OVERVIEW, imageItem.isOverview());
                    imageEntry.put(KEY_FK_LABEL_ID, label.getId());
                    long count = db.insert(TABLE_REPORTS_IMAGE_DETAILS, null, imageEntry);
                    if (count != -1) {
                        Log.d("Error in insertion", String.valueOf(count));
                    }
                }
            }

    }


    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }


}
