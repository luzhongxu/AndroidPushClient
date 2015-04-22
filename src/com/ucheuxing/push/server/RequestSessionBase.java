package com.ucheuxing.push.server;

import android.util.Log;

public abstract class RequestSessionBase extends SessionBase {

	public abstract int getCmdId();

	public abstract void gererateRequest(BinaryOutputStream out);

	public RequestSessionBase(OnRequestListener listener) {
		super(listener);
	}

	public boolean parseResponseData(BinaryInputStream in) {
		return true;
	}

	public byte[] toRequestBytes() {

		BinaryOutputStream out = new BinaryOutputStream();
		gererateRequest(out);

		byte[] content = out.toArrayBytes();
		int contentLength = content.length;

		BinaryOutputStream out2 = new BinaryOutputStream();
		out2.writeInt(0x6c697665);// 0x6c697665
		out2.writeInt(contentLength + 4);
		out2.writeInt(getCmdId());
		out2.writeBytes(content);
		Log.d("RequestSessionBase", "RequestSessionBase write");
		StringBuffer mStringBuffer = new StringBuffer();
		for (int i = 0; i < out2.toArrayBytes().length; i++) {
			mStringBuffer.append(out2.toArrayBytes()[i] + "");
		}
		Log.d("RequestSessionBase", mStringBuffer.toString());
		return out2.toArrayBytes();
	}
}
