package com.ucheuxing.push.server;

import com.ucheuxing.push.server.RequestManager.RequestTask;

public abstract class RequestBase {

	// /////////////////////////////////////////////////////////////////////
	public interface OnRequestListener {
		public void onRequestError(RequestBase request);
	}

	// /////////////////////////////////////////////////////////////////////
	public int errorCode = 0;
	public String errorString = "";
	// /////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////
	private OnRequestListener mListener;
	private Object mExtra;
	private boolean mCancel = false;
	private RequestTask mExecTask;

	protected UcheUxingPushServer mServer;

	// /////////////////////////////////////////////////////////////////////

	public RequestBase(OnRequestListener listener) {
		mListener = listener;
	}

	public void clearRuquestListener() {
		mListener = null;
	}

	public OnRequestListener getRequestListener() {
		return mListener;
	}

	public void setUcheUxingPushServer(UcheUxingPushServer server) {
		mServer = server;
	}

	public void cancel() {
		mCancel = true;
		if (mExecTask != null) {
			mExecTask.cancel(false);
		}
	}

	public boolean isCancel() {
		return mCancel;
	}

	void setExecTask(RequestTask task) {
		mExecTask = task;
	}

	protected abstract boolean doRequest();

	public Object getExtra() {
		return mExtra;
	}

	public void setExtra(Object extra) {
		mExtra = extra;
	}

	public void handleRequestOK() {
	}

	public void handleRequestError() {
		if (mListener == null)
			return;

		if (errorString == null) {
			errorString = "处理出错";
		}

		mListener.onRequestError(this);
	}
}
