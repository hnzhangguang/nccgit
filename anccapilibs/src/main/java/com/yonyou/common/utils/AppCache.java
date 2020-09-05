package com.yonyou.common.utils;

import java.lang.ref.SoftReference;
import java.util.HashMap;

/**
 * lgy
 */
public class AppCache {
	
	private static AppCache appCache = new AppCache();
	private static SoftReference softReference = new SoftReference(appCache);
	private HashMap<String, Object> mCache = new HashMap<>();
	
	public static AppCache getAppCache() {
		
		if (softReference.get() == null) {
			appCache = new AppCache();
			softReference = new SoftReference(appCache);
		}
		return (AppCache) softReference.get();
	}
	
	public Object getValue(String key) {
		if (mCache == null) {
			return null;
		}
		if (mCache.containsKey(key)) {
			return mCache.get(key);
		}
		return null;
	}
	
	public void setValue(String key, Object value) {
		if (mCache != null) {
			mCache.put(key, value);
		}
	}
	
	public void removeValue(String key) {
		if (mCache != null) {
			mCache.remove(key);
		}
	}

//    public void clear() {
//        if (mCache != null) {
//            mCache.clear();
//            mCache = null;
//        }
//        appCache = null;
//    }
}
