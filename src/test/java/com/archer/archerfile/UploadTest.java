package com.archer.archerfile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.archer.net.http.client.NativeRequest;
import com.archer.net.http.client.NativeResponse;
import com.archer.net.http.multipart.FormData;
import com.archer.rs.util.SM4Util;

public class UploadTest {

    public static void main( String[] args )
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
    	System.out.println(new String(res.getBody()));
    	
    }

}
