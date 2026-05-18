package com.archer.rs;

import com.archer.net.Bytes;

class ArcherCallback {
	
	protected static final long TIMEOUT = 3000;
	
	private Object lock = new Object();
	
	private ArcherMessageType clientType;
	protected byte[] nonce;
	protected byte[] key;
	protected byte[] value;
	
	public ArcherCallback(ArcherMessageType clientType, byte[] nonce) {
		this.clientType = clientType;
		this.nonce = nonce;
	}
	
	protected void parse(Bytes data) {
		if(clientType == ArcherMessageType.CLIENT_GET_TYPE) {
			int keyLen = data.readInt16();
			key = data.read(keyLen);
			value = data.readAll();
		}
		unlock();
		 
	}
	
	protected void lock() {
		long s = System.currentTimeMillis();
		synchronized(lock) {
			try {
				lock.wait(TIMEOUT);
			} catch (InterruptedException e) {
				throw new ArcherException(e);
			}
		}
		long e = System.currentTimeMillis();
		if(e - s >= TIMEOUT) {
			throw new ArcherException("Wait for response timeout.");
		}
	}
	
	protected void unlock() {
		synchronized(lock) {
			lock.notifyAll();
		}
	}
}
