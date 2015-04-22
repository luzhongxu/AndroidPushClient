package com.ucheuxing.push.server;

public abstract class SessionBase extends RequestBase {

	// /////////////////////////////////////////////////////////////////////

	public SessionBase(OnRequestListener listener) {
		super(listener);
	}

	public abstract int getCmdId();

	public abstract byte[] toRequestBytes();

	public void handleResponseOK() {

	}

	@Override
	protected boolean doRequest() {

		if (!mServer.isConnected()) {
			this.errorCode = -1;
			this.errorString = "服务器未连接";
			return false;
		}

		byte[] requestData = this.toRequestBytes();

		if (requestData == null) {
			this.errorCode = -2;
			this.errorString = "无效的请求数据";
			return false;
		}

		if (!mServer.writeSocketData(requestData)) {
			this.errorCode = -3;
			this.errorString = "发送请求数据失败";
			return false;
		}

		return true;
	}
}
