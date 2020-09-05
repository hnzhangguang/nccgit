package com.yonyou.plugins.window;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yonyou.ancclibs.R;


public class MTLAlertDialog extends Dialog implements View.OnClickListener {
	
	private String message;
	private boolean cancelable = true;
	private TextView tvMessage;
	private Button btnConfirm;
	
	
	/**
	 * @param context
	 * @param message
	 */
	public MTLAlertDialog(Context context, String message) {
		super(context, R.style.MyDialog);
		this.message = message;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mtl_alert_dialog);
		//alertDialog是否可以点击外围消失
		setCanceledOnTouchOutside(cancelable);
		setCancelable(cancelable);
		tvMessage = findViewById(R.id.tv_dialog_message);
		btnConfirm = findViewById(R.id.btn_confirm);
		btnConfirm.setOnClickListener(this);
		tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
		tvMessage.setText(message);
		WindowManager windowManager = this.getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (display.getWidth()); //设置宽度
		this.getWindow().setAttributes(lp);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_confirm) {
			if (this.isShowing()) {
				dismiss();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (this.isShowing()) {
			dismiss();
		}
	}
}
