package com.archer.rs;

import com.archer.net.ChannelContext;

class ArcherCallback {
	
	protected static final long TIMEOUT = 3000;
	
	private Object lock = new Object();
	
	private int bodySize = 0;
	private ArcherMessageType clientType;
	protected byte[] nonce;
	protected byte[] key;
	
	private int valueSize = 0;
	private int valueOff = 0;
	protected byte[] value;
	
	public ArcherCallback(ArcherMessageType clientType, byte[] nonce) {
		this.clientType = clientType;
		this.nonce = nonce;
	}
	
	protected void parse(ChannelContext ctx) {
		if(value == null) {
			if(clientType == ArcherMessageType.CLIENT_GET_TYPE) {
				int keyLen = ctx.channel().readInt16();
				key = ctx.read(keyLen);
				valueSize = bodySize - 2  - keyLen;
				value = new byte[valueSize];
			}
		}
		while(ctx.readableSize() > 0 && valueOff < valueSize) {
			int reads = ctx.read(value, valueOff, valueSize - valueOff);
			valueOff += reads;
		}
		if(valueOff == valueSize) {
			unlock();
		}
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
	
	protected void setBodySize(int size) {
		this.bodySize = size;
	}
	
	protected int bodySize() {
		return this.bodySize;
	}
}
