package com.archer.rs.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Random;

import com.archer.rs.ArcherException;
import com.archer.rs.RSSignature;

public class SignatureUtil {
	
	private static final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	
	private static final Random r = new Random();

	public static RSSignature generateSignature(String url, String key) {
		char[] rand = new char[16];
		for(int i = 0; i < 16; i++) {
			rand[i] = chars[r.nextInt(chars.length)];
		}
		String t = String.valueOf(System.currentTimeMillis());
		String sig = Base64.getEncoder().encodeToString(SM4Util.encrypt((url + t).getBytes(), key.getBytes()));
		try {
			return new RSSignature(URLEncoder.encode(sig, "UTF-8"), t);
		} catch (UnsupportedEncodingException ignore) {
			throw new ArcherException("Generate signature failed");
		}
	}
}
