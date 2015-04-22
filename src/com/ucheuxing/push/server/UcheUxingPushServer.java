package com.ucheuxing.push.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.ucheuxing.push.server.ReceiveDataThread.ReceiveDataListener;
import com.ucheuxing.push.server.RequestBase.OnRequestListener;
import com.ucheuxing.push.server.request.ConnectRequest;
import com.ucheuxing.push.server.request.HeartBeatRequest;
import com.ucheuxing.push.server.request.ConnectRequest.ConnectRequestListener;
import com.ucheuxing.push.server.request.HeartBeatRequest.HeartBeatRequestListener;
/**
 * 
 *  @author Tony  DateTime 2015-4-17 下午2:34:27    
 *  @version 1.0
 */
public class UcheUxingPushServer implements ConnectRequestListener, Callback,
		ReceiveDataListener, HeartBeatRequestListener {

	// /////////////////////////////////////////////////////////////////////

	public interface UcheUxingPushServerListener {
		public void onServerConnectedError();

		public void onServerConnected(UcheUxingPushServer server);

		public void onServerDisconnected(UcheUxingPushServer server);
	}

	public interface UcheUxingNotifyListener {
	}

	// /////////////////////////////////////////////////////////////////////
	
	private static final int MSG_RESULT = 1;
	private static final int MSG_HEARTBEAT = 2;
	private static final int MSG_NOFIFICATION = 3;

	private static UcheUxingPushServer gInst = new UcheUxingPushServer();

	public static UcheUxingPushServer getInstance() {
		return gInst;
	}

	// /////////////////////////////////////////////////////////////////////

	private List<UcheUxingPushServerListener> mListenerList = new ArrayList<UcheUxingPushServerListener>();
	private List<UcheUxingNotifyListener> mNotifyListenerList = new ArrayList<UcheUxingNotifyListener>();
	private RequestManager mRequestManager = new RequestManager(this);

	private Socket mSocket;
	private boolean mIsConnected;
	private boolean mIsConnecting;

	private ReceiveDataThread mReceivceThread;
	private List<RequestSessionBase> mRequestList;

	private Handler mHandler;

	// /////////////////////////////////////////////////////////////////////

	private UcheUxingPushServer() {
		mRequestList = new ArrayList<RequestSessionBase>();
		mHandler = new Handler(Looper.getMainLooper(), this);
	}

	// /////////////////////////////////////////////////////////////////////

	public void addServerListener(UcheUxingPushServerListener listener) {
		mListenerList.remove(listener);
		mListenerList.add(listener);
	}

	public void removeServerListener(UcheUxingPushServerListener listener) {
		mListenerList.remove(listener);
	}

	public void addServerNotifyListener(UcheUxingNotifyListener listener) {
		if (listener == null)
			return;
		mNotifyListenerList.remove(listener);
		mNotifyListenerList.add(listener);
	}

	public void removeServerNotifyListener(UcheUxingNotifyListener listener) {
		mNotifyListenerList.remove(listener);
	}

	public void removeRequest(OnRequestListener handler) {
		mRequestManager.removeHandler(handler);
	}

	/**
	 *  connect to the server
	 *  @author Tony  DateTime 2015-4-17 下午2:57:56
	 *  @param hostName 主机ip
	 *  @param port 端口号
	 *  @return
	 */
	public boolean connectToServer(String hostName, int port) {
		if (mIsConnected)
			return true;

		if (mIsConnecting)
			return true;

		try {
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		mIsConnecting = true;

		ConnectRequest connectRequest = new ConnectRequest(this);
		connectRequest.reqHostName = hostName;
		connectRequest.reqPort = port;

		sendRequest(connectRequest);

		return true;
	}

	public boolean isConnected() {
		return mIsConnected;
	}

	/**
	 *  disconnect the socket
	 *  @author Tony  DateTime 2015-4-17 下午2:59:50
	 */
	public void disconnect() {
		Log.d("onServerConnected", "disconnect -- ");

		mIsConnected = false;

		if (mReceivceThread != null) {
			mReceivceThread.cancel();
		}

		synchronized (this) {
			if (mSocket != null) {
				try {
					mSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		if (mReceivceThread != null) {
			mReceivceThread.cancel();
			try {
				mReceivceThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mReceivceThread = null;
		}

		mSocket = null;
	}

	
	public boolean sendRequest(RequestBase request) {
		Log.d("sendRequest", request.getClass().getSimpleName());
		request.setUcheUxingPushServer(this);
		if (request instanceof NotifySessionBase) {
			Log.w("sendRequest",
					"Please use sendNotifyFeedback to response Notification!!!");
			return false;
		}
		if (request instanceof RequestSessionBase) {
			RequestSessionBase sessionRequest = (RequestSessionBase) request;
			synchronized (this) {
				mRequestList.remove(sessionRequest);
				mRequestList.add(sessionRequest);
			}
		}
		mRequestManager.sendRequest(request);
		return true;
	}

	public boolean sendNotifyFeedback(NotifySessionBase request) {
		request.setUcheUxingPushServer(this);
		mRequestManager.sendRequest(request);
		return true;
	}

	/**
	 *  @author Tony  DateTime 2015-4-17 下午3:06:10
	 *  @param requestData 
	 *  @return
	 */
	public boolean writeSocketData(byte[] requestData) {

		if (mSocket == null) {
			return false;
		}
		synchronized (this) {
			try {
				Log.i("SOCKET", "Send cmdId=" + requestData[8] + " "
						+ requestData[9]);
				OutputStream out = mSocket.getOutputStream();
				out.write(requestData);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 *  @author Tony  DateTime 2015-4-17 下午3:08:03
	 *  @param dataBuffer
	 */
	public void addReceivedData(byte[] dataBuffer) {
		Log.d("SOCKET", " addReceivedData ");
		BinaryInputStream inputStream = new BinaryInputStream(dataBuffer);
		int cmdId = inputStream.readInt();

		RequestSessionBase findRequest = null;

		synchronized (this) {
			for (int i = 0; i < this.mRequestList.size(); i++) {
				RequestSessionBase request = mRequestList.get(i);
				if (request.getCmdId() == cmdId) {
					findRequest = request;
					mRequestList.remove(request);
					break;
				}
			}
		}
		if (findRequest != null) { // request
			Message msg = mHandler.obtainMessage(MSG_RESULT);
			msg.arg1 = findRequest.parseResponseData(inputStream) ? 1 : 0;
			msg.arg2 = cmdId;
			msg.obj = findRequest;
			msg.sendToTarget();
		} else { // notify
			Log.d("SOCKET", " the type is Notify ");
			NotifySessionBase base = createServerNotification(cmdId);
			if (base == null)
				return;

			base.setUcheUxingPushServer(this);

			if (mNotifyListenerList != null) {
				Message msg = mHandler.obtainMessage(MSG_NOFIFICATION);
				msg.arg1 = base.parseNotifyData(inputStream) ? 1 : 0;
				msg.obj = base;
				msg.sendToTarget();
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == MSG_RESULT) {
			boolean success = (msg.arg1 == 1);
			RequestSessionBase request = (RequestSessionBase) msg.obj;
			if (success) {
				request.handleResponseOK();
			} else {
				request.handleRequestError();
			}
		} else if (msg.what == MSG_HEARTBEAT) {
			HeartBeatRequest r = new HeartBeatRequest(this);
			sendRequest(r);
		} else if (msg.what == MSG_NOFIFICATION) {
			NotifySessionBase notification = (NotifySessionBase) msg.obj;
			handleNotifyReceived(notification);
		}
		return false;
	}

	@Override
	public void onRequestError(RequestBase request) {
		if (mReceivceThread != null) {
			mReceivceThread.cancel();
			mReceivceThread = null;
		}

		for (UcheUxingPushServerListener listener : mListenerList) {
			listener.onServerConnectedError();
		}
		mIsConnecting = false;
	}

	@Override
	public void onConnected(ConnectRequest request) {

		mIsConnecting = false;

		mSocket = request.repSocket;

		mIsConnected = true;
		mReceivceThread = new ReceiveDataThread(this, this);
		mReceivceThread.socket = mSocket;
		mReceivceThread.start();
		Log.d("onServerConnected",
				"mListenerList size: " + mListenerList.size());
		for (UcheUxingPushServerListener listener : mListenerList) {
			Log.d("onServerConnected", "server onConnected");
			listener.onServerConnected(this);
		}
	}

	@Override
	public void onReceiveDataFinished(ReceiveDataThread request) {
		Log.d("onServerConnected", "onReceiveDataFinished -- ");

		disconnect();

		for (UcheUxingPushServerListener listener : mListenerList) {
			Log.d("onServerConnected", "onReceiveDataFinished");
			listener.onServerDisconnected(this);
		}
	}

	@Override
	public void onHeartBeated(HeartBeatRequest request) {
		mHandler.sendEmptyMessageDelayed(MSG_HEARTBEAT, 1000 * 60 * 5);
	}

	// TODO
	private void handleNotifyReceived(NotifySessionBase notification) {
		List<UcheUxingNotifyListener> listenerList = mNotifyListenerList;
		if (listenerList == null) {
			return;
		}
		for (int i = 0; i < listenerList.size(); i++) {
			UcheUxingNotifyListener l = listenerList.get(i);

			switch (notification.getCmdId()) {
			// TODO:根据不同的通知类型来做
			// case ServerConfigChangeNotify.CmdId:
			// if( l instanceof ServerConfigChangeListener ) {
			// ((ServerConfigChangeListener)l).onServerConfigChanged((ServerConfigChangeNotify)notification);
			// }
			// break;
			// case ClientBackupInfoChangeNotify.CmdId:
			// if (l instanceof ClientBackupInfoChangeListener) {
			// ((ClientBackupInfoChangeListener) l)
			// .onClientBackupInfoChange((ClientBackupInfoChangeNotify)
			// notification);
			// }
			// break;

			}
		}
	}

	// TODO
	private NotifySessionBase createServerNotification(int cmdId) {
		Log.d("SOCKET", " createServerNotification cmdId: " + cmdId);
		NotifySessionBase base = null;
		switch (cmdId) {
		// case ServerConfigChangeNotify.CmdId:
		// return new ServerConfigChangeNotify();
		// case ClientBackupInfoChangeNotify.CmdId:
		// return new ClientBackupInfoChangeNotify();
		}
		return base;
	}

}
