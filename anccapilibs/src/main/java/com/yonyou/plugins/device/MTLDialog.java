package com.yonyou.plugins.device;

import android.app.Dialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yonyou.ancclibs.R;
import com.yonyou.plugins.MTLArgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MTLDialog extends Dialog implements View.OnClickListener {
	
	private String title;
	private String firstButtonText;
	private String secondButtonText;
	private String message;
	private boolean cancelable = true;
	private TextView tvMessage;
	private TextView titleView;
	private Button btnFirstView;
	private Button btnSecondView;
	private MTLArgs args;
	
	
	/**
	 * @param args
	 */
	public MTLDialog(MTLArgs args) {
		super(args.getContext(), R.style.MyDialog);
		this.args = args;
		JSONObject json = args.getJSONObject();
		title = json.optString("title", "提示");
		message = json.optString("message");
		JSONArray buttons = json.optJSONArray("buttons");
		if (buttons != null && buttons.length() > 1) {
			firstButtonText = buttons.optString(0);
			secondButtonText = buttons.optString(1);
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mtl_js_dialog);
		//alertDialog是否可以点击外围消失
		setCanceledOnTouchOutside(cancelable);
		setCancelable(cancelable);
		titleView = findViewById(R.id.mtl_dialog_title);
		titleView.setText(title);
		btnFirstView = findViewById(R.id.dialog_btn_first);
		btnFirstView.setOnClickListener(this);
		btnFirstView.setText(firstButtonText);
		btnSecondView = findViewById(R.id.dialog_btn_second);
		btnSecondView.setOnClickListener(this);
		btnSecondView.setText(secondButtonText);
		tvMessage = findViewById(R.id.tv_dialog_message);
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
		if (id == R.id.dialog_btn_first) {
			clickCallBack(0);
			if (this.isShowing()) {
				dismiss();
			}
		} else if (id == R.id.dialog_btn_second) {
			clickCallBack(1);
			if (this.isShowing()) {
				dismiss();
			}
		}
	}
	
	private void clickCallBack(int index) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("index", index);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		args.success(jsonObject);
		
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		if (this.isShowing()) {
			dismiss();
		}
	}
}
