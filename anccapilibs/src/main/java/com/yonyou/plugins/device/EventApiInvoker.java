package com.yonyou.plugins.device;

import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

public class EventApiInvoker implements IApiInvoker {
	
	public static final String BACK_BUTTON = "backButton";
	
	private static final String ADD_EVENT_LISTENER = "addEventListener";
	private static final String REMOVE_EVENT_LISTENER = "removeEventListener";
	
	public static boolean executeEvent(Integer key, String event) {
		MTLArgs eventArgs = EventCache.getEventCache().getEvent(key, event);
		if (eventArgs != null) {
			String callback = eventArgs.getString("eventCallback");
			eventArgs.success(event, true, callback);
			return true;
		}
		return false;
	}
	
	@Override
	public String call(String apiname, MTLArgs args) throws MTLException {
		switch (apiname) {
			case ADD_EVENT_LISTENER:
				addEventListener(args);
				return "";
			case REMOVE_EVENT_LISTENER:
				removeEventListener(args);
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
	
	private void addEventListener(MTLArgs args) {
		String event = args.getString("event");
		EventCache.getEventCache().setEvent(args.getContext().hashCode(), event, args);
	}
	
	private void removeEventListener(MTLArgs args) {
		String event = args.getString("event");
		EventCache.getEventCache().removeEvent(args.getContext().hashCode(), event);
	}
	
	
}
