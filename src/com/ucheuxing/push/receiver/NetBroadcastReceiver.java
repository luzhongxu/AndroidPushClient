package com.ucheuxing.push.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.widget.Toast;

public class NetBroadcastReceiver extends BroadcastReceiver {

	public static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)) {
			ConnectivityManager manager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = manager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();
			Toast.makeText(
					context,
					"mobile:" + mobileInfo.isConnected() + "\n" + "wifi:"
							+ wifiInfo.isConnected() + "\n" + "active:"
							+ (activeInfo == null ? "NULL" : activeInfo
									.getType()), 1).show();
		}
	}

}
