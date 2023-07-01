package com.sinch;

import java.io.IOException;
import java.util.HashMap;

import com.sinch.messaging.Message;
import com.sinch.messaging.MessageCodec;
import com.sinch.messaging.MessageCodecImpl;

public class BinaryMessageCodecApp {
	public static void main(String[] args) {
		MessageCodec codec = new MessageCodecImpl();
		try {
			Message message = new Message();
			message.headers = new HashMap<>();
			message.headers.put("Header1", "Value1");
			message.headers.put("Header2", "Value2");
			message.payload = "Hello, Hello!".getBytes();
			byte[] encodedData = codec.encode(message);
			Message decodedMessage = codec.decode(encodedData);
			System.out.println(new String(decodedMessage.payload));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
