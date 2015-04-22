package com.ucheuxing.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

/**
 * push receiver
 * 
 * @author Tony DateTime 2015-4-22 上午11:12:48
 * @version 1.0
 */
public class UcheUxingPushReceiver extends BroadcastReceiver {

	public static final String UCHEUXING_PUSH_ACTION = "com.ucheuxing.action.push";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, UCHEUXING_PUSH_ACTION)) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();
			Toast.makeText(
					context,
					"mobile:"
							+ mobileInfo.isConnected()
							+ "\n"
							+ "wifi:"
							+ wifiInfo.isConnected()
							+ "\n"
							+ "active:"
							+ (activeInfo == null ? "NULL" : activeInfo
									.getType()), 1).show();
		}
	}

}
