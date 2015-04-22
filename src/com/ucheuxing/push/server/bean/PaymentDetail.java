package com.ucheuxing.push.server.bean;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * 付款详情的bean
 *  @author Tony  DateTime 2015-4-21 下午3:02:55    
 *  @version 1.0
 */
public class PaymentDetail implements Parcelable {

	public String msg;

	public PaymentDetail() {
		super();
	}

	public PaymentDetail(String msg) {
		super();
		this.msg = msg;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(msg);
	}

	public static final Parcelable.Creator<PaymentDetail> CREATOR = new Creator<PaymentDetail>() {

		@Override
		public PaymentDetail[] newArray(int size) {
			return new PaymentDetail[size];
		}

		@Override
		public PaymentDetail createFromParcel(Parcel source) {
			String msg = source.readString();
			return new PaymentDetail(msg);
		}
	};

}
