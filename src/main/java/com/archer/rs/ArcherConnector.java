package com.archer.rs;

import java.util.Random;

import com.archer.net.Bytes;
import com.archer.net.Channel;
import com.archer.net.ChannelContext;
import com.archer.net.HandlerList;
import com.archer.net.handler.Handler;
import com.archer.rs.util.SM4Util;

class ArcherConnector implements Handler  {
	
	private static final byte[] MAGIC = {'9', '6', '0', '7'};
	
	private String host;
	private int port;
	private Channel channel;
	private ChannelContext ctx;
	private volatile boolean connecting = false;
	private volatile int dataSize = 0;
	
	private Random r = new Random();
	private byte[] authKey;
	
	private Intersection inc = new Intersection();

	private Object lock = new Object();
	
	
	public ArcherConnector(String host, int port, byte[] authKey) {
		this.host = host;
		this.port = port;
		this.authKey = authKey;
		
		this.channel = new Channel();
		HandlerList handlers = new HandlerList();
		handlers.add(this);
		this.channel.handlerList(handlers);
	}
	
	protected void sendSave(byte[] key, byte[] value) {
		sendData(ArcherMessageType.CLIENT_SAVE_TYPE, key, value);
	}
	
	protected byte[] sendGet(byte[] key) {
		return sendData(ArcherMessageType.CLIENT_GET_TYPE, key, null);
	}
	
	@Override
	public void onAccept(ChannelContext ctx) {}

	@Override
	public void onConnect(ChannelContext ctx) {
		this.ctx = ctx;
		synchronized(lock) {
			lock.notifyAll();
		}
	}

	@Override
	public void onDisconnect(ChannelContext ctx) {
		this.ctx = null;
		connecting = false;
	}

	@Override
	public void onError(ChannelContext ctx, Throwable e) {
		e.printStackTrace();
	}

	//head_len = 4 + 16 + 32 = 52
	//  magic        nonce        sig        msgType     keyLen          key             data
	//    4     +     16     +     32     +     1     +     2     +     keyLen     +     data
	// 9,6,0,7  
	@Override
	public void onRead(ChannelContext ctx) {
		if(dataSize == 0) {
			dataSize = ctx.readInt32();
		}
		if(dataSize < 0) {
			dataSize = 0;
			return ;
		}
		if(ctx.readableSize() < dataSize) {
			return ;
		}
		byte[] databs = ctx.read(dataSize);
		if(databs.length != dataSize) {
			throw new ArcherException("Can not parse remote data. Expeted length = " + dataSize + ", receive len = " + databs.length);
		}
		parseData(new Bytes(databs));
		dataSize = 0;
	}

	@Override
	public void onSslCertificate(ChannelContext ctx, byte[] arg1) {}

	@Override
	public void onWrite(ChannelContext ctx, byte[] out) {
		ctx.toLastOnWrite(out);
	}
	

	//head_len = 4 + 16 + 32 = 52
	//  magic        nonce        sig        msgType     keyLen          key             data
	//    4     +     16     +     32     +     1     +     2     +     keyLen     +     data
	// 9,6,0,7  
	private void parseData(Bytes data) {
		byte[] magic = data.read(4);
		for(int i = 0; i < 4; i++) {
			if(MAGIC[i] != magic[i]) {
				throw new ArcherException("Invalid protocol.");
			}
		}
		byte[] nonce = data.read(16);
		data.read(32); //signature

		ArcherMessageType type = ArcherMessageType.from(data.readInt8());
		ArcherCallback cb = inc.findCallback(nonce);
		try {
			if(type == ArcherMessageType.SERVER_OK_TYPE) {
				cb.parse(data);
			} else if(type == ArcherMessageType.SERVER_FAIL_TYPE) {
				throw new ArcherException("Server failed.");
			} else {
				throw new ArcherException("Invalid type.");
			}
		} finally {
			cb.unlock();
		}
	}
	
	//head_len = 4 + 16 + 32 = 52
	//  magic        nonce        sig        msgType     keyLen          key             data
	//    4     +     16     +     32     +     1     +     2     +     keyLen     +     data
	// 9,6,0,7  
	private byte[] sendData(ArcherMessageType type, byte[] key, byte[] value) {
		if(!connecting && !this.channel.isActive()) {
			connecting = true;
			this.channel.connect(host, port);
			waitForConnected();
		}
		int totalLen = 52 + 1 + 2 + key.length + value.length;
		Bytes data = new Bytes(4 + totalLen);
		data.writeInt32(totalLen);
		data.write(MAGIC);
		byte[] nonce = new byte[16];
		r.nextBytes(nonce);
		data.write(nonce);
		byte[] cipher = SM4Util.encrypt(nonce, authKey);
		data.write(cipher);

		data.writeInt8(type.getType());
		data.writeInt16(key.length);
		data.write(key);
		if(type == ArcherMessageType.CLIENT_SAVE_TYPE && value != null) {
			data.write(value);
		}
		ArcherCallback cb = new ArcherCallback(type, nonce);
		inc.saveCallback(nonce, cb);
		
		onWrite(ctx, data.array());
		cb.lock();
		return cb.value;
	}
	
	private void waitForConnected() {
		long s = System.currentTimeMillis();
		synchronized(lock) {
			try {
				lock.wait(ArcherCallback.TIMEOUT);
			} catch (InterruptedException e) {
				throw new ArcherException(e);
			}
		}
		long e = System.currentTimeMillis();
		if(e - s >= ArcherCallback.TIMEOUT) {
			throw new ArcherException("Connected timeout.");
		}
	}
}
