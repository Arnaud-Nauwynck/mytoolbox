package fr.an.util.bits;

public class BitsUtil {

	public  static boolean[] strBitsToBooleans(String str) {
		boolean[] res;
		int len = str.length();
		res = new boolean[len];
		for (int i = 0; i < len; i++) {
			char ch = str.charAt(i);
			res[i] = (ch != '0');
		}
		return res;
	}

	public static String booleansToStrBits(boolean[] p) {
		StringBuilder res = new StringBuilder();
		for (int i = 0; i < p.length; i++) {
			res.append((p[i])? '1' : '0');
		}
		return res.toString();
	}
	
	public  static byte[] booleansToBytes(boolean[] p) {
			byte[] res;
			final int len = p.length;
			int reslen = (len+8-1) / 8;
			res = new byte[reslen];
			int buf = 0; 
			int bufLen = 0;
			int byteIndex = 0;
			for (int i = 0; i < len; i++) {
				boolean bit = p[i];
				buf = (buf << 1) + ((bit)? 1 : 0);
				bufLen++;
				if (bufLen == 8) {
					res[byteIndex++] = (byte) buf;
					buf = 0;
					bufLen = 0;				
				}
			}
			if (bufLen != 0) {
	//			// flush to round to 8 bits.. (shift bits on the left)
	//			int shiftLeft = 8 - bufLen;
	//			res[reslen-1] = (byte) (buf << shiftLeft);
				
				res[reslen-1] = (byte) buf;
			}
			return res;
		}

}
