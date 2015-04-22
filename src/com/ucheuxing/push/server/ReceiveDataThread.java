package com.ucheuxing.push.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ucheuxing.push.server.RequestBase.OnRequestListener;
import com.ucheuxing.push.server.util.ByteUtil;

public class ReceiveDataThread extends Thread {

	private final String TAG = "SOCKET";

	public interface ReceiveDataListener extends OnRequestListener {
		public void onReceiveDataFinished(ReceiveDataThread request);
	}

	public Socket socket;

	private ReceiveDataListener mListener;
	private boolean mIsCanncel;
	private UcheUxingPushServer mServer;

	private Handler mHandler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message arg0) {
			handleResponse();
			return false;
		}
	});

	public ReceiveDataThread(ReceiveDataListener listener,
			UcheUxingPushServer server) {
		mListener = listener;
		mServer = server;

	}

	public void cancel() {
		mIsCanncel = true;
	}

	public boolean isCancel() {
		return mIsCanncel;
	}

	@Override
	public void run() {

		byte[] headerBuffer = new byte[4];
		Log.i(TAG, " reading 1 ");
		while (!isCancel()) {

			try {
				InputStream inputStream = socket.getInputStream();
				Log.i(TAG, " reading 2 ");
				int totalRead = 0;
				int needRead = 4;
				while (true) {
					int read = inputStream.read(headerBuffer, totalRead,
							needRead - totalRead);
					Log.i(TAG, " reading 3 read ： "+read);
					if (read == -1) {
						Log.i(TAG, "READ FAIL");
						mHandler.sendEmptyMessage(0);
						return;
					}
					totalRead += read;
					if (totalRead == needRead)
						break;
				}

				if (!(headerBuffer[0] == 0x65 && headerBuffer[1] == 0x76
						&& headerBuffer[2] == 0x69 && headerBuffer[3] == 0x6c)) {
					Log.i(TAG, "READ FAIL, Magic is not correct.");
					continue;
				}

				totalRead = 0;
				needRead = 4;
				while (true) {
					int read = inputStream.read(headerBuffer, totalRead,
							needRead - totalRead);
					if (read == -1) {
						Log.i(TAG, "READ FAIL");
						mHandler.sendEmptyMessage(0);
						return;
					}
					totalRead += read;
					if (totalRead == needRead)
						break;
				}

				int length = ByteUtil.byte2Int(headerBuffer);

				if (length >= 0x10000) {
					Log.w(TAG, "Header length is too much! len = " + length);
					// String formatStr =
					// String.format("Header Buffer[0-3]=%02x %02x %02x %02x",
					// headerBuffer[0], headerBuffer[1], headerBuffer[2],
					// headerBuffer[3]);
					// Log.w(TAG, formatStr);
					continue;
				}
				if (length < 2) {
					Log.w(TAG, "Header length is too little! len = " + length);
					// String formatStr =
					// String.format("Header Buffer[0-3]=%02x %02x %02x %02x",
					// headerBuffer[0], headerBuffer[1], headerBuffer[2],
					// headerBuffer[3]);
					// Log.w(TAG, formatStr);
					continue;
				}

				byte[] dataBuffer = new byte[length];

				totalRead = 0;
				needRead = length;
				while (true) {
					int read = inputStream.read(dataBuffer, totalRead, needRead
							- totalRead);
					totalRead += read;
					if (totalRead == needRead)
						break;
				}
				StringBuffer mStringBuffer = new StringBuffer();
				for (int i = 0; i < length; i++) {
					mStringBuffer.append((dataBuffer[i] + 48));

				}
				Log.i(TAG, "Recv cmdId=" + mStringBuffer.toString());

				mServer.addReceivedData(dataBuffer);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mHandler.sendEmptyMessage(0);
	}

	protected void handleResponse() {
		if (mListener != null) {
			mListener.onReceiveDataFinished(this);
		}
	}

}
