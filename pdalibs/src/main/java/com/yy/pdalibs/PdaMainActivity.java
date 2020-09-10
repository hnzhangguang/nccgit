package com.yy.pdalibs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PdaMainActivity extends AppCompatActivity {

    Button banma;


    // 斑马 start
    private BroadcastReceiver myBroadcastReceiver_banma = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Bundle b = intent.getExtras();
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            if (action.equals(getResources().getString(R.string.activity_intent_filter_action_banma))) {
                Toast.makeText(PdaMainActivity.this, "斑马", Toast.LENGTH_SHORT).show();
            }
        }
    };

    // 斑马 end

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver_banma);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pda_main);
        // 斑马 start
        banma = findViewById(R.id.banma);
        banma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getResources().getString(R.string.activity_intent_filter_action_banma));
        registerReceiver(myBroadcastReceiver_banma, filter);


        // 斑马 end


    }
}
