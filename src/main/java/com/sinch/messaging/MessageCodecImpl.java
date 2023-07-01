package com.sinch.messaging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessageCodecImpl implements MessageCodec {
	private static final int MAX_HEADER_SIZE = 1023;
	private static final int MAX_NUM_HEADERS = 63;
	private static final int MAX_PAYLOAD_SIZE = 262144; // 256*1024 (kib)
	private static final String KEY="SinchEncoding";

	public byte[] encode(Message message) throws IllegalArgumentException, IOException {
		validateMessage(message);
		try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
			outStream.write(KEY.getBytes(StandardCharsets.US_ASCII));
			outStream.write(message.headers.size());
			encodeHeaders(outStream, message.headers);
			outStream.write(message.payload);
			return outStream.toByteArray();
		} catch (IOException e) {
			throw new IOException("Error occurred while encoding the message.", e);
		}

	}

	private void encodeHeaders(ByteArrayOutputStream outStream, Map<String, String> headers) throws IOException {
		headers.forEach((headerName, headerValue) -> {
			validateHeaderValues(headerName);
			validateHeaderValues(headerValue);

			byte[] headerNameBytes = headerName.getBytes(StandardCharsets.US_ASCII);
			byte[] headerValueBytes = headerValue.getBytes(StandardCharsets.US_ASCII);

			try {
				outStream.write(headerNameBytes.length);
				outStream.write(headerNameBytes);
				outStream.write(headerValueBytes.length);
				outStream.write(headerValueBytes);
			} catch (IOException e) {
				throw new RuntimeException("Error occurred while writing headers.", e);
			}
		});
	}

	public Message decode(byte[] data) throws IllegalArgumentException, IOException {
		validateData(data);
		String signature = new String(data, 0, KEY.length(), StandardCharsets.US_ASCII);
	    if (!signature.equals("SinchEncoding")) {
	        throw new IllegalArgumentException("Invalid data. Not encoded using this codec.");
	    }
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(data)) {
		    inputStream.skip(KEY.length());
			Message message = new Message();
			int headerCount = inputStream.read();
			message.headers = new HashMap<>();

			for (int i = 0; i < headerCount; i++) {
				int headerNameLength = inputStream.read();
				byte[] headerNameBytes = new byte[headerNameLength];
				inputStream.read(headerNameBytes);
				String headerName = new String(headerNameBytes, StandardCharsets.US_ASCII);
                validateHeaderValues(headerName);
				int headerValueLength = inputStream.read();
				byte[] headerValueBytes = new byte[headerValueLength];
				inputStream.read(headerValueBytes);
				String headerValue = new String(headerValueBytes, StandardCharsets.US_ASCII);
				message.headers.put(headerName, headerValue);
				validateHeaderValues(headerName);
			}

			byte[] payload = new byte[inputStream.available()];
			inputStream.read(payload);
			message.payload = payload;
            validateMessage(message);
			return message;
		} catch (IOException e) {
			throw new IOException("Error occurred while decoding the message.", e);
		}
	}

	private void validateData(byte[] data) {
		if (data == null || data.length == 0) {

			throw new IllegalArgumentException("Invalid data");
		}

	}

	private void validateMessage(Message message) {
		if (message == null || message.headers == null || message.payload == null) {
			throw new IllegalArgumentException("This message is invalid.");
		}
		if (message.headers.size() > MAX_NUM_HEADERS) {
			throw new IllegalArgumentException("Number of headers exeeds the limit.");
		}
		if (message.payload.length > MAX_PAYLOAD_SIZE) {
			throw new IllegalArgumentException("Payload size exceeds the limit.");
		}
	}

	private void validateHeaderValues(String value) {
		if (value == null || value.isEmpty() || value.length() > MAX_HEADER_SIZE) {
			throw new IllegalArgumentException("Invalid Header." + value);
		}

	}
}
