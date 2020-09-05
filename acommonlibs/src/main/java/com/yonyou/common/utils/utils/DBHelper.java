package com.yonyou.common.utils.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public static final String TABLE_NAME = "student";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_NUMBER = "number";
	public static final String COLUMN_SCORE = "score";
	public static final String[] TABLE_COLUMNS = {
		  COLUMN_ID, COLUMN_NAME, COLUMN_GENDER, COLUMN_NUMBER, COLUMN_SCORE
	};
	private static final String TAG = "mmmm";
	private static final String DATABASE_NAME = "nccmobdatabase.db";
	private static final int DATABASE_VERSION = 4;
	
	public DBHelper(Context context) {
		this(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	
	public DBHelper(
		  Context context,
		  String name,
		  SQLiteDatabase.CursorFactory factory,
		  int version,
		  DatabaseErrorHandler errorHandler) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION, errorHandler);
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		String sql =
			  "CREATE TABLE IF NOT EXISTS "
				    + TABLE_NAME
				    + " ( "
				    + COLUMN_ID
				    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				    + COLUMN_NAME
				    + " TEXT,"
				    + COLUMN_GENDER
				    + " INTEGER, "
				    + COLUMN_NUMBER
				    + " TEXT, "
				    + COLUMN_SCORE
				    + " INTEGER)";
		
		database.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
	}
}
