package com.archer.rs;

class Intersection {
	
	private byte[][]  nonces;
	private ArcherCallback[] cbs;
	
	private Object lock = new Object();
	
	public Intersection() {
		nonces = new byte[1024][];
		cbs = new ArcherCallback[1024];
	}
	
	public void saveCallback(byte[] nonce, ArcherCallback cb) {
		if(nonce.length != 16) {
			throw new ArcherException("Invalid nonce");
		}
		synchronized(lock) {
			for(int i = 0; i < cbs.length; i++) {
				if(cbs[i] == null) {
					nonces[i] = nonce;
					cbs[i] = cb;
					return ;
				}
			}
			int len = nonces.length;
			byte[][] tmpnonces = new byte[nonces.length * 2][];
			ArcherCallback[] tmpcbs = new ArcherCallback[nonces.length * 2];
			System.arraycopy(nonces, 0, tmpnonces, 0, nonces.length);
			System.arraycopy(cbs, 0, tmpcbs, 0, nonces.length);
			nonces = tmpnonces;
			cbs = tmpcbs;
			nonces[len] = nonce;
			cbs[len] = cb;
		}
	}
	
	public ArcherCallback findCallback(byte[] nonce) {
		if(nonce.length != 16) {
			throw new ArcherException("Invalid nonce");
		}
		for(int i = 0; i < cbs.length; i++) {
			boolean match = true;
			if(cbs[i] != null) {
				for(int j = 0; j < 16; j++) {
					if(nonces[i][j] != nonce[j]) {
						match = false;
						break;
					}
				}
			}
			if(cbs[i] != null && match) {
				synchronized(lock) {
					ArcherCallback ret = cbs[i];
					nonces[i] = null;
					cbs[i] = null;
					return ret;
				}
			}
		}
		return null;
	}
}