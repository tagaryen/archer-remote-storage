package com.archer.rs;

import java.nio.charset.StandardCharsets;

import com.archer.xjson.JavaTypeRef;
import com.archer.xjson.XJSON;

public class ArcherRSClient {
	
	private ArcherConnector connector;
	private XJSON xjson = new XJSON();
	
	public ArcherRSClient(String host, int port, byte[] authKey) {
		this.connector = new ArcherConnector(host, port, authKey);
	}
	
	public void save(String key, Object data) {
		saveString(key, xjson.stringify(data));
	}

	public void saveString(String key, String data) {
		connector.sendSave(key.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8));
	}
	public <T> T get(String key, JavaTypeRef<T> ref) {
		String value = getString(key);
		return xjson.parse(value, ref);
	}
	public <T> T get(String key, Class<T> cls) {
		String value = getString(key);
		return xjson.parse(value, cls);
	}
	public String getString(String key) {
		return new String(connector.sendGet(key.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
	}	
}
