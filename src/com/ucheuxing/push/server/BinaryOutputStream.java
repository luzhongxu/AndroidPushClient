package com.ucheuxing.push.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.ucheuxing.push.server.util.ByteUtil;

public class BinaryOutputStream {

	ByteArrayOutputStream mOut = new ByteArrayOutputStream();

	public byte[] toBytesWithLength() {
		int length = mOut.size() + 4;
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			byte[] intBytes = ByteUtil.int2ByteArray(length);
			out.write(intBytes);
			out.write(mOut.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public byte[] toArrayBytes() {
		return mOut.toByteArray();
	}

	public void writeByte(byte value) {
		mOut.write(value);// write one byte.
	}

	public void writeShort(short value) {
		byte[] shortBytes = ByteUtil.short2ByteArray(value);
		try {
			mOut.write(shortBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeInt(int value) {
		byte[] intBytes = ByteUtil.int2ByteArray(value);
		try {
			mOut.write(intBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeLong(Long value) {
		byte[] longBytes = ByteUtil.long2ByteArray(value);
		try {
			mOut.write(longBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeString(String str) {
		try {
			if (str == null) {
				mOut.write((byte) 0);
			} else {
				byte[] utf8Bytes = str.getBytes("UTF8");
				mOut.write((byte) utf8Bytes.length);
				mOut.write(utf8Bytes);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public void writeGbkString(String str) {
		try {
			if (str == null) {
				mOut.write((byte) 0);
			} else {
				byte[] gbkBytes = str.getBytes("GBK");
				mOut.write((byte) gbkBytes.length);
				mOut.write(gbkBytes);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return;
	}

	public void writeBytes(byte[] bytes) {
		try {
			mOut.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
