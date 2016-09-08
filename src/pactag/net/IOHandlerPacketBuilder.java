package pactag.net;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;

public class IOHandlerPacketBuilder {

	private ByteArrayOutputStream data;
	private ArrayList<byte[]> dataListBuilder;
	private final CharsetEncoder encoder;
	private ArrayList<Character> typeListBuilder;

	public IOHandlerPacketBuilder() {
		if (!Charset.isSupported("UTF-8"))
			throw new RuntimeException("UTF-8 Charset not supported, can not encode!");
		typeListBuilder = new ArrayList<>();
		dataListBuilder = new ArrayList<>();
		data = new ByteArrayOutputStream();
		encoder = Charset.forName("UTF-8").newEncoder();
	}

	public IOHandlerPacketBuilder addBytes(byte[] data) {
		try {
			this.data.write(data);
			typeListBuilder.add(Character.valueOf('B'));
			dataListBuilder.add(this.data.toByteArray());
			this.data.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public IOHandlerPacketBuilder addBytes(byte[] data, int off, int len) {
		this.data.write(data, off, len);
		typeListBuilder.add(Character.valueOf('B'));
		dataListBuilder.add(this.data.toByteArray());
		this.data.reset();
		return this;
	}

	public IOHandlerPacketBuilder addCustom(byte[] data, char type) {
		try {
			this.data.write(data);
			typeListBuilder.add(Character.valueOf(type));
			dataListBuilder.add(this.data.toByteArray());
			this.data.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public IOHandlerPacketBuilder addDouble(double value) {
		dataListBuilder.add(ByteBuffer.allocate(8).putDouble(value).array());
		typeListBuilder.add(Character.valueOf('D'));
		return this;
	}
	
	public IOHandlerPacketBuilder addFloat(float value) {
		dataListBuilder.add(ByteBuffer.allocate(4).putFloat(value).array());
		typeListBuilder.add(Character.valueOf('F'));
		return this;
	}
	
	public IOHandlerPacketBuilder addInt(int value) {
		dataListBuilder.add(ByteBuffer.allocate(4).putInt(value).array());
		typeListBuilder.add(Character.valueOf('I'));
		return this;
	}
	
	public IOHandlerPacketBuilder addLong(long value) {
		dataListBuilder.add(ByteBuffer.allocate(8).putLong(value).array());
		typeListBuilder.add(Character.valueOf('L'));
		return this;
	}
	
	public IOHandlerPacketBuilder addShort(short value) {
		dataListBuilder.add(ByteBuffer.allocate(2).putShort(value).array());
		typeListBuilder.add(Character.valueOf('S'));
		return this;
	}

	public IOHandlerPacketBuilder addString(String line) {
		ByteBuffer buf;
		try {
			buf = encoder.encode(CharBuffer.wrap(line.toCharArray()));
		} catch (CharacterCodingException e) {
			e.printStackTrace();
			return this;
		}
		byte[] data;
		buf.get(data = new byte[buf.remaining()]);
		try {
			this.data.write(data);
			typeListBuilder.add(Character.valueOf('C'));
			dataListBuilder.add(this.data.toByteArray());
			this.data.reset();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public IOHandlerPacket build() {
		char[] types = new char[typeListBuilder.size()];
		for (int i = 0; i < types.length; i++)
			types[i] = typeListBuilder.get(i).charValue();
		byte[][] data = new byte[dataListBuilder.size()][];
		dataListBuilder.toArray(data);
		int[] lengths = new int[data.length];
		for (int i = 0 ; i < data.length; i++)
			lengths[i] = data[i].length;
		return new IOHandlerPacket(new IOHandlerPacketStructure(types, lengths), data, 0);
	}

	public void clear() {
		data.reset();
		typeListBuilder.clear();
		dataListBuilder.clear();
	}
}
