
# Objective

Design and implement a simple binary message encoding scheme to be used in a signaling protocol. It will be used primarily for passing messages
between peers in a real-time communication application.
For the purpose of this task we assume a simple message model:
* A message can contain a variable number of headers, and a binary payload.
* The headers are name-value pairs, where both names and values are ASCII-encoded strings.
* Header names and values are limited to 1023 bytes (independently).
* A message can have max 63 headers.
* The message payload is limited to 256 KiB.

# Java Project for Simple Binary Message Encoding and Decoding
A Java command line Application developed with Java(Ver. 8), Junit 5 and Maven.
The **MessageCodecImpl** class is responsible for encoding and decoding messages. It implements the **MessageCodec** interface.
The MessageCodec interface defines two methods:</br>
encode: Encodes a Message object into a byte array.</br>
decode: Decodes a byte array into a Message object.</br>
The **Message** class represents a message and consists of a map of headers and a byte array for the payload. 

**Method encode and decode**

**encode :** This method encodes a Message object into a byte array. It first validates the message using the validateMessage method. Then, it writes the encoding signature, the number of headers, the headers itself, and finally the payload to a ByteArrayOutputStream. The resulting byte array is returned.

**decode :** This method decodes a byte array into a Message object. It first validates the data using the validateData method and checks if the data has been encoded with MessageCodecImpl encode method by comparing the encoding signature. Then, it reads the number of headers and decodes each header name and value from the input stream. Finally, it reads the payload from the input stream and creates a new Message object with the decoded headers and payload.

**Test cases** can be found under src/test/java

**How to Run the Tests**

* Open the command line/console inside folder message-encoder-decoder
* Run the test case using command : **mvn test**

**Instructions for Build and runs**
* Open the command line/console inside folder message-encoder-decoder
* Build the project using command : **mvn clean install**
* Maven will create a target folder and put jar inside it with name message-encoder-decoder-0.0.1-SNAPSHOT.jar
* Now run the application using below command
**java -jar target/message-encoder-decoder-0.0.1-SNAPSHOT.jar**

**Assumptions**
As only upper limit of Headers is mentioned in Problem decscription. I assume that Empty Header is valid but it should not be null. But Message should have header and payload both inside it. 
