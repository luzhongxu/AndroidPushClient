package com.ucheuxing.push.server;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.ucheuxing.push.server.RequestBase.OnRequestListener;

public class RequestManager {

	private List<RequestBase> mRequestList;

	public RequestManager(UcheUxingPushServer server) {
		mRequestList = new ArrayList<RequestBase>();
	}

	public void sendRequest(RequestBase request) {
		if (request == null)
			return;

		synchronized (this) {
			mRequestList.add(request);
		}
		new RequestTask(request).execute();
	}

	public void removeHandler(OnRequestListener handler) {

		synchronized (this) {

			for (int i = 0; i < mRequestList.size(); i++) {
				RequestBase request = mRequestList.get(i);
				OnRequestListener h = request.getRequestListener();
				if (h == handler) {
					request.cancel();
					request.clearRuquestListener();
				}
			}
		}
	}

	private void removeRequest(RequestBase request) {
		synchronized (this) {
			mRequestList.remove(request);
		}
	}

	class RequestTask extends AsyncTask<RequestBase, String, Boolean> {

		private RequestBase mRequest = null;

		public RequestTask(RequestBase request) {
			super();
			mRequest = request;
		}

		@Override
		protected void onPreExecute() {
			mRequest.setExecTask(this);
			if (mRequest.isCancel()) {
				this.cancel(false);
			}
		}

		@Override
		protected Boolean doInBackground(RequestBase... params) {
			boolean req = mRequest.doRequest();
			return req;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			removeRequest(mRequest);
		}

		@Override
		protected void onPostExecute(Boolean success) {
			super.onPostExecute(success);

			removeRequest(mRequest);

			if (mRequest.isCancel())
				return;

			if (success) {
				mRequest.handleRequestOK();
			} else {
				mRequest.handleRequestError();
			}
		}
	}

}
