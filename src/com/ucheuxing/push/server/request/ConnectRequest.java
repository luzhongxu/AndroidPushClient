package com.ucheuxing.push.server.request;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import com.ucheuxing.push.server.RequestBase;

public class ConnectRequest extends RequestBase {

	// /////////////////////////////////////////////////////////////////////
	public interface ConnectRequestListener extends OnRequestListener {
		public void onConnected(ConnectRequest request);
	}

	// /////////////////////////////////////////////////////////////////////
	public String reqHostName;
	public int reqPort;

	// /////////////////////////////////////////////////////////////////////
	public Socket repSocket;

	// /////////////////////////////////////////////////////////////////////

	private static final int TIME_OUT = 5 * 1000;
	private static final String TAG = ConnectRequest.class.getSimpleName();

	// /////////////////////////////////////////////////////////////////////
	public ConnectRequest(ConnectRequestListener listener) {
		super(listener);
	}

	@Override
	protected boolean doRequest() {

		try {
			Log.d(TAG, " doRequest , reqHostName : "+reqHostName+" reqPort :　"+reqPort);
			repSocket = new Socket();
			repSocket.connect(new InetSocketAddress(reqHostName, reqPort),
					TIME_OUT);
			boolean connected = repSocket.isConnected();
			Log.d(TAG, " connected : "+connected);
			if (connected) {
				repSocket.setSoTimeout(0);
			}
			return connected;
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG, " doRequest exception "+e.toString());
		}
		return false;
	}

	@Override
	public void handleRequestOK() {
		ConnectRequestListener listener = (ConnectRequestListener) getRequestListener();
		Log.d("handleResponseOK", "handleResponseOK!");
		if (listener != null) {
			listener.onConnected(this);
		}
	}

}
