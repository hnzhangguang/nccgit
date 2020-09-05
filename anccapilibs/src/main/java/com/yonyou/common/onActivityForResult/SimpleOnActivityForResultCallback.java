package com.yonyou.common.onActivityForResult;

import android.content.Intent;

/**
 * SimpleOnActivityForResultCallback
 */
public abstract class SimpleOnActivityForResultCallback implements OnActivityForResultCallback {
	@Override
	public void result(Integer resultCode, Intent data) {
	
	}
	
	@Override
	public void cancel(Intent data) {
	
	}
}
