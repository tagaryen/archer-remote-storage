package com.archer.rs;

enum ArcherMessageType {

	// 1  ~  32   for   errors
	ERROR_HEAD_TYPE(1),
	ERROR_TYPE_TYPE(2),
	ERROR_BODY_TYPE(3),
	// 33 ~  64   for   client
	CLIENT_GET_TYPE(33),
	CLIENT_SAVE_TYPE(34),
	// 65 ~  96   for   server
	SERVER_OK_TYPE(65),
	SERVER_FAIL_TYPE(66);
	
	private int type;
	
	ArcherMessageType(int type) {
		this.type = type;
	}
	
	public int getType() {
		return type;
	}

	public static ArcherMessageType from(int t) {
		for(ArcherMessageType type: values()) {
			if(type.type == t) {
				return type;
			}
		}
		throw new ArcherException("Invalid message type [" + t + "]");
	}
}
