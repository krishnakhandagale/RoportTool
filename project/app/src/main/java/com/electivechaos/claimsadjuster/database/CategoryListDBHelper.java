package com.electivechaos.claimsadjuster.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.electivechaos.claimsadjuster.pojo.BuildingTypePOJO;
import com.electivechaos.claimsadjuster.pojo.Category;
import com.electivechaos.claimsadjuster.pojo.CoveragePOJO;
import com.electivechaos.claimsadjuster.pojo.FoundationPOJO;
import com.electivechaos.claimsadjuster.pojo.ImageDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.Label;
import com.electivechaos.claimsadjuster.pojo.PerilPOJO;
import com.electivechaos.claimsadjuster.pojo.PropertyDetailsPOJO;
import com.electivechaos.claimsadjuster.pojo.ReportItemPOJO;
import com.electivechaos.claimsadjuster.pojo.ReportPOJO;
import com.electivechaos.claimsadjuster.pojo.RoofSystemPOJO;
import com.electivechaos.claimsadjuster.pojo.SidingPOJO;
import com.electivechaos.claimsadjuster.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class CategoryListDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 176;


    // Database Name
    private static final String DATABASE_NAME = "master_categories_list";

    // Database table name
    private static final String TABLE_REPORTS_LIST = "generated_reports";
    private static final String TABLE_MASTER_CATEGORY = "master_category";
    private static final String TABLE_CATEGORY_LABELS = "category_label";
    private static final String TABLE_PERIL = "peril";
    private static final String TABLE_COVERAGE = "coverage";
    private static final String TABLE_REPORTS_IMAGE_DETAILS = "report_image_details";
    private static final String TABLE_REPORTS_ELEVATION_IMAGE_DETAILS = "report_elevation_image_details";
    private static final String TABLE_PROPERTY_DETAILS = "property_details";
    private static final String TABLE_ROOF_SYSTEM = "roof_system_details";
    private static final String TABLE_SIDING = "siding_details";
    private static final String TABLE_FOUNDATION = "foundation_details";
    private static final String TABLE_BUILDING_TYPE = "building_type_details";



    //Roof system columns names
    private static final String KEY_ROOF_SYSTEM_ID = "roof_system_id";
    private static final String KEY_ROOF_SYSTEM_NAME= "name";

    //Siding columns names
    private static final String KEY_SIDING_ID = "siding_id";
    private static final String KEY_SIDING_NAME= "name";

    //Foundation columns names
    private static final String KEY_FOUNDATION_ID = "foundation_id";
    private static final String KEY_FOUNDATION_NAME= "name";

    //Building type columns names
    private static final String KEY_BUILDING_TYPE_ID = "building_type_id";
    private static final String KEY_BUILDING_NAME= "name";


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
    private static final String KEY_PERIL = "peril";
    private static final String KEY_LOCATION_SNAPSHOT ="location_snapshot";



    // Master Category list Table Columns names
    private static final String KEY_CATEGORY_ID = "_id";
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_DESCRIPTION  = "description";
    private static final String KEY_CATEGORY_COVERAGE_TYPE  = "coverage_type";


    // Label Table Column Names
    private static final String KEY_LABEL_ID = "label_id";
    private static final String KEY_LABEL_NAME = "name";
    private static final String KEY_LABEL_DESCRIPTION  = "description";
    private static final String KEY_LABEL_COVERAGE_TYPE  = "coverage_type";

    private static final String KEY_FK_LABEL_REPORT_ID  = "report_id_fk";
    private static final String KEY_LABEL_HOUSE_NUMBER  = "house_number";




    // Image Table Column Names
    private static final String KEY_IMAGE_ID= "image_id";
    private static final String KEY_IMAGE_TITLE = "image_title";
    private static final String KEY_IMAGE_DESCRIPTION = "image_description";
    private static final String KEY_IMAGE_URL = "image_url";
    private static final String KEY_IS_DAMAGE = "is_damage";
    private static final String KEY_IS_OVERVIEW = "is_overview";
    private static final String KEY_IS_POINT_OF_ORIGIN= "is_point_of_origin";
    private static final String KEY_IMAGE_COVERAGE_TYPE= "image_coverage_type";
    private static final String KEY_IMAGE_NAME= "image_name";
    private static final String KEY_IMAGE_DATE_TIME= "image_date_time";
    private static final String KEY_IMAGE_GEO_TAG= "image_geo_tag";



    //Elevation image table columns name
    private static final String KEY_ELEVATION_IMAGE_ID = "elevation_image_id";

    //Peril table columns
    private static final String KEY_PERIL_ID = "_id";
    private static final String KEY_PERIL_NAME = "name";
    private static final String KEY_PERIL_DESCRIPTION = "description";
    private static final String KEY_FK_LABEL_ID = "label_id_fk";//Peril table columns


    private static final String KEY_COVERAGE_ID = "_id";
    private static final String KEY_COVERAGE_NAME = "name";
    private static final String KEY_COVERAGE_DESCRIPTION = "description";


    //Property details table columns name
    private static final String KEY_PROPERTY_DATE = "property_date";
    private static final String KEY_SQUARE_FOOTAGE = "square_footage";
    private static final String KEY_ROOF_SYSTEM = "roof_system";
    private static final String KEY_SIDING = "siding";
    private static final String KEY_FOUNDATION = "foundation";
    private static final String KEY_BUILDING_TYPE = "building_type";
    private static final String KEY_FK_PROPERTY_REPORT_ID = "report_id_fk";

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
                + KEY_PERIL + " TEXT,"
                + KEY_LOCATION_SNAPSHOT + " TEXT"+")";


        String CREATE_CATEGORY_DETAILS_TABLE = "CREATE TABLE " + TABLE_MASTER_CATEGORY + "("
                +KEY_CATEGORY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_CATEGORY_NAME + " TEXT,"
                + KEY_CATEGORY_COVERAGE_TYPE + " TEXT,"
                + KEY_CATEGORY_DESCRIPTION +" TEXT "+")";

        String CATEGORY_LABELS_TABLE = "CREATE TABLE " + TABLE_CATEGORY_LABELS + "("
                + KEY_LABEL_ID + " TEXT PRIMARY KEY,"
                + KEY_LABEL_NAME + " TEXT,"
                + KEY_LABEL_DESCRIPTION + " TEXT,"

                + KEY_FK_LABEL_REPORT_ID + " TEXT,"

                +KEY_LABEL_HOUSE_NUMBER + " TEXT,"
                +KEY_LABEL_COVERAGE_TYPE + " TEXT,"
                + "FOREIGN KEY("+ KEY_FK_LABEL_REPORT_ID +") REFERENCES "+TABLE_REPORTS_LIST+"("+ KEY_REPORT_ID +")"+ " ON DELETE CASCADE)";

        String CREATE_PERIL_TABLE = "CREATE TABLE "
                + TABLE_PERIL + "("
                + KEY_PERIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_PERIL_NAME + " TEXT,"
                + KEY_PERIL_DESCRIPTION + " TEXT,"+ " CONSTRAINT uc_peril UNIQUE ("+KEY_PERIL_NAME+" COLLATE NOCASE), CHECK("+KEY_PERIL_NAME+"<> '')"+")";


        String CREATE_COVERAGE_TABLE = "CREATE TABLE "
                + TABLE_COVERAGE + "("
                + KEY_COVERAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_COVERAGE_NAME + " TEXT,"
                + KEY_COVERAGE_DESCRIPTION + " TEXT,"+ " CONSTRAINT uc_coverage UNIQUE ("+KEY_COVERAGE_NAME+" COLLATE NOCASE), CHECK("+KEY_COVERAGE_NAME+"<> '')"+")";


        String CREATE_IMAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_REPORTS_IMAGE_DETAILS + "("
                + KEY_IMAGE_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE_TITLE + " TEXT,"
                + KEY_IMAGE_DESCRIPTION + " TEXT,"

                + KEY_IMAGE_URL+ " TEXT,"
                + KEY_IS_DAMAGE+ " BOOLEAN,"
                + KEY_IS_OVERVIEW+ " BOOLEAN,"
                + KEY_FK_LABEL_ID+ " TEXT,"
                + KEY_IS_POINT_OF_ORIGIN+ " BOOLEAN,"
                + KEY_IMAGE_COVERAGE_TYPE+ " TEXT,"
                + KEY_IMAGE_NAME+ " TEXT,"
                + KEY_IMAGE_DATE_TIME+ " TEXT,"
                + KEY_IMAGE_GEO_TAG+ " TEXT,"
                + "FOREIGN KEY("+ KEY_FK_LABEL_ID +") REFERENCES "+TABLE_CATEGORY_LABELS+"("+KEY_LABEL_ID+ ")"+ " ON DELETE CASCADE )";


        String CREATE_ELEVATION_IMAGE_DETAILS_TABLE = "CREATE TABLE " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS + "("
                + KEY_ELEVATION_IMAGE_ID + " TEXT PRIMARY KEY,"
                + KEY_IMAGE_TITLE + " TEXT,"
                + KEY_IMAGE_DESCRIPTION + " TEXT,"
                + KEY_IMAGE_URL+ " TEXT,"
                + KEY_FK_LABEL_ID+ " TEXT,"

                + "FOREIGN KEY("+ KEY_FK_LABEL_ID +") REFERENCES "+TABLE_CATEGORY_LABELS+"("+KEY_LABEL_ID+ ")"+ " ON DELETE CASCADE )";

        String CREATE_PROPERTY_DETAILS_TABLE = "CREATE TABLE " + TABLE_PROPERTY_DETAILS + "("
                + KEY_PROPERTY_DATE + " TEXT,"
                + KEY_SQUARE_FOOTAGE+ " TEXT,"
                + KEY_ROOF_SYSTEM+ " TEXT,"
                + KEY_SIDING+ " TEXT,"
                + KEY_FOUNDATION+ " TEXT,"
                + KEY_BUILDING_TYPE+ " TEXT,"
                + KEY_FK_PROPERTY_REPORT_ID+ " TEXT,"
                + "FOREIGN KEY("+ KEY_FK_PROPERTY_REPORT_ID +") REFERENCES "+TABLE_REPORTS_LIST+"("+ KEY_REPORT_ID +")"+ " ON DELETE CASCADE)";


        String CREATE_ROOF_SYSTEM_TABLE = "CREATE TABLE "
                + TABLE_ROOF_SYSTEM + "("
                + KEY_ROOF_SYSTEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_ROOF_SYSTEM_NAME + " TEXT, CONSTRAINT uc_roof UNIQUE ("+KEY_ROOF_SYSTEM_NAME+" COLLATE NOCASE), CHECK("+KEY_ROOF_SYSTEM_NAME+"<> '')"+")";

        String CREATE_SIDING_TABLE = "CREATE TABLE "
                + TABLE_SIDING + "("
                + KEY_SIDING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_SIDING_NAME + " TEXT, CONSTRAINT uc_siding UNIQUE ("+KEY_SIDING_NAME+" COLLATE NOCASE), CHECK("+KEY_SIDING_NAME+"<> '')"+")";

        String CREATE_FOUNDATION_TABLE = "CREATE TABLE "
                + TABLE_FOUNDATION + "("
                + KEY_FOUNDATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_FOUNDATION_NAME + " TEXT, CONSTRAINT uc_foundation UNIQUE ("+KEY_FOUNDATION_NAME+" COLLATE NOCASE), CHECK("+KEY_FOUNDATION_NAME+"<> '')"+")";

        String CREATE_BUILDING_TYPE_TABLE = "CREATE TABLE "
                + TABLE_BUILDING_TYPE + "("
                + KEY_BUILDING_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"
                + KEY_BUILDING_NAME+ " TEXT, CONSTRAINT uc_building UNIQUE ("+KEY_BUILDING_NAME+" COLLATE NOCASE), CHECK("+KEY_BUILDING_NAME+"<> '')"+")";


        final String categories[] = {
                "Risk Overview",
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
                "Underwriting Risk",
                "Exterior",
                "Front Elevation",
                "Back Elevation",
                "Left Elevation",
                "Right Elevation"

        };

        final String roofSystem[] = {
                "Composition Shingle 20 year",
                "Composition Shingle 25 year",
                "Composition Shingle 30 year",
                "Composition Shingle 40 year",
                "Composition Shingle 50 year",
                "Metal- Steel",
                "Metal- Aluminum",
                "Wood Shake",
                "Slate",
                "Concrete Tile",
                "Clay Tile",
                "Ceramic Tile",
                "Solar Tile",
        };


        final String siding[] = {
                "Vinyl",
                "Brick Veneer",
                "Wood",
                "Stucco",
                "Block",
                "Stone Veneer",
                "Aluminum",
                "Fiber Cement",
                "T-111",
                "Plywood"
        };

        final String foundation[] = {
                "Concrete Slab",
                "Pier and Beam",
                "None"
        };

        final String  buildingType[] = {
                "Detached S.F",
                "Attached S.F",
                "Single-Wide",
                "Double-Wide",
                "Modular",
                "Stick Built",
                "Commercial"
        };

        db.execSQL(CREATE_PERIL_TABLE);
        db.execSQL(CREATE_REPORTS_LIST_TABLE);

        db.execSQL(CREATE_CATEGORY_DETAILS_TABLE);
        db.execSQL(CREATE_IMAGE_DETAILS_TABLE);
        db.execSQL(CREATE_ELEVATION_IMAGE_DETAILS_TABLE);

        db.execSQL(CREATE_PROPERTY_DETAILS_TABLE);
        db.execSQL(CATEGORY_LABELS_TABLE);

        db.execSQL(CREATE_ROOF_SYSTEM_TABLE);
        db.execSQL(CREATE_SIDING_TABLE);
        db.execSQL(CREATE_FOUNDATION_TABLE);
        db.execSQL(CREATE_BUILDING_TYPE_TABLE);

        db.execSQL(CREATE_COVERAGE_TABLE);


        for (String category : categories) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_CATEGORY_NAME, category);
            contentValues.put(KEY_CATEGORY_DESCRIPTION, category);
            db.insert(TABLE_MASTER_CATEGORY, null, contentValues);
        }

        for (String aRoofSystem : roofSystem) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_ROOF_SYSTEM_NAME, aRoofSystem);
            db.insert(TABLE_ROOF_SYSTEM, null, contentValues);
        }

        for (String aSiding : siding) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_SIDING_NAME, aSiding);
            db.insert(TABLE_SIDING, null, contentValues);
        }

        for (String aFoundation : foundation) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_FOUNDATION_NAME, aFoundation);
            db.insert(TABLE_FOUNDATION, null, contentValues);
        }

        for (String aBuildingType : buildingType) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_BUILDING_NAME, aBuildingType);
            db.insert(TABLE_BUILDING_TYPE, null, contentValues);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASTER_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_LABELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERIL);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_ELEVATION_IMAGE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS_IMAGE_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTY_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOF_SYSTEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIDING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOUNDATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING_TYPE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COVERAGE);
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
        contentValues.put(KEY_CATEGORY_COVERAGE_TYPE, category.getCoverageType());
        return  db.insert(TABLE_MASTER_CATEGORY,null,contentValues);
    }

    public int updateCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_NAME, category.getCategoryName());
        contentValues.put(KEY_CATEGORY_DESCRIPTION, category.getCategoryDescription());
        contentValues.put(KEY_CATEGORY_COVERAGE_TYPE, category.getCoverageType());
        return  db.update(TABLE_MASTER_CATEGORY, contentValues,KEY_CATEGORY_ID+"="+category.getCategoryId(),null);
    }

    public ArrayList<Category> getCategoryList(){

        ArrayList<Category> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_MASTER_CATEGORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);

        if (cursor.moveToFirst()) {
            do {
                if(!cursor.getString(1).equals("Risk Overview")){
                    Category category = new Category();
                    category.setCategoryId(cursor.getInt(0));
                    category.setCategoryName(cursor.getString(1));
                    category.setCategoryDescription(cursor.getString(3));
                    category.setCoverageType(cursor.getString(2));
                    tempList.add(category);
                }
            } while (cursor.moveToNext());
        }
        return  tempList;
    }

    public long addRoofSystem(RoofSystemPOJO roofSystemPOJO){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_ROOF_SYSTEM_NAME, roofSystemPOJO.getName());
            return db.insertOrThrow(TABLE_ROOF_SYSTEM,null,contentValues);
        }catch (SQLiteConstraintException e) {
            return -111;
        }
    }

    public ArrayList<RoofSystemPOJO> getRoofSystemList(){

        ArrayList<RoofSystemPOJO> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_ROOF_SYSTEM;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);

        if (cursor.moveToFirst()) {
            do {

                RoofSystemPOJO roofSystemPOJO = new RoofSystemPOJO();
                roofSystemPOJO.setId(cursor.getInt(0));
                roofSystemPOJO.setName(cursor.getString(1));
                tempList.add(roofSystemPOJO);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }

    public long addSiding(SidingPOJO sidingPOJO){

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_SIDING_NAME, sidingPOJO.getName());
            return db.insertOrThrow(TABLE_SIDING,null,contentValues);
        }
       catch (SQLiteConstraintException e){
            return -111;
       }
    }

    public ArrayList<SidingPOJO> getSidingList(){

        ArrayList<SidingPOJO> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_SIDING;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);

        if (cursor.moveToFirst()) {
            do {

                SidingPOJO sidingPOJO = new SidingPOJO();
                sidingPOJO.setId(cursor.getInt(0));
                sidingPOJO.setName(cursor.getString(1));
                tempList.add(sidingPOJO);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }


    public long addFoundation(FoundationPOJO foundationPOJO){

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_FOUNDATION_NAME, foundationPOJO.getName());
            return db.insertOrThrow(TABLE_FOUNDATION,null,contentValues);
        }
        catch (SQLiteConstraintException e) {
            return -111;
        }
    }

    public ArrayList<FoundationPOJO> getFoundationList(){

        ArrayList<FoundationPOJO> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_FOUNDATION;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);

        if (cursor.moveToFirst()) {
            do {

                FoundationPOJO foundationPOJO = new FoundationPOJO();
                foundationPOJO.setId(cursor.getInt(0));
                foundationPOJO.setName(cursor.getString(1));
                tempList.add(foundationPOJO);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }

    public long addBuildingType(BuildingTypePOJO buildingTypePOJO){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_BUILDING_NAME, buildingTypePOJO.getName());
            return db.insertOrThrow(TABLE_BUILDING_TYPE,null,contentValues);
        }
        catch (SQLiteConstraintException e) {
            return -111;
        }
    }

    public ArrayList<BuildingTypePOJO> getBuildingTypeList(){

        ArrayList<BuildingTypePOJO> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_BUILDING_TYPE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);

        if (cursor.moveToFirst()) {
            do {

                BuildingTypePOJO buildingTypePOJO = new BuildingTypePOJO();
                buildingTypePOJO.setId(cursor.getInt(0));
                buildingTypePOJO.setName(cursor.getString(1));
                tempList.add(buildingTypePOJO);
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
        contentValues.put(KEY_LABEL_HOUSE_NUMBER,label.getHouseNumber());
        contentValues.put(KEY_LABEL_COVERAGE_TYPE,label.getCoverageType());
        db.insertOrThrow(TABLE_CATEGORY_LABELS,null,contentValues);
        return  id;
    }

    public Label getLabelFromCategoryDetails(String categoryLabel){

        Label label = new Label();
        String selectLabelDetailsQuery = "SELECT * FROM master_category WHERE name = '"+categoryLabel+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectLabelDetailsQuery, null);
        if (cursor.moveToFirst()) {
            do {
                label.setCategoryID(cursor.getInt(0));
                label.setName(cursor.getString(1));
                label.setDescription(cursor.getString(3));
                label.setCoverageType(cursor.getString(2));
            } while (cursor.moveToNext());
        }
        return  label;
    }



    public int updateLabel(Label label){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_LABEL_NAME, label.getName());
        contentValues.put(KEY_LABEL_DESCRIPTION, label.getDescription());
        contentValues.put(KEY_LABEL_HOUSE_NUMBER,label.getHouseNumber());
        contentValues.put(KEY_LABEL_COVERAGE_TYPE,label.getCoverageType());
        return  db.update(TABLE_CATEGORY_LABELS, contentValues,KEY_LABEL_ID+"="+label.getId(),null);
    }

    public int deletePeril(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_PERIL,"_id=?",new String[]{id});
    }

    public long addPeril(PerilPOJO perilPOJO){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_PERIL_NAME, perilPOJO.getName());
            contentValues.put(KEY_PERIL_DESCRIPTION, perilPOJO.getDescription());
            return  db.insertOrThrow(TABLE_PERIL,null,contentValues);
        }catch (SQLiteConstraintException exception){
            return -100;
        }

    }

    public int updatePeril(PerilPOJO perilPOJO){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_PERIL_NAME, perilPOJO.getName());
            contentValues.put(KEY_PERIL_DESCRIPTION, perilPOJO.getDescription());
            return  db.update(TABLE_PERIL, contentValues,KEY_PERIL_ID+"="+ perilPOJO.getID(),null);

        }catch (SQLiteConstraintException ex){
          return -100;
        }
    }

    public ArrayList<PerilPOJO> getPeril(){

        ArrayList<PerilPOJO> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_PERIL;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);
        if (cursor.moveToFirst()) {
            do {
                PerilPOJO perilPOJO = new PerilPOJO();
                perilPOJO.setID(cursor.getInt(0));
                perilPOJO.setName(cursor.getString(1));
                perilPOJO.setDescription(cursor.getString(2));
                tempList.add(perilPOJO);
            } while (cursor.moveToNext());
        }
        return  tempList;
    }




    public int deleteCoverage(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_COVERAGE,"_id=?",new String[]{id});
    }

    public long addCoverage(CoveragePOJO coveragePOJO){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_COVERAGE_NAME, coveragePOJO.getName());
            contentValues.put(KEY_COVERAGE_DESCRIPTION, coveragePOJO.getDescription());
            return  db.insertOrThrow(TABLE_COVERAGE,null,contentValues);
        }catch (SQLiteConstraintException exception){
            return -100;
        }

    }

    public int updateCoverage(CoveragePOJO coveragePOJO){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_COVERAGE_NAME, coveragePOJO.getName());
            contentValues.put(KEY_COVERAGE_DESCRIPTION, coveragePOJO.getDescription());
            return  db.update(TABLE_COVERAGE, contentValues,KEY_COVERAGE_ID+"="+ coveragePOJO.getID(),null);

        }catch (SQLiteConstraintException ex){
            return -100;
        }
    }

    public ArrayList<CoveragePOJO> getCoverageList(){

        ArrayList<CoveragePOJO> tempList = new ArrayList<>();
        String selectQueryCoverageTable = "SELECT  * FROM " + TABLE_COVERAGE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryCoverageTable, null);
        if (cursor.moveToFirst()) {
            do {
                CoveragePOJO coveragePOJO = new CoveragePOJO();
                coveragePOJO.setID(cursor.getInt(0));
                coveragePOJO.setName(cursor.getString(1));
                coveragePOJO.setDescription(cursor.getString(2));
                tempList.add(coveragePOJO);
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
        values.put(KEY_CLIENT_NAME, reportItemPOJO.getInsuredName());
        values.put(KEY_CLAIM_NUMBER, reportItemPOJO.getClaimNumber());
        values.put(KEY_DATE_CREATED, reportItemPOJO.getCreatedDate());
        values.put(KEY_FILE_PATH, reportItemPOJO.getFilePath());
        values.put(KEY_LOCATION_LAT, reportItemPOJO.getLocationLat());
        values.put(KEY_LOCATION_LONG, reportItemPOJO.getLocationLong());
        values.put(KEY_ADDRESS_LINE, reportItemPOJO.getAddressLine());
        values.put(KEY_PERIL, reportItemPOJO.getPerilPOJO().getName());
        values.put(KEY_LOCATION_SNAPSHOT, reportItemPOJO.getGoogleMapSnapShotFilePath());
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
                        imageEntry.put(KEY_IMAGE_COVERAGE_TYPE, imageItem.getCoverageTye());
                        imageEntry.put(KEY_FK_LABEL_ID, labelId);
                        imageEntry.put(KEY_IS_POINT_OF_ORIGIN, imageItem.isPointOfOrigin());
                        imageEntry.put(KEY_IMAGE_NAME, imageItem.getImageName());
                        imageEntry.put(KEY_IMAGE_DATE_TIME, imageItem.getImageDateTime());
                        imageEntry.put(KEY_IMAGE_GEO_TAG, imageItem.getImageGeoTag());
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
                        imageEntry.put(KEY_ELEVATION_IMAGE_ID, id);
                        imageEntry.put(KEY_IMAGE_TITLE, imageItem.getTitle());
                        imageEntry.put(KEY_IMAGE_DESCRIPTION, imageItem.getDescription());
                        imageEntry.put(KEY_IMAGE_URL, imageItem.getImageUrl());
                        imageEntry.put(KEY_FK_LABEL_ID, labelId);
                    long count = db.insertOrThrow(TABLE_REPORTS_ELEVATION_IMAGE_DETAILS, null, imageEntry);
                        Log.d("Error in insertion", String.valueOf(count));
                    }
                }
            }

            PropertyDetailsPOJO propertyDetailsPOJO =reportItemPOJO.getPropertyDetailsPOJO();
            ContentValues propertyEntry = new ContentValues();
            propertyEntry.put(KEY_PROPERTY_DATE, propertyDetailsPOJO.getPropertyDate());
            propertyEntry.put(KEY_SQUARE_FOOTAGE, propertyDetailsPOJO.getSquareFootage());
            propertyEntry.put(KEY_ROOF_SYSTEM, propertyDetailsPOJO.getRoofSystem());
            propertyEntry.put(KEY_SIDING, propertyDetailsPOJO.getSiding());
            propertyEntry.put(KEY_FOUNDATION, propertyDetailsPOJO.getFoundation());
            propertyEntry.put(KEY_BUILDING_TYPE, propertyDetailsPOJO.getBuildingType());
            propertyEntry.put(KEY_FK_PROPERTY_REPORT_ID, reportId);
            db.insert(TABLE_PROPERTY_DETAILS, null, propertyEntry);


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
                reportItemPOJO.setInsuredName(cursor.getString(3));
                reportItemPOJO.setClaimNumber(cursor.getString(4));
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

    public void updateFilePath(String filePath, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET file_path='"+filePath+"' WHERE report_id ='"+reportId+"'");

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
                reportPOJO.setInsuredName(c.getString(3));
                reportPOJO.setClaimNumber(c.getString(4));
                reportPOJO.setCreatedDate(c.getString(5));
                reportPOJO.setFilePath(c.getString(6));
                reportPOJO.setLocationLat(c.getString(7));
                reportPOJO.setLocationLong(c.getString(8));
                reportPOJO.setAddressLine(c.getString(9));
                PerilPOJO perilPOJO = new PerilPOJO();
                perilPOJO.setName(c.getString(10));
                reportPOJO.setPerilPOJO(perilPOJO);
                reportPOJO.setGoogleMapSnapShotFilePath(c.getString(11));
            }while (c.moveToNext());
        }

        Cursor cProperty = db.rawQuery("SELECT * FROM property_details WHERE report_id_fk= '"+id+"'", null);
        PropertyDetailsPOJO propertyPOJO = new PropertyDetailsPOJO();

        if (cProperty.moveToFirst()) {
            do {
                propertyPOJO.setPropertyDate(cProperty.getString(0));
                propertyPOJO.setSquareFootage(cProperty.getString(1));
                propertyPOJO.setRoofSystem(cProperty.getString(2));
                propertyPOJO.setSiding(cProperty.getString(3));
                propertyPOJO.setFoundation(cProperty.getString(4));
                propertyPOJO.setBuildingType(cProperty.getString(5));
                propertyPOJO.setReportId(cProperty.getString(6));

            }while (cProperty.moveToNext());
        }

        reportPOJO.setPropertyDetailsPOJO(propertyPOJO);

        Cursor cLabelList = db.rawQuery("SELECT * FROM category_label WHERE report_id_fk = '"+id+"'", null);
        ArrayList<Label> labelList=new ArrayList<>();

            if (cLabelList.moveToFirst()) {
            do {
                Label label =new Label();
                label.setId(cLabelList.getString(0));
                label.setName(cLabelList.getString(1));
                label.setDescription(cLabelList.getString(2));
                label.setReportId(cLabelList.getString(3));
                label.setHouseNumber(cLabelList.getString(4));
                label.setCoverageType(cLabelList.getString(5));
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
                            sImageDetailsPOJO.setPointOfOrigin(cSelectedImages.getString(7).equals("1"));
                            sImageDetailsPOJO.setCoverageTye(cSelectedImages.getString(8));
                            sImageDetailsPOJO.setImageName(cSelectedImages.getString(9));
                            sImageDetailsPOJO.setImageDateTime(cSelectedImages.getString(10));
                            sImageDetailsPOJO.setImageGeoTag(cSelectedImages.getString(11));
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

    public void updateReportDescription(String value, String reportId) {
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
    public void updateLocationLat(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET location_lat='"+value+"' WHERE report_id='"+reportId+"'");
    }
    public void updateLocationLong(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET location_lang='"+value+"' WHERE report_id='"+reportId+"'");
    }
    public void updateLocationSnapshot(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET location_snapshot='"+value+"' WHERE report_id='"+reportId+"'");
    }

    public void updatePropertyDate(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE property_details SET property_date='"+value+"' WHERE report_id_fk='"+reportId+"'");
    }

    public void updateSquareFootage(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE property_details SET square_footage='"+value+"' WHERE report_id_fk='"+reportId+"'");
    }

    public void updateRoofSystem(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE property_details SET roof_system='"+value+"' WHERE report_id_fk='"+reportId+"'");
    }

    public void updateSiding(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE property_details SET siding='"+value+"' WHERE report_id_fk='"+reportId+"'");
    }

    public void updateFoundation(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE property_details SET foundation='"+value+"' WHERE report_id_fk='"+reportId+"'");
    }

    public void updateBuildingType(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE property_details SET building_type='"+value+"' WHERE report_id_fk='"+reportId+"'");
    }

    public void updatePerilName(String value, String reportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE generated_reports SET peril='"+value+"' WHERE report_id='"+reportId+"'");
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
                    imageEntry.put(KEY_IS_POINT_OF_ORIGIN, imageItem.isPointOfOrigin());
                    imageEntry.put(KEY_IMAGE_COVERAGE_TYPE, imageItem.getCoverageTye());
                    imageEntry.put(KEY_IMAGE_NAME, imageItem.getImageName());
                    imageEntry.put(KEY_IMAGE_DATE_TIME, imageItem.getImageDateTime());
                    imageEntry.put(KEY_IMAGE_GEO_TAG, imageItem.getImageGeoTag());
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
