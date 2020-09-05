package com.yonyou.common.utils.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.yonyou.common.utils.logs.LogerNcc;

import org.json.JSONArray;
import org.json.JSONObject;

/*
 * @功能:数据库工具类: 执行增删改查操作
 * @Date 2:31 PM 2020/7/14
 * @Author zhangg
 **/
public class DBUtil {
	
	public static SQLiteDatabase getWriteDB(Context context) {
		return new DBHelper(context).getWritableDatabase();
	}
	
	public static SQLiteDatabase getReadDB(Context context) {
		return new DBHelper(context).getReadableDatabase();
	}
	
	/**
	 * 执行sql : 增删改操作
	 *
	 * @param context
	 * @param sql
	 */
	public static void exeSQL(Context context, String sql) {
		if (TextUtils.isEmpty(sql)) {
			return;
		}
		// "INSERT INTO student ('name', 'gender') VALUES ('name', 88) "
		// "UPDATE student SET gender = 22 WHERE number = '201804081705' "
		// "delete from student where number = '201804081705' "
		String tempSql = sql.replaceAll(" ", "");
		if (tempSql.toLowerCase().contains("createdatabase")) { // 是创建数据库语句的时候,不创建,不允许自建数据库
			return;
		}
		getWriteDB(context).execSQL(sql);
	}
	
	/**
	 * 根据sql查询数据
	 *
	 * @param context
	 * @param sql     查询sql
	 * @return 数组(json)
	 */
	public static JSONArray queryDataBySQL(Context context, String sql) {
		JSONArray array = new JSONArray();
		SQLiteDatabase writeDB = DBUtil.getWriteDB(context);
		Cursor cursor = writeDB.rawQuery(sql, null);
		try {
			String[] columnNames = cursor.getColumnNames();
			while (cursor != null && cursor.moveToNext()) {
				JSONObject object = new JSONObject();
				for (String columnName : columnNames) {
					int columnIndex = cursor.getColumnIndex(columnName);
					int type = cursor.getType(columnIndex);
					if (Cursor.FIELD_TYPE_INTEGER == type) {
						int string = cursor.getInt(columnIndex);
						object.put(columnName, string);
					} else if (Cursor.FIELD_TYPE_FLOAT == type) {
						float string = cursor.getFloat(columnIndex);
						object.put(columnName, string);
					} else if (Cursor.FIELD_TYPE_BLOB == type) {
						byte[] string = cursor.getBlob(columnIndex);
						object.put(columnName, new String(string));
					} else if (Cursor.FIELD_TYPE_NULL == type) {
						String string = cursor.getString(columnIndex);
						object.put(columnName, string);
					} else if (Cursor.FIELD_TYPE_STRING == type) {
						String string = cursor.getString(columnIndex);
						object.put(columnName, string);
					} else {
						String string = cursor.getString(columnIndex);
						object.put(columnName, string);
					}
				}
				array.put(object);
				//        LogerNcc.e(array);
			}
		} catch (Exception e) {
			LogerNcc.e(e);
			e.printStackTrace();
		}
		return array;
	}
}
