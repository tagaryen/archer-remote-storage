package com.archer.archerfile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.archer.net.http.HttpRequest;
import com.archer.net.http.HttpResponse;
import com.archer.net.http.HttpServer;
import com.archer.net.http.HttpServerException;
import com.archer.net.http.HttpStreamWriter;
import com.archer.net.http.HttpWrappedHandler;
import com.archer.net.http.client.NativeRequest;
import com.archer.net.http.client.NativeResponse;
import com.archer.net.http.multipart.FormData;
import com.archer.rs.RSSignature;
import com.archer.rs.util.SM4Util;
import com.archer.rs.util.SignatureUtil;

public class UploadTest {
	

	public static void testProxy() {
		String body = "{\"dlBmList\":[\"platformportal01\",\"maintenancecenter\"],\"pzKey\":\"connectLogin\"}";
		NativeResponse res = NativeRequest.post("http://10.32.122.172:9617/webGateway/apiCommon/v1.0/mhmdl/getDlPzx", body.getBytes(StandardCharsets.UTF_8));
    	System.out.println(res.getContentType());
		System.out.println(new String(res.getBody()));
	}
	
	public static void test() {

		String body = "{"
				+ "    \"name\":\"根代理\","
				+ "    \"requestPath\": \"/\","
				+ "    \"proxyUrl\": \"https://10.32.123.24:8090/\","
				+ "    \"proxySsl\": {"
				+ "        \"verifyPeer\": false"
				+ "    },"
				+ "    \"requestHeaders\": {},"
				+ "    \"responseHeaders\":{\"this-is\":\"a proxy\"}"
				+ "}";
		RSSignature sig = SignatureUtil.generateSignature("/archer/proxy-api/proxy-add", "tg$1!^cv1%%*(a=+");
		NativeResponse res = NativeRequest.post("http://10.32.122.172:9617/archer/proxy-api/proxy-add?t="+sig.getT()+"&signature="+sig.getSignature(), body.getBytes(StandardCharsets.UTF_8));
		System.out.println(new String(res.getBody()));
	}
	public static void testDel() {
		RSSignature sig = SignatureUtil.generateSignature("/archer/proxy-api/proxy-del", "tg$1!^cv1%%*(a=+");
		String name = null;
		try {
			name = URLEncoder.encode("服务代理", "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		NativeResponse res = NativeRequest.post("http://10.32.122.172:9617/archer/proxy-api/proxy-del?name="+name+"&t="+sig.getT()+"&signature="+sig.getSignature(), null);
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
    
	public static void httpServer() {
		HttpServer server = new HttpServer();
		try {
			server.listen("127.0.0.1", 9666, new HttpWrappedHandler() {

				@Override
				public void handle(HttpRequest req, HttpResponse res) throws Exception {
					System.out.println(new String(req.getContent()));
					HttpStreamWriter writer = res.streamWriter();
					writer.write("nihaowa".getBytes(StandardCharsets.UTF_8));
					writer.end();
				}

				@Override
				public void handleException(HttpRequest req, HttpResponse res, Throwable t) {
					t.printStackTrace();
				}});
		} catch (HttpServerException e) {
			e.printStackTrace();
		}
	}
	

    public static void main( String[] args ) {
//    	test();
//    	testDel();
    	testProxy();
//    	httpServer();
    }
}
