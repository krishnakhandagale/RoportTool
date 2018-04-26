package com.electivechaos.checklistapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.electivechaos.checklistapp.Pojo.Category;

import java.util.ArrayList;

/**
 * Created by barkhasikka on 26/04/18.
 */

public class CategoryListDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 14;

    // Database Name
    private static final String DATABASE_NAME = "master_categories_list";

    private static final String TABLE_MASTER_CATEGORY = "master_category";

    // Master Category list Table Columns names
    private static final String KEY_CATEGORY_ID = "id";
    private static final String KEY_CATEGORY_NAME = "name";
    private static final String KEY_CATEGORY_DESCRIPTION  = "description";

    public CategoryListDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CATEGORY_DETAILS_TABLE = "CREATE TABLE " + TABLE_MASTER_CATEGORY + "("
                +KEY_CATEGORY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT DEFAULT 0,"+ KEY_CATEGORY_NAME + " TEXT,"+ KEY_CATEGORY_DESCRIPTION +" TEXT "+")";

        db.execSQL(CREATE_CATEGORY_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MASTER_CATEGORY);
        // Create tables again
        onCreate(db);
    }

    public int deleteCategoryEntry(String categoryId){
        SQLiteDatabase db = this.getWritableDatabase();
        return  db.delete(TABLE_MASTER_CATEGORY,"category_id=?",new String[]{categoryId});
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

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }
}
