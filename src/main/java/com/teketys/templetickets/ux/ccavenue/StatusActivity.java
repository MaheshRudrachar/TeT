package com.teketys.templetickets.ux.ccavenue;

/**
 * Created by rudram1 on 8/25/16.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;
import com.teketys.templetickets.R;
import com.teketys.templetickets.ux.dialogs.OrderCreateSuccessDialogFragment;

public class StatusActivity extends AppCompatActivity {
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_status);
		
		Intent mainIntent = getIntent();
		TextView tv4 = (TextView) findViewById(R.id.textView1);
		tv4.setText(mainIntent.getStringExtra("transStatus"));

		DialogFragment thankYouDF = OrderCreateSuccessDialogFragment.newInstance(false, true);
		thankYouDF.show(getSupportFragmentManager(), OrderCreateSuccessDialogFragment.class.getSimpleName());
	}
	
	public void showToast(String msg) {
		Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
	}
} 