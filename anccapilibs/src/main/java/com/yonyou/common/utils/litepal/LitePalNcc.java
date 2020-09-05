package com.yonyou.common.utils.litepal;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.yonyou.common.utils.logs.LogerNcc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.FluentQuery;
import org.litepal.LitePal;
import org.litepal.Operator;
import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;
import org.litepal.exceptions.DatabaseGenerateException;
import org.litepal.exceptions.LitePalSupportException;
import org.litepal.util.BaseUtility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @功能: ncc操作数据库工具类
 * @Date  2020/8/6;
 * @Author zhangg
 **/
public class LitePalNcc {
	
	
	private static Map<String, List<Field>> classFieldsMap = new HashMap<String,
		  List<Field>>();
	
	public static void initialize(Application application) {
		LitePal.initialize(application);
	}
	
	public static SQLiteDatabase getDatabase() {
		return LitePal.getDatabase();
	}
	
	/*
	 * @功能: 只是简单的执行sql语句, 不返回数据
	 * @参数  sql 要执行的语句;
	 * @Date  2020/8/5;
	 * @Author zhangg
	 **/
	public static void executeSQL(String sql) {
		LitePal.getDatabase().execSQL(sql);
	}
	
	
	/**
	 * 查询全表数据
	 *
	 * @param modelClass
	 * @return
	 */
	public static <T> List<T> findAll(Class<T> modelClass) {
		List<T> resultList =
			  LitePal.findAll(modelClass);
		return resultList;
	}
	
	/**
	 * 根据where条件查询全表数据
	 *
	 * @param modelClass
	 * @param whereSql
	 * @return
	 */
	public static List<LitePalSupport> findAll(Class<LitePalSupport> modelClass,
	                                           String whereSql) {
		if (TextUtils.isEmpty(whereSql.trim())) {
			return findAll(modelClass);
		} else {
			List<LitePalSupport> resultList =
				  LitePal.where(whereSql).find(modelClass);
			return resultList;
		}
	}
	
	public static FluentQuery select(String... columns) {
		FluentQuery fluentQuery = Operator.select(columns);
		return fluentQuery;
	}
	
	public static FluentQuery where(String... conditions) {
		FluentQuery fluentQuery = Operator.where(conditions);
		return fluentQuery;
	}
	
	public static FluentQuery order(String column) {
		FluentQuery fluentQuery = Operator.order(column);
		return fluentQuery;
	}
	
	public static FluentQuery limit(int value) {
		FluentQuery fluentQuery = Operator.limit(value);
		return fluentQuery;
	}
	
	public static int delete(Class<?> modelClass, long id) {
		return Operator.delete(modelClass, id);
	}
	
	public static int deleteAll(Class<?> modelClass, String... conditions) {
		return Operator.deleteAll(modelClass, conditions);
	}
	
	protected static List<Field> getSupportedFields(String className) {
		List<Field> fieldList = classFieldsMap.get(className);
		if (fieldList == null) {
			List<Field> supportedFields = new ArrayList<Field>();
			Class<?> clazz;
			try {
				clazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new DatabaseGenerateException(DatabaseGenerateException.CLASS_NOT_FOUND + className);
			}
			recursiveSupportedFields(clazz, supportedFields);
			classFieldsMap.put(className, supportedFields);
			return supportedFields;
		}
		return fieldList;
	}
	
	private static void recursiveSupportedFields(Class<?> clazz,
	                                             List<Field> supportedFields) {
		if (clazz == LitePalSupport.class || clazz == Object.class) {
			return;
		}
		Field[] fields = clazz.getDeclaredFields();
		if (fields != null && fields.length > 0) {
			for (Field field : fields) {
				Column annotation = field.getAnnotation(Column.class);
				if (annotation != null && annotation.ignore()) {
					continue;
				}
				int modifiers = field.getModifiers();
				if (!Modifier.isStatic(modifiers)) {
					Class<?> fieldTypeClass = field.getType();
					String fieldType = fieldTypeClass.getName();
					if (BaseUtility.isFieldTypeSupported(fieldType)) {
						supportedFields.add(field);
					}
				}
			}
		}
		recursiveSupportedFields(clazz.getSuperclass(), supportedFields);
	}
	
	
	/**
	 * 获取单表数据
	 *
	 * @param sql
	 * @return author zhangg
	 */
	public static JSONArray getSelectDataBySQL(String sql) {
		if (sql == null) {
			return null;
		}
		if (!sql.trim().toLowerCase().startsWith("select")) {
			return null;
		}
		Cursor cursor = Operator.findBySQL(sql);
		JSONArray jsonArray = new JSONArray();
		String[] columnNames = cursor.getColumnNames();
		try {
			if (null != cursor) {
				cursor.moveToFirst();
			}
			do {
				JSONObject json = new JSONObject();
				for (String columnName : columnNames) {
					int columnIndex = cursor.getColumnIndex(columnName);  // index
					int type = cursor.getType(columnIndex);  // 根据index获取类型
					switch (type) {
						case Cursor.FIELD_TYPE_INTEGER:
							json.put(columnName, cursor.getInt(columnIndex));
							break;
						case Cursor.FIELD_TYPE_BLOB:
							json.put(columnName, cursor.getBlob(columnIndex));
							break;
						case Cursor.FIELD_TYPE_FLOAT:
							json.put(columnName, cursor.getFloat(columnIndex));
							break;
						case Cursor.FIELD_TYPE_NULL:
						case Cursor.FIELD_TYPE_STRING:
						default:
							json.put(columnName, cursor.getString(columnIndex));
							break;
					}
				}
				jsonArray.put(json);
			} while (cursor.moveToNext());
			
		} catch (Exception e) {
			LogerNcc.e(e);
			e.printStackTrace();
		}
		return jsonArray;
	}
	
	/**
	 * 根据sql 查询数据
	 *
	 * @param sql
	 * @param modelClass
	 * @return map<JsonObject></>
	 * @throws JSONException author zhangg
	 */
	public static JSONArray findBySQL(String sql, Class<?> modelClass) throws JSONException {
		Cursor cursor = Operator.findBySQL(sql);
		List<String> fieldList = new ArrayList<>();
		boolean isContainStar = true;  // 默认是包含*号的
		if (!sql.contains("*")) {  // 说明只是查询部分字段
			isContainStar = false;
			int index = sql.indexOf("from");
			String select = sql.substring(0, index).replace("select", "");
			fieldList = Arrays.asList(select.replaceAll(" ", "").split(","));
		}
		JSONArray array = new JSONArray();
		if (cursor.moveToFirst()) {
			List<Field> supportedFields = getSupportedFields(modelClass.getName());
			do {
				long id = cursor.getLong(cursor.getColumnIndexOrThrow("id"));
				JSONObject json = new JSONObject();
				json.put("id", id);
				for (Field field : supportedFields) {
					String name = field.getName();
					if (!isContainStar) {
						// 不包含此字段时不处理
						if (!fieldList.contains(name)) {
							continue;
						}
					}
					int columnIndex = cursor.getColumnIndex(name);
					String methodName = genGetColumnMethod(field.getType());
					if ("getBoolean".equals(methodName)) {
						json.put(name, cursor.getInt(columnIndex));
					} else if ("getString".equals(methodName) || "getChar".equals(methodName) ||
						  "getCharacter".equals(methodName)) {
						String string = cursor.getString(columnIndex);
						json.put(name, TextUtils.isEmpty(string) ? "" : string);
					} else if ("getDate".equals(methodName)) {
						json.put(name, cursor.getLong(columnIndex));
					} else if ("getInteger".equals(methodName) || "getInt".equals(methodName)) {
						json.put(name, cursor.getInt(columnIndex));
					} else if ("getLong".equals(methodName)) {
						json.put(name, cursor.getLong(columnIndex));
					} else {
						// 为了处理float,double显示问题,先转换为string,然后特殊处理
						String string = cursor.getString(columnIndex);
						json.put(name, TextUtils.isEmpty(string) ? "" : string);
					}
				}
				array.put(json);
			} while (cursor.moveToNext());
		}
		return array;
	}
	
	private static String genGetColumnMethod(Class<?> fieldType) {
		String typeName;
		if (fieldType.isPrimitive()) {
			typeName = BaseUtility.capitalize(fieldType.getName());
		} else {
			typeName = fieldType.getSimpleName();
		}
		String methodName = "get" + typeName;
		if ("getBoolean".equals(methodName)) {
			methodName = "getInt";
		} else if ("getChar".equals(methodName) || "getCharacter".equals(methodName)) {
			methodName = "getString";
		} else if ("getDate".equals(methodName)) {
			methodName = "getLong";
		} else if ("getInteger".equals(methodName)) {
			methodName = "getInt";
		}
		return methodName;
	}
	
	
	protected static Object createInstanceFromClass(Class<?> modelClass) {
		try {
			Constructor<?> constructor = findBestSuitConstructor(modelClass);
			return constructor.newInstance(getConstructorParams(modelClass, constructor));
		} catch (Exception e) {
			throw new LitePalSupportException(e.getMessage(), e);
		}
	}
	
	
	protected static Object[] getConstructorParams(Class<?> modelClass,
	                                               Constructor<?> constructor) {
		Class<?>[] paramTypes = constructor.getParameterTypes();
		Object[] params = new Object[paramTypes.length];
		for (int i = 0; i < paramTypes.length; i++) {
			params[i] = getInitParamValue(modelClass, paramTypes[i]);
		}
		return params;
	}
	
	
	protected static Constructor<?> findBestSuitConstructor(Class<?> modelClass) {
		Constructor<?>[] constructors = modelClass.getDeclaredConstructors();
		if (constructors.length == 0)
			throw new LitePalSupportException(modelClass.getName() + " has no " +
				  "constructor. " +
				  "LitePal " +
				  "could not handle it");
		Constructor<?> bestSuitConstructor = null;
		int minConstructorParamLength = Integer.MAX_VALUE;
		for (Constructor<?> constructor : constructors) {
			Class<?>[] types = constructor.getParameterTypes();
			boolean canUseThisConstructor = true; // under some conditions, constructor
			// can not use
			// for create instance
			for (Class<?> parameterType : types) {
				if (parameterType == modelClass
					  || parameterType.getName().startsWith("com.android") && parameterType.getName().endsWith("InstantReloadException")) {
					// we can not use this constructor
					canUseThisConstructor = false;
					break;
				}
			}
			if (canUseThisConstructor) { // we can use this constructor
				if (types.length < minConstructorParamLength) { // find the constructor
					// with least
					// parameter
					bestSuitConstructor = constructor;
					minConstructorParamLength = types.length;
				}
			}
		}
		if (bestSuitConstructor != null) {
			bestSuitConstructor.setAccessible(true);
		} else {
			StringBuilder builder = new StringBuilder(modelClass.getName()).append(" " +
				  "has no " +
				  "suited " +
				  "constructor to new instance. Constructors defined in class:");
			for (Constructor<?> constructor : constructors) {
				builder.append("\n").append(constructor.toString());
			}
			throw new LitePalSupportException(builder.toString());
		}
		return bestSuitConstructor;
	}
	
	
	private static Object getInitParamValue(Class<?> modelClass, Class<?> paramType) {
		String paramTypeName = paramType.getName();
		if ("boolean".equals(paramTypeName) || "java.lang.Boolean".equals(paramTypeName)) {
			return false;
		}
		if ("float".equals(paramTypeName) || "java.lang.Float".equals(paramTypeName)) {
			return 0f;
		}
		if ("double".equals(paramTypeName) || "java.lang.Double".equals(paramTypeName)) {
			return 0.0;
		}
		if ("int".equals(paramTypeName) || "java.lang.Integer".equals(paramTypeName)) {
			return 0;
		}
		if ("long".equals(paramTypeName) || "java.lang.Long".equals(paramTypeName)) {
			return 0L;
		}
		if ("short".equals(paramTypeName) || "java.lang.Short".equals(paramTypeName)) {
			return 0;
		}
		if ("char".equals(paramTypeName) || "java.lang.Character".equals(paramTypeName)) {
			return ' ';
		}
		if ("[B".equals(paramTypeName) || "[Ljava.lang.Byte;".equals(paramTypeName)) {
			return new byte[0];
		}
		if ("java.lang.String".equals(paramTypeName)) {
			return "";
		}
		if (modelClass == paramType) {
			return null;
		}
		return createInstanceFromClass(paramType);
	}
	
	
}
