package com.ucheuxing.push.server.request;

import com.ucheuxing.push.server.BinaryInputStream;
import com.ucheuxing.push.server.BinaryOutputStream;
import com.ucheuxing.push.server.RequestSessionBase;

public class HeartBeatRequest extends RequestSessionBase {

	// /////////////////////////////////////////////////////////////////////
	public static final int RequestId = 0x05;

	// /////////////////////////////////////////////////////////////////////
	public interface HeartBeatRequestListener extends OnRequestListener {
		public void onHeartBeated(HeartBeatRequest request);
	}

	// /////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////
	public HeartBeatRequest(HeartBeatRequestListener listener) {
		super(listener);
	}

	// /////////////////////////////////////////////////////////////////////
	@Override
	public int getCmdId() {
		return RequestId;
	}

	@Override
	public void gererateRequest(BinaryOutputStream out) {
	}

	@Override
	public boolean parseResponseData(BinaryInputStream in) {
		return true;
	}

	@Override
	public void handleResponseOK() {
		HeartBeatRequestListener listener = (HeartBeatRequestListener) getRequestListener();
		if (listener != null) {
			listener.onHeartBeated(this);
		}
	}

}
