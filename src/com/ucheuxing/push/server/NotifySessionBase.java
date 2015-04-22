package com.ucheuxing.push.server;

public abstract class NotifySessionBase extends SessionBase {

	// /////////////////////////////////////////////////////////////////////
	public interface OnNotifyListener extends OnRequestListener {
	}

	// /////////////////////////////////////////////////////////////////////

	public abstract int getCmdId();

	public abstract void gererateFeedback(BinaryOutputStream out);

	public NotifySessionBase(OnNotifyListener listener) {
		super(listener);
	}

	public boolean parseNotifyData(BinaryInputStream in) {
		return true;
	}

	public byte[] toRequestBytes() {

		BinaryOutputStream out = new BinaryOutputStream();
		gererateFeedback(out);

		byte[] content = out.toArrayBytes();

		int contentLength = content.length;

		BinaryOutputStream out2 = new BinaryOutputStream();
		out2.writeInt(0x6773764c);
		out2.writeInt(contentLength);
		out2.writeInt(getCmdId());
		out2.writeBytes(content);

		return out2.toArrayBytes();
	}

	final public void sendFeedback() {
		mServer.sendNotifyFeedback(this);
	}

	final public void handleResponseOK() {
	}

	final public void handleRequestError() {
		super.handleRequestError();
	}
}
