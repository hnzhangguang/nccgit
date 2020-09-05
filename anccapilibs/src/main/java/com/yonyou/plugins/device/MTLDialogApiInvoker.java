package com.yonyou.plugins.device;

import com.yonyou.plugins.IApiInvoker;
import com.yonyou.plugins.MTLArgs;
import com.yonyou.plugins.MTLException;

public class MTLDialogApiInvoker implements IApiInvoker {
	
	private static final String CONFIRM = "confirm";
	
	@Override
	public String call(String apiname, MTLArgs args) throws MTLException {
		switch (apiname) {
			case CONFIRM:
				openMtlDialog(args);
				return "";
		}
		throw new MTLException(apiname + ": function not found");
	}
	
	private void openMtlDialog(MTLArgs args) {
		MTLDialog dialog = new MTLDialog(args);
		dialog.show();
	}
}
