package com.yonyou.plugins.device;

import com.yonyou.plugins.MTLArgs;

import java.util.Hashtable;

/**
 * lgy
 */
public class EventCache {
	private static EventCache eventCache = new EventCache();
	private Hashtable<Integer, Hashtable<String, MTLArgs>> mCache = new Hashtable();
	
	public static EventCache getEventCache() {
		if (eventCache == null) {
			eventCache = new EventCache();
		}
		return eventCache;
	}
	
	public MTLArgs getEvent(Integer key, String event) {
		if (mCache != null && mCache.containsKey(key)) {
			Hashtable<String, MTLArgs> eventMap = mCache.get(key);
			if (eventMap != null && eventMap.containsKey(event)) {
				return eventMap.get(event);
			}
		}
		return null;
	}
	
	public Hashtable<String, MTLArgs> getEventMap(Integer key) {
		if (mCache != null && mCache.containsKey(key)) {
			Hashtable<String, MTLArgs> eventMap = mCache.get(key);
			return eventMap;
		}
		return null;
	}
	
	public void setEvent(Integer key, String event, MTLArgs args) {
		if (mCache != null) {
			Hashtable<String, MTLArgs> eventMap = getEventMap(key);
			if (eventMap == null) {
				eventMap = new Hashtable<>();
			}
			eventMap.put(event, args);
			mCache.put(key, eventMap);
		}
	}
	
	public void removeEvent(Integer key, String event) {
		if (mCache != null) {
			Hashtable<String, MTLArgs> eventMap = getEventMap(key);
			if (eventMap != null) {
				eventMap.remove(event);
			}
		}
	}
	
	public void clearEvent() {
		if (mCache != null) {
			mCache.clear();
			mCache = null;
		}
		eventCache = null;
	}
}
