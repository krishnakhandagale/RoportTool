package com.electivechaos.checklistapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.electivechaos.checklistapp.pojo.Category;
import com.electivechaos.checklistapp.pojo.CauseOfLoss;
import com.electivechaos.checklistapp.pojo.Label;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class CategoryListDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 18;

    // Database Name
    private static final String DATABASE_NAME = "master_categories_list";

    private static final String TABLE_MASTER_CATEGORY = "master_category";
    private static final String TABLE_CATEGORY_LABELS = "category_label";
    private static final String TABLE_CAUSE_OF_LOSS = "cause_of_loss";

    // Master Category list Table Columns names
    private static final String KEY_CATEGORY_ID = "_id";
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_DESCRIPTION  = "description";

    private static final String KEY_LABEL_ID = "_id";
    private static final String KEY_LABEL_NAME = "name";
    private static final String KEY_LABEL_DESCRIPTION  = "description";
    private static final String KEY_FK_CATEGORY_ID = "category_id_fk";


    private static final String KEY_CAUSE_OF_LOSS_ID = "_id";
    private static final String KEY_CAUSE_OF_LOSS_NAME = "name";
    private static final String KEY_CAUSE_OF_LOSS_DESCRIPTION = "description";



    public CategoryListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORY_DETAILS_TABLE = "CREATE TABLE " + TABLE_MASTER_CATEGORY + "("
                +KEY_CATEGORY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"+ KEY_CATEGORY_NAME + " TEXT,"+ KEY_CATEGORY_DESCRIPTION +" TEXT "+")";

        String CATEGORY_LABELS_TABLE = "CREATE TABLE " + TABLE_CATEGORY_LABELS + "("
                + KEY_LABEL_NAME + " TEXT," + KEY_LABEL_DESCRIPTION + " TEXT," + KEY_FK_CATEGORY_ID + " INTEGER,"
                + KEY_LABEL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"+ "FOREIGN KEY("+ KEY_FK_CATEGORY_ID +") REFERENCES "+TABLE_MASTER_CATEGORY+"("+KEY_CATEGORY_ID+ ")"+ " ON DELETE CASCADE)";

        String CREATE_CAUSE_OF_LOSS_TABLE = "CREATE TABLE " + TABLE_CAUSE_OF_LOSS + "("
                + KEY_CAUSE_OF_LOSS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0," + KEY_CAUSE_OF_LOSS_NAME + " TEXT,"+ KEY_CAUSE_OF_LOSS_DESCRIPTION + " TEXT"+")";



        db.execSQL(CREATE_CATEGORY_DETAILS_TABLE);
        db.execSQL(CATEGORY_LABELS_TABLE);
        db.execSQL(CREATE_CAUSE_OF_LOSS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASTER_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY_LABELS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CAUSE_OF_LOSS);
        onCreate(db);
    }

    public int deleteCategoryEntry(String categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = KEY_CATEGORY_ID + "=?";
        return  db.delete(TABLE_MASTER_CATEGORY,whereClause,new String[]{categoryId});
    }

    public Category getCategory(String categoryId){
        Category category = null;
        SQLiteDatabase db = getReadableDatabase();
        String where = KEY_CATEGORY_ID + " = ?";
        String[] whereArgs = {categoryId};
        Cursor cursor = db.query(TABLE_MASTER_CATEGORY, null, where, whereArgs, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                // read column data
                category = new Category();
                category.setCategoryId(cursor.getInt(0));
                category.setCategoryName(cursor.getString(1));
                category.setCategoryDescription(cursor.getString(2));
            }
        } finally {
            cursor.close();
        }
        return category;
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
        contentValues.put(KEY_FK_CATEGORY_ID, label.getCategoryID());
        return  db.insert(TABLE_CATEGORY_LABELS,null,contentValues);
    }

    public int updateLabel(Label label){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_CATEGORY_NAME, label.getName());
        contentValues.put(KEY_CATEGORY_DESCRIPTION, label.getDescription());
        contentValues.put(KEY_FK_CATEGORY_ID, label.getCategoryID());
        return  db.update(TABLE_CATEGORY_LABELS, contentValues,KEY_LABEL_ID+"="+label.getID(),null);
    }
    public ArrayList<Label> getLabelList(){

        ArrayList<Label> tempList = new ArrayList<>();
        String selectQueryReportTable = "SELECT  * FROM " + TABLE_CATEGORY_LABELS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQueryReportTable, null);
        if (cursor.moveToFirst()) {
            do {

                Label label = new Label();
                label.setID(cursor.getInt(0));
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


    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }


}
