package com.archer.rs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;

import com.archer.rs.ArcherException;
import com.archer.rs.RSSignature;

public class SignatureUtil {
	
	public static RSSignature generateSignature(String url, String key) {
		long time = System.currentTimeMillis();
		String t = String.valueOf(time);
		String sig = generateSignatureStr(url, time, key);
		try {
			return new RSSignature(URLEncoder.encode(sig, "UTF-8"), t);
		} catch (UnsupportedEncodingException ignore) {
			throw new ArcherException("Generate signature failed");
		}
	}
	
	public static String generateSignatureStr(String url, long time, String key) {
		return Base64.getEncoder().encodeToString(SM4Util.encrypt((url + time).getBytes(), key.getBytes()));
	}
}
