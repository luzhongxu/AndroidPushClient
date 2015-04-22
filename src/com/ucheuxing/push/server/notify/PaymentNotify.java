package com.ucheuxing.push.server.notify;

import android.util.Log;

import com.ucheuxing.push.server.BinaryInputStream;
import com.ucheuxing.push.server.BinaryOutputStream;
import com.ucheuxing.push.server.NotifySessionBase;

/**
 * 收款成功的通知
 * 
 * @author Tony DateTime 2015-4-21 下午2:55:49
 * @version 1.0
 */
public class PaymentNotify extends NotifySessionBase {

	// /////////////////////////////////////////////////////////////////////
	public static final int CmdId = 0x9000;

	private static final String TAG = null;

	// /////////////////////////////////////////////////////////////////////
	public interface PaymentNotifyListener extends OnNotifyListener {
		public void onPaymentSuccess(PaymentNotify request);
	}

	// /////////////////////////////////////////////////////////////////////
	// public ServerInfo ntfServerInfo;

	// /////////////////////////////////////////////////////////////////////
	public int fbkResult;

	// /////////////////////////////////////////////////////////////////////
	public PaymentNotify() {
		super(null);
	}

	// /////////////////////////////////////////////////////////////////////
	@Override
	public int getCmdId() {
		return CmdId;
	}

	@Override
	public void gererateFeedback(BinaryOutputStream out) {
		out.writeInt(fbkResult);
	}

	public boolean parseNotifyData(BinaryInputStream in) {
		Log.d(TAG, "  parseNotifyData ");
		// if(ntfServerInfo == null) {
		// ntfServerInfo = new ServerInfo();
		// }
		// ntfServerInfo.serverUUID = in.readString();
		// ntfServerInfo.serviceName = in.readString();
		// ntfServerInfo.hasPinCode = in.readInt();
		// ntfServerInfo.serverAutoBackupDir = in.readString();

		return true;
	}

}
