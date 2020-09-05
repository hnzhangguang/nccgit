package com.yonyou.nccmob.appsetting.usersetting;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yonyou.nccmob.R;
import com.yonyou.nccmob.base.BaseActivity;


/*
 * @功能: 联系我们界面
 * @Date  2020/8/7;
 * @Author zhangg
 **/
public class ContactsWeActivity extends BaseActivity {
	
	
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts_we);
		
		TextView pagerTitle = findViewById(R.id.pagerTitle);
		pagerTitle.setText("联系我们");
		
		ImageView iv_back = findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		
		
	}
}
