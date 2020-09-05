package com.yonyou.plugins.translatevoice;

import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

public class TranslateVoiceApiInvoker implements IApiInvoker {
	private static final String TRANSLATE_VOICE = "translateVoice";
	
	@Override
	public String call(String apiname, final MTLArgs args) throws MTLException {
		switch (apiname) {
			case TRANSLATE_VOICE:
				MTLTranslateVoice.translateVoice(args);
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
	
}
