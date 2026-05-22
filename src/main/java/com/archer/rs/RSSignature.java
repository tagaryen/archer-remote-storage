package com.archer.rs;

public class RSSignature {

	private String signature;
	private String t;
	
	public RSSignature(String signature, String t) {
		this.signature = signature;
		this.t = t;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public String getT() {
		return t;
	}
	
}
