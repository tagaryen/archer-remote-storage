package com.archer.archerfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.archer.net.http.client.NativeRequest;
import com.archer.net.http.client.NativeResponse;
import com.archer.net.http.multipart.FormData;
import com.archer.rs.RSSignature;
import com.archer.rs.util.SM4Util;
import com.archer.rs.util.SignatureUtil;

public class UploadTest {
	

	public static void testProxy() {
		NativeResponse res = NativeRequest.post("http://10.32.122.172:9617/proxy?t=1234567", "nihaow".getBytes(StandardCharsets.UTF_8));
    	System.out.println(res.getContentType());
		System.out.println(new String(res.getBody()));
	}
	
	public static void test() {

		String body = "{\r\n"
				+ "    \"name\":\"某某代理配置\",\r\n"
				+ "    \"requestPath\": \"/proxy\",\r\n"
				+ "    \"proxyUrl\": \"https://10.32.123.24:9666/api\",\r\n"
				+ "    \"proxySsl\": {\r\n"
				+ "        \"verifyPeer\": false,\r\n"
				+ "        \"caPath\": \"/usr/local/rs/gm_cert/ca.crt\",\r\n"
				+ "        \"crtPath\": \"/usr/local/rs/gm_cert/cli.crt\",\r\n"
				+ "        \"keyPath\": \"/usr/local/rs/gm_cert/cli.key\",\r\n"
				+ "        \"enCrtPath\": \"/usr/local/rs/gm_cert/cli_en.crt\",\r\n"
				+ "        \"enKeyPath\": \"/usr/local/rs/gm_cert/cli_en.key\"\r\n"
				+ "    },\r\n"
				+ "    \"requestHeaders\": {},\r\n"
				+ "    \"responseHeaders\":{}\r\n"
				+ "}";
		RSSignature sig = SignatureUtil.generateSignature("/archer/proxy-api/proxy-add", "tg$1!^cv1%%*(a=+");
		System.out.println(sig);
		NativeResponse res = NativeRequest.post("http://10.32.122.172:9617/archer/proxy-api/proxy-add?t="+sig.getT()+"&signature="+sig.getSignature(), body.getBytes(StandardCharsets.UTF_8));
		System.out.println(new String(res.getBody()));
	}

    public static void test2( )
    {
    	FormData form = new FormData();
    	try {
			form.put("file", "ReadMe.md", Files.readAllBytes(Paths.get("E:\\projects\\cppProject\\archer_multiples\\icon.ico")));
		} catch (IOException e) {
			e.printStackTrace();
		}
    	String authKey = "e11!^cvvcs$a1@ad";
    	String uri = "/archer/file-api/file-upload";
    	String nonce = "1234567890123456";
    	byte[] sig = SM4Util.encrypt((uri+nonce).getBytes(), authKey.getBytes());
    	String url = "http://127.0.0.1:9617" + uri + "?nonce="+nonce+"&signature="+Base64.getEncoder().encodeToString(sig);
    	
    	NativeResponse res = NativeRequest.request("POST", url, form, null);
    	System.out.println(res.getContentType());
    	System.out.println(new String(res.getBody()));
    	
    }

    public static void main( String[] args ) {
//    	test();
    	testProxy();
    }
}
