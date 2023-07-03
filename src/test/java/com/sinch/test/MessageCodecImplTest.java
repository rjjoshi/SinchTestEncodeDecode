package com.sinch.test;

import java.io.IOException;
import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sinch.messaging.Message;
import com.sinch.messaging.MessageCodec;
import com.sinch.messaging.MessageCodecImpl;

public class MessageCodecImplTest {

	@Test
	public void testEncodeAndDecode() throws IOException {
		Message realMessage = new Message();
		realMessage.headers = new HashMap<>();
		realMessage.headers.put("Header1", "Value1");
		realMessage.headers.put("Header2", "Value2");
		realMessage.payload = "My Payload".getBytes();
		MessageCodec codec = new MessageCodecImpl();
		byte[] encodedData = codec.encode(realMessage);
		Message decodedMessage = codec.decode(encodedData);
		Assertions.assertEquals(realMessage.headers, decodedMessage.headers);
		Assertions.assertArrayEquals(realMessage.payload, decodedMessage.payload);
	}
	
	@Test
	public void testEncodeAndDecodeWithEmptyHeader() throws IOException {
		Message realMessage = new Message();
		realMessage.headers = new HashMap<>();
		realMessage.payload = "My Payload".getBytes();
		MessageCodec codec = new MessageCodecImpl();
		byte[] encodedData = codec.encode(realMessage);
		Message decodedMessage = codec.decode(encodedData);
		Assertions.assertEquals(realMessage.headers, decodedMessage.headers);
		Assertions.assertArrayEquals(realMessage.payload, decodedMessage.payload);
	}

	@Test
	public void testEncodeWithInvalidMessage() {
		Message invalidMessage = new Message();
		invalidMessage.headers = null;
		invalidMessage.payload = "Invalid Payload".getBytes();
		MessageCodec codec = new MessageCodecImpl();
		Assertions.assertThrows(IllegalArgumentException.class, () -> codec.encode(invalidMessage));
	}

	@Test
	public void testEncodeWithLargePayload() {
		byte[] largePayload = new byte[300000];
		Message message = new Message();
		message.headers = new HashMap<>();
		message.headers.put("Header", "Value");
		message.payload = largePayload;
		MessageCodec codec = new MessageCodecImpl();
		Assertions.assertThrows(IllegalArgumentException.class, () -> codec.encode(message));
	}

	@Test
	public void testDecodeNullData() throws IOException {
		MessageCodecImpl codec = new MessageCodecImpl();
		byte[] invalidData = null;
		Assertions.assertThrows(IllegalArgumentException.class, () -> codec.decode(invalidData));
	}
		@Test
	public void testValidKeyAndData() throws IllegalArgumentException, IOException {
		byte[] encodedData = new byte[] {
				// Signature
				'S', 'i', 'n', 'c', 'h', 'E', 'n', 'c', 'o', 'd', 'i', 'n', 'g',
				// Number of headers
				1,
				// Header 1: Name length, Name, Value length, Value
				4, 'H', 'e', 'a', 'd', 5, 'V', 'a', 'l', 'u', 'e',
				// Payload
				1, 2, 3, 4 };

		MessageCodecImpl codec = new MessageCodecImpl();
		Message decodedMessage = codec.decode(encodedData);
		assert decodedMessage.headers.size() == 1;
		assert decodedMessage.headers.get("Head").equals("Value");
		assert decodedMessage.payload.length == 4;
		assert decodedMessage.payload[0] == 1;
		assert decodedMessage.payload[1] == 2;
		assert decodedMessage.payload[2] == 3;
		assert decodedMessage.payload[3] == 4;
	}

	@Test
	public void testInvalidKey() {

		byte[] encodedData = new byte[] {
				// Invalid signature
				'I', 'n', 'v', 'a', 'l', 'i', 'd', 'S', 'i', 'g', 'n', 'a', 't', 'u', 'r', 'e',
				// Number of headers
				1,
				// Header 1: Name length, Name, Value length, Value
				4, 'H', 'e', 'a', 'd', 5, 'V', 'a', 'l', 'u', 'e',
				// Payload
				1, 2, 3, 4 };

		MessageCodecImpl codec = new MessageCodecImpl();
		Assertions.assertThrows(IllegalArgumentException.class, () -> codec.decode(encodedData));

	}

}
