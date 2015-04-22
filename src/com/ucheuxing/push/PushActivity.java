package com.ucheuxing.push;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ucheuxing.push.server.UcheUxingPushServer;
import com.ucheuxing.push.server.UcheUxingPushServer.UcheUxingPushServerListener;
import com.ucheuxing.push.server.util.ServerParamConfig;

public class PushActivity extends Activity implements OnClickListener,
		UcheUxingPushServerListener {

	private static final String TAG = PushActivity.class.getSimpleName();
	private Button mConnectBtn;
	private UcheUxingPushServer ucheUxingPushServer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ucheUxingPushServer = UcheUxingPushServer.getInstance();
		mConnectBtn = (Button) findViewById(R.id.btnConnect);
		mConnectBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnConnect:
			if (ucheUxingPushServer != null) {
				ucheUxingPushServer.addServerListener(this);
				boolean isConnect = ucheUxingPushServer.connectToServer(
						ServerParamConfig.HOSTNAME, ServerParamConfig.PORT);
				Log.d(TAG, " isConnect : " + isConnect);
			}
			break;

		default:
			break;
		}
	}

	// ////////////////////////////////////////////////
	@Override
	public void onServerConnectedError() {
		Log.d(TAG, " onServerConnectedError ");
	}

	@Override
	public void onServerConnected(UcheUxingPushServer server) {
		Log.d(TAG, " onServerConnected ");

	}

	@Override
	public void onServerDisconnected(UcheUxingPushServer server) {
		Log.d(TAG, " onServerDisconnected ");

	}
	// ////////////////////////////////////////////////
}
