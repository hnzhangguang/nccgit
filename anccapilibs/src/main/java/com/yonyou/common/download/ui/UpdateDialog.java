package com.yonyou.common.download.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

import com.yonyou.ancclibs.R;
import com.yonyou.common.download.callback.UpdateDialogCallback;


public class UpdateDialog extends Dialog implements View.OnClickListener {
	
	private boolean cancelable = true;
	private Button cancel;
	private Button confirm;
	private CheckBox checkBox;
	private UpdateDialogCallback callback;
	private Context context;
	
	public UpdateDialog(Context context, UpdateDialogCallback callback) {
		super(context, R.style.UpdateDialog);
		this.callback = callback;
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mtl_update_dialog);
		//alertDialog是否可以点击外围消失
		setCanceledOnTouchOutside(cancelable);
		setCancelable(cancelable);
		cancel = findViewById(R.id.cancel);
		cancel.setOnClickListener(this);
		confirm = findViewById(R.id.confirm);
		confirm.setOnClickListener(this);
		checkBox = findViewById(R.id.update_ignore);
//        tvMessage = findViewById(R.id.tv_dialog_message);
//        tvMessage.setMovementMethod(ScrollingMovementMethod.getInstance());
//        tvMessage.setText(message);
		WindowManager windowManager = this.getWindow().getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (display.getWidth()); //设置宽度
		this.getWindow().setAttributes(lp);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.cancel) {
			callback.action(false, checkBox.isChecked());
			if (this.isShowing()) {
				dismiss();
			}
		} else if (id == R.id.confirm) {
			callback.action(true, checkBox.isChecked());
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
