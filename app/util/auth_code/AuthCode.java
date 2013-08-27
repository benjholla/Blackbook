package util.auth_code;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthCode {

	public static String getAuthCode(String username) {
		Date today = new Date();
		String day = String.valueOf(42 + Integer.parseInt(new SimpleDateFormat("DDD").format(today)));
		String year = String.valueOf(11 + new SimpleDateFormat("yy").format(today));

		// http://en.wikipedia.org/wiki/SipHash
		Long hash = SipHash.digest(new SipKey(new byte[] { (byte) 0xDE,
				(byte) 0xAD, (byte) 0xBE, (byte) 0xEF, (byte) 0xCA,
				(byte) 0xFE, (byte) 0xBA, (byte) 0xBE, (byte) 0x8B,
				(byte) 0xAD, (byte) 0xF0, (byte) 0x0D, (byte) 0x1B,
				(byte) 0xAD, (byte) 0xB0, (byte) 0x02 }), (username + day + year).getBytes());
		
		String numericDigest = Long.toHexString(hash).toUpperCase()
				.replaceAll("A", "10").replaceAll("B", "11")
				.replaceAll("C", "12").replaceAll("D", "13")
				.replaceAll("E", "14").replaceAll("F", "15");
		
		return  numericDigest.substring(0,6);
	}
}
