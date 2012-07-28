package com.AIC.QRCodeScanner;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.Cursor;
import android.content.ContentValues;


class QueryHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME="querylist.db"; 
	private static final int SCHEMA_VERSION=1;
	
	public QueryHelper(Context context) {
	    super(context, DATABASE_NAME, null, SCHEMA_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE querydata (_id INTEGER PRIMARY KEY AUTOINCREMENT, querydata TEXT);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// no-op, since will not be called until 2nd schema
		// version exists
	}
	
	public void insert(String query) {
		ContentValues cv=new ContentValues();
		cv.put("name", query);
		
		getWritableDatabase().insert("restaurants", "name", cv); 
	}
	
	public Cursor getAll() { 
		return(getReadableDatabase().rawQuery("SELECT _id, name",null));
	}
		
	public String getName(Cursor c) { 
		return(c.getString(1));
	}

}
