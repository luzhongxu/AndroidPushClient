package com.ucheuxing.push.server.util;

import java.io.UnsupportedEncodingException;

public class ByteUtil {

	/**
	 * 从一个byte[]数组中截取一部分
	 * 
	 * @param src
	 * @param begin
	 * @param count
	 * @return
	 */
	public static byte[] subBytes(byte[] src, int begin, int count) {
		byte[] bs = new byte[count];
		for (int i = begin; i < begin + count; i++)
			bs[i - begin] = src[i];
		return bs;
	}

	public static byte[] join(byte[] src, byte[] des) {
		byte[] result = new byte[src.length + des.length];
		System.arraycopy(src, 0, result, 0, src.length);
		System.arraycopy(des, 0, result, src.length, des.length);
		return result;
	}

	public static byte[] BigToLittleOrLittleToBig(byte[] src) {
		int lenght = src.length;
		byte[] ret = new byte[lenght];
		for (int i = lenght; i > 0; i--) {
			ret[i - 1] = src[lenght - i];
		}
		return ret;
	}

	public static byte[] int2ByteArray(int i) {
		byte[] result = new byte[4];
		result[3] = (byte) ((i >> 24) & 0xFF);
		result[2] = (byte) ((i >> 16) & 0xFF);
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

	public static byte[] short2ByteArray(short i) {
		byte[] result = new byte[2];
		result[1] = (byte) ((i >> 8) & 0xFF);
		result[0] = (byte) (i & 0xFF);
		return result;
	}

	public static byte[] long2ByteArray(long i) {
		byte[] result = new byte[8];
		result[7] = (byte) (i >> 56);
		result[6] = (byte) (i >> 48);
		result[5] = (byte) (i >> 40);
		result[4] = (byte) (i >> 32);
		result[3] = (byte) (i >> 24);
		result[2] = (byte) (i >> 16);
		result[1] = (byte) (i >> 8);
		result[0] = (byte) (i >> 0);

		return result;
	}

	static StringBuffer sStringBuffer = new StringBuffer();

	public static String byte2hex(byte[] b) {
		// String hs = "";
		sStringBuffer.setLength(0);
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				// hs = hs + "0" + stmp;
				sStringBuffer.append("0").append(stmp);
			else
				// hs = hs + stmp;
				sStringBuffer.append(stmp);
		}
		return sStringBuffer.toString();
	}

	public static byte[] hex2byte(String str) { // 字符串转二进�?
		if (str == null)
			return null;
		str = str.trim();
		int len = str.length();
		if (len == 0 || len % 2 != 0)
			return null;
		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer
						.decode("0x" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}

	}

	// 将两位的byte数组转换�?6�?int�?
	public static int toInt16(byte[] bytes) {
		int ret = ((bytes[0] << 8) & 0xff00) | (bytes[1] & 0x00ff);
		return ret;
	}

	/**
	 * 2. * 浮点转换为字�?3. * 4. * @param f 5. * @return 6.
	 */
	public static byte[] float2byte(float f) {

		// 把float转换为byte[]
		int fbit = Float.floatToIntBits(f);

		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (fbit >> (24 - i * 8));
		}

		// 翻转数组
		int len = b.length;
		// 建立�?��与源数组元素类型相同的数�?
		byte[] dest = new byte[len];
		// 为了防止修改源数组，将源数组拷贝�?��副本
		System.arraycopy(b, 0, dest, 0, len);
		byte temp;
		// 将顺位第i个与倒数第i个交�?
		for (int i = 0; i < len / 2; ++i) {
			temp = dest[i];
			dest[i] = dest[len - i - 1];
			dest[len - i - 1] = temp;
		}

		return dest;

	}

	/**
	 * 将长度为 4 �?byte 数组转换 int
	 * 
	 * @param b
	 * @return
	 */
	public static int byte2Int(byte[] b) {
		return b[0] & 0xFF | (b[1] & 0xFF) << 8 | (b[2] & 0xFF) << 16
				| (b[3] & 0xFF) << 24;
	}

	/**
	 * 将长度为2的byte数组转换�?6位int
	 * 
	 * @param res
	 *            byte[]
	 * @return int
	 * */
	public static short byte2Short(byte[] b) {
		// res = InversionByte(res);
		// �?��byte数据左移24位变�?x??000000，再右移8位变�?x00??0000
		int targets = b[0] & 0xFF | (b[1] & 0xFF) << 8; // | 表示安位�?
		return (short) targets;
	}

	/**
	 * 字节转换为浮�?
	 * 
	 * @param b
	 *            字节（至�?个字节）
	 * @param index
	 *            �?��位置
	 * @return
	 */
	public static float byte2float(byte[] b) {
		int asInt = (b[0] & 0xFF) | ((b[1] & 0xFF) << 8)
				| ((b[2] & 0xFF) << 16) | ((b[3] & 0xFF) << 24);

		return Float.intBitsToFloat(asInt);
	}

	// 将byte数组中的0截取
	public static byte[] cutToByte(byte[] mbyte) {
		if (mbyte == null || mbyte.length == 0) {
			return null;
		}

		for (int i = 0; i < mbyte.length; i++) {
			byte[] result = new byte[i];
			if (mbyte[i] == 0) {

				System.arraycopy(mbyte, 0, result, 0, i);
				return result;
			}
		}
		return null;
	}

	public static byte[] unpackBytes(byte[] tmp) {
		byte[] p = new byte[tmp.length * 2 + 1];
		int i, cnt;
		byte[] uncode = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'+', '-', '.', ' ' };
		// 压缩串移到临时串
		for (i = 0; i < tmp.length; i++)
			p[i] = tmp[i];
		p[i] = 0;

		// 解析
		for (cnt = 0, i = 0; i < tmp.length; i++) {
			int x = tmp[i];
			if (x < 0)
				x += 256;
			p[cnt] = uncode[(x - 32) / 14];
			++cnt;
			p[cnt] = uncode[(x - 32) % 14];
			++cnt;
		}

		if (cnt > 0 && p[cnt - 1] == ' ') {
			cnt--;
		}
		p[cnt] = 0;

		byte[] ret = new byte[cnt];
		for (i = 0; i < ret.length; i++)
			ret[i] = p[i];

		return ret;
	}

	public static String unpack(byte[] tmp) {
		byte[] bytes = unpackBytes(tmp);
		if (bytes == null)
			return "";
		try {
			return new String(bytes, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

}
