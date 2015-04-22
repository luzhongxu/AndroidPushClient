package com.ucheuxing.push.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.InflaterInputStream;

import android.util.Log;
/**
 * 
 *  @author Tony  DateTime 2015-4-17 下午2:37:00    
 *  @version 1.0
 */
public class BinaryInputStream {

	private static final String TAG = BinaryInputStream.class.getSimpleName();

	private byte[] mBuffer;
	private int mPos;

	public BinaryInputStream(byte[] inputBytes) {
		mBuffer = inputBytes;
		mPos = 0;
	}

	public BinaryInputStream(byte[] inputBytes, int offset, int length) {
		mBuffer = new byte[length];
		System.arraycopy(inputBytes, offset, mBuffer, 0, length);
		mPos = 0;
	}

	public void rewind() {
		mPos = 0;
	}

	public void skip(int offset) {
		mPos += offset;
	}

	public int available() {
		return mBuffer.length - mPos;
	}

	public byte readByte() {
		if (available() < 1) {
			Log.e(TAG, "readByte overhead.");
			return 0;
		}
		return mBuffer[mPos++];
	}

	public int readShort() {

		if (available() < 2) {
			Log.e(TAG, "readShort overhead.");
			return 0;
		}

		int targets = mBuffer[mPos] & 0xFF | (mBuffer[mPos + 1] & 0xFF) << 8;
		mPos += 2;
		return targets;
	}

	public int readInt() {
		if (available() < 4) {
			Log.e(TAG, "readInt overhead.");
			return 0;
		}

		int target = mBuffer[mPos] & 0xFF | (mBuffer[mPos + 1] & 0xFF) << 8
				| (mBuffer[mPos + 2] & 0xFF) << 16
				| (mBuffer[mPos + 3] & 0xFF) << 24;
		mPos += 4;
		return target;
	}

	public float readFloat() {
		int target = readInt();
		return Float.intBitsToFloat(target);
	}

	public long readLong() {
		if (available() < 8) {
			Log.e(TAG, "readLong overhead.");
			return 0;
		}

		long target1 = mBuffer[mPos] & 0xFF | (mBuffer[mPos + 1] & 0xFF) << 8
				| (mBuffer[mPos + 2] & 0xFF) << 16
				| (mBuffer[mPos + 3] & 0xFF) << 24;
		mPos += 4;
		long target2 = mBuffer[mPos] & 0xFF | (mBuffer[mPos + 1] & 0xFF) << 8
				| (mBuffer[mPos + 2] & 0xFF) << 16
				| (mBuffer[mPos + 3] & 0xFF) << 24;
		mPos += 4;
		return target1 | target2 << 32;
	}

	public String readString() {

		int length = (int) readByte();
		if (length < 0)
			length += 256;

		int startPos = mPos;
		if (mPos + length > mBuffer.length) {
			Log.i(TAG, "readString exceed.");
			return "";
		}
		mPos += length;
		String retVal = new String(mBuffer, startPos, mPos - startPos);

		return retVal;
	}

	public String readGbkString() {
		int length = (int) readByte();
		if (length < 0)
			length += 256;

		int startPos = mPos;
		if (mPos + length > mBuffer.length) {
			Log.i(TAG, "readGbkString exceed.");
			return "";
		}
		mPos += length;

		try {
			String retVal = new String(mBuffer, startPos, mPos - startPos,
					"GBK");
			return retVal;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return "";
	}

	public byte[] readBytes(int length) {

		int copyLen = mBuffer.length - mPos >= length ? length : mBuffer.length
				- mPos;
		byte[] retBytes = new byte[copyLen];
		System.arraycopy(mBuffer, mPos, retBytes, 0, copyLen);
		mPos += length;
		return retBytes;
	}

	public byte[] readBytesWithEndByte(byte endByte) {

		int startPos = mPos;
		while (mPos < mBuffer.length && mBuffer[mPos] != endByte)
			mPos++;

		byte[] retBuffer = new byte[mPos - startPos];
		System.arraycopy(mBuffer, startPos, retBuffer, 0, mPos - startPos);
		if (mPos < mBuffer.length)
			mPos++;

		return retBuffer;
	}

	public void skipWithEndByte(byte endByte) {
		while (mPos < mBuffer.length && mBuffer[mPos] != endByte)
			mPos++;
		if (mPos < mBuffer.length)
			mPos++;
	}

	public byte[] decompressRest() {

		int size = this.readInt();
		Log.i("decompressRest", "start");
		this.skip(4);

		ByteArrayInputStream bStream = new ByteArrayInputStream(mBuffer, mPos,
				mBuffer.length - mPos);
		InflaterInputStream zipStream = new InflaterInputStream(bStream);

		byte[] retBuffer = new byte[size];

		int read = 0;
		int total = 0;
		try {
			while (true) {
				read = zipStream.read(retBuffer, total, retBuffer.length
						- total);
				total += read;
				if (total >= retBuffer.length)
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.i("decompressRest", "end");
		mPos = mBuffer.length;

		return retBuffer;
	}

	public byte[] restData() {

		int restCount = available();
		byte[] retData = new byte[restCount];
		System.arraycopy(mBuffer, mPos, retData, 0, restCount);
		mPos = mBuffer.length;
		return retData;
	}

	public List<BinaryInputStream> splitLines() {
		List<BinaryInputStream> retList = new ArrayList<BinaryInputStream>();

		byte[] restBytes = restData();

		int startPos = 0;

		for (int i = 0; i < restBytes.length; i++) {

			if (restBytes[i] == '\r') {
				if (i < restBytes.length - 1 && restBytes[i + 1] == '\n') {
					BinaryInputStream stream = new BinaryInputStream(restBytes,
							startPos, i - startPos);
					retList.add(stream);
					startPos = i + 2;
					i++;
					continue;
				}
			}

			if (restBytes[i] == '\n') {
				BinaryInputStream stream = new BinaryInputStream(restBytes,
						startPos, i - startPos);
				retList.add(stream);
				startPos = i + 1;
				continue;
			}
		}
		return retList;
	}

}
