package com.yonyou.common.onActivityForResult;

import android.content.Intent;

/**
 * OnActivityForResultCallback
 */
public interface OnActivityForResultCallback {
	void result(Integer resultCode, Intent data);
	
	void success(Integer resultCode, Intent data);
	
	void cancel(Intent data);
}
