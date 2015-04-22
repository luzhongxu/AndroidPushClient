/**********************************************************
 *     _              _  _  _  ____          __   _       *
 *    / \   _ __   __| || |(_)/ ___|  ___ __/ /__| |_     *
 *   / _ \ | '_  \/ _  || || |\___ \ / _ \_   _|_   _|    *
 *  / ___ \| | | | (_) || || | ___) | (_) || |   | |_     *
 * /_/   \_|_| |_|\____)|_||_||____/ \___/ /_/   \___|    *
 *                                                        *
 **********************************************************
 * Copyright 2014, AndliSoft.com.                         *
 * All rights, including trade secret rights, reserved.   *
 **********************************************************/

package com.ucheuxing.push.server.request;

import com.ucheuxing.push.server.BinaryInputStream;
import com.ucheuxing.push.server.BinaryOutputStream;
import com.ucheuxing.push.server.RequestSessionBase;


public class LoginServerRequest extends RequestSessionBase {

	///////////////////////////////////////////////////////////////////////
	public static final int RequestId = 0x6001;
	
	///////////////////////////////////////////////////////////////////////
	public interface LoginServerRequestListener extends OnRequestListener {
		public void onLoginServerSuccess(LoginServerRequest request);
	}
	
	///////////////////////////////////////////////////////////////////////	
	public String reqPinCode;
	public String reqClientId;
	///////////////////////////////////////////////////////////////////////	
	public int repResult;

	///////////////////////////////////////////////////////////////////////	
	public int repLoginStatus;
	
	///////////////////////////////////////////////////////////////////////
	public LoginServerRequest(LoginServerRequestListener listener ) {
		super(listener);
	}

	///////////////////////////////////////////////////////////////////////
	@Override
	public int getCmdId() {
		return RequestId;
	}

	@Override
	public void gererateRequest(BinaryOutputStream out) {
		out.writeString(reqPinCode);
		out.writeString(reqClientId);
	}
	
	@Override
	public boolean parseResponseData(BinaryInputStream in ) {
		repResult = in.readInt();
		repLoginStatus = in.readInt();
		return true;
	}
	
	@Override
	public void handleResponseOK() {
		LoginServerRequestListener listener = (LoginServerRequestListener) getRequestListener();
		if( listener != null ) {
			listener.onLoginServerSuccess(this);
		}
	}
}

