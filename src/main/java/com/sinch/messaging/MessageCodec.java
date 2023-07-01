package com.sinch.messaging;

import java.io.IOException;

public interface MessageCodec {
	byte[] encode(Message message) throws IllegalArgumentException, IOException;
	Message decode(byte[] data) throws IllegalArgumentException, IOException;
}
