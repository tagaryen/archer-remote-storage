package com.archer.rs;

public class RSSignature {

	private String signature;
	private String nonce;
	
	public RSSignature(String signature, String nonce) {
		this.signature = signature;
		this.nonce = nonce;
	}
	
	public String getSignature() {
		return signature;
	}
	
	public String getNonce() {
		return nonce;
	}
	
}
